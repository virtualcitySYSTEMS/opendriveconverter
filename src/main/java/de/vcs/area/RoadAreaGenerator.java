package de.vcs.area;

import de.vcs.adapter.geometry.PolynomAdapter;
import de.vcs.datatypes.LaneParameter;
import de.vcs.datatypes.LaneSectionParameter;
import de.vcs.model.odr.geometry.Line;
import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ParamPolynomHelper;
import de.vcs.utils.math.PolynomHelper;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.xmlobjects.gml.model.geometry.primitives.LineString;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class RoadAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;
    ArrayList<LaneSectionParameter> laneSectionParameters;
    ArrayList<Double> sRunner;

    public RoadAreaGenerator(Road road) {
        this.road = road;
        init();
    }

    @Override
    public void generateArea() {
        applySRunner();
    }

    private void applySRunner() {
        sRunner.forEach(s -> {
            LaneSectionParameter lsp = new LaneSectionParameter();
            //global
            ParamPolynom ppoly = (ParamPolynom) road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
            Point uvpoint = ParamPolynomHelper.calcUVPoint(s, ppoly);
            uvpoint = (Point) Transformation.transform(uvpoint, ppoly.getIntertialTransform().getHdg(),
                    ppoly.getInertialReference().getPos().getValue().get(0),
                    ppoly.getInertialReference().getPos().getValue().get(1));
            Point nvpoint = ParamPolynomHelper.calcNormalVector(s, uvpoint, ppoly);
            nvpoint = (Point) Transformation.transform(nvpoint, ppoly.getIntertialTransform().getHdg(),
                    ppoly.getInertialReference().getPos().getValue().get(0),
                    ppoly.getInertialReference().getPos().getValue().get(1));
            Point offsetPoint = calcLaneOffsetPoints(s, uvpoint, nvpoint);
            LaneSection ls = road.getLanes().getLaneSections().floorEntry(s).getValue();
            lsp.setLaneParameters(calcLaneWidths(s, uvpoint, nvpoint, ls));
            lsp.setRefLinePoint(uvpoint);
            lsp.setAbsolutS(s);
            lsp.setLaneOffsetPoint(offsetPoint);
        });
    }

    private TreeMap<Integer, LaneParameter> calcLaneWidths(double ds, Point uvpoint, Point nvpoint, LaneSection ls) {
        TreeMap<Integer, LaneParameter> map = new TreeMap<>();
        ls.getLeftLanes().forEach(ll -> {
            Polynom poly = ll.getWidths().floorEntry(ds - ls.getLinearReference().getS()).getValue();
            double width = PolynomHelper
                    .calcPolynomValue(ds - ls.getLinearReference().getS() - poly.getStTransform().getsOffset(), poly);
            LaneParameter lp = new LaneParameter();
            lp.setWidth(width);
            map.put(ll.getId(), lp);
        });
        return map;
    }

    private Point calcLaneOffsetPoints(double ds, Point uvpoint, Point nvpoint) {
        Polynom poly = road.getLanes().getLaneOffsets().floorEntry(ds).getValue();
        double width = PolynomHelper.calcPolynomValue(ds, poly);
        Point p = ParamPolynomHelper.calcUVPointPerpendicularToCurve(ds, width, uvpoint, nvpoint);
        p = (Point) Transformation.transform(p, poly.getIntertialTransform().getHdg(),
                poly.getInertialReference().getPos().getValue().get(0),
                poly.getInertialReference().getPos().getValue().get(1));
        return p;
    }

    private void init() {
        laneSectionParameters = new ArrayList<>();
        sRunner = Discretisation.generateSRunner(2.0, road.getLength());
    }
}

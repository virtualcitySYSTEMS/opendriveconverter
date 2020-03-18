package de.vcs.area;

import de.vcs.datatypes.LaneParameter;
import de.vcs.datatypes.LaneSectionParameter;
import de.vcs.model.odr.geometry.Line;
import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ParamPolynomHelper;
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
        calcLanes();
    }

    //TODO check if ParamPolynomHelper method just receives s and calculates ds in method
    private void calcLanes() {
        sRunner.forEach(s -> {
        });
    }

    private Point calcRefPoint(double s) {
        ParamPolynom poly = (ParamPolynom) road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
        //create uv point
        Point p =
                ParamPolynomHelper.calcUVPointPerpendicularToCurve(s - poly.getLinearReference().getS(), 0.0, poly);
        //transform to xy point
        p = (Point) Transformation.transform(p, poly.getIntertialTransform().getHdg(),
                poly.getInertialReference().getPos().getValue().get(0),
                poly.getInertialReference().getPos().getValue().get(1));
        return p;
    }

    private Map<Integer, LaneParameter> calcLanesLane(double s) {
        TreeMap<Integer, LaneParameter> laneParameters = new TreeMap<Integer, LaneParameter>();
        return laneParameters;
    }

    private void init() {
        laneSectionParameters = new ArrayList<>();
        sRunner = Discretisation.generateSRunner(2.0, road.getLength());
    }
}

package de.vcs.area;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.LaneParameter;
import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.math.PolynomHelper;
import de.vcs.utils.transformation.PointFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;

public class RoadAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;
    ArrayList<LaneSectionParameters> laneSectionParameters;
    ArrayList<Double> sRunner;
    PointFactory pointFactory;

    public RoadAreaGenerator(Road road) {
        this.road = road;
        pointFactory = new PointFactory();
    }

    @Override
    public void generateArea() {
        applySRunner();
    }

    private void applySRunner() {
        road.getLanes().getLaneSections().keySet().forEach(key -> {
            double sStart = key;
            double sEnd;
            if (road.getLanes().getLaneSections().size() <= 1 | key == road.getLanes().getLaneSections().lastKey()) {
                sEnd = road.getLength();
            } else {
                try {
                    sEnd = road.getLanes().getLaneSections().ceilingKey(key + 0.01);
                } catch (Exception e) {
                    sEnd = sStart;
                }
            }
            if (sStart != sEnd) {
                LaneSection ls = road.getLanes().getLaneSections().get(key);
                LaneSectionParameters lsp = new LaneSectionParameters();
                ArrayList<Double> sPositions = Discretisation.generateSRunner(1.0, sEnd - sStart);
                sPositions.forEach(s -> {
                    fillCenterLine(sStart + s, lsp, true);
                });
                //ls.getCenterLane().getGmlGeometries()
                // .add(ODRGeometryFactory.create(JTSConstants.LINESTRING, lsp.getLanes().get(0)));
            }
        });
    }


    private void fillCenterLine(Double s, LaneSectionParameters lsp, boolean applyLaneOffset) {
        AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
        double offset = 0.0;
        if (applyLaneOffset) {
            if (road.getLanes().getLaneOffsets().isEmpty()) {
                offset = 0.0;
            } else {
                double localS = road.getLanes().getLaneOffsets().floorEntry(s).getKey();
                offset = PolynomHelper
                        .calcPolynomValue(road.getLanes().getLaneOffsets().floorEntry(s).getValue(), s - localS);
            }
        }
        if (lsp.getLanes().get(0) == null) {
            lsp.getLanes().put(0, new ArrayList<LaneParameter>());
        }
        lsp.getLanes().get(0).add();
        pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, s, offset));
    }

    private void fillLanes(Double s, LaneSectionParameters lsp, LaneSection ls) {
        double localS = s - ls.getLinearReference().getS();
        ls.getRightLanes().forEach((key, value) -> {
            Polynom poly = value.getWidths().floorEntry(localS).getValue();
            double polyS = localS - poly.getLinearReference().getS();
            double width = PolynomHelper.calcPolynomValue(poly, polyS);
            if (lsp.getLanes().get(key) == null) {
                lsp.getLanes().put(key, new ArrayList<LaneParameter>());
            }
            lsp.getLanes().get(key).add()
        });
    }
}

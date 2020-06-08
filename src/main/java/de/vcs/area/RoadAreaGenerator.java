package de.vcs.area;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.LaneParameter;
import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ParamPolynomHelper;
import de.vcs.utils.math.PolynomHelper;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.TreeMap;

public class RoadAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;
    ArrayList<LaneSectionParameters> laneSectionParameters;
    ArrayList<Double> sRunner;

    public RoadAreaGenerator(Road road) {
        this.road = road;
    }

    @Override
    public void generateArea() {
        applySRunner();
    }

    private void applySRunner() {
        road.getLanes().getLaneSections().keySet().forEach(key -> {

            double sStart = key;
            double sEnd;
            if (road.getLanes().getLaneSections().size() <= 1) {
                sEnd = road.getLength();
            } else {
                try {
                    sEnd = road.getLanes().getLaneSections().ceilingKey(key + 0.01);
                } catch (Exception e) {
                    sEnd = sStart;
                }
            }
            System.out.println(key + " Start:" + sStart + " End: " + sEnd);
            if (sStart != sEnd) {
                LaneSection ls = road.getLanes().getLaneSections().get(key);
                LaneSectionParameters lsp = new LaneSectionParameters();
                ArrayList<Double> sPositions = Discretisation.generateSRunner(2.0, sEnd - sStart);
                sPositions.forEach(s -> {
                    lsp.getCenterLine().add(fillCenterLine(sStart + s, ls, road));
                });
                ls.getCenterLane().getGmlGeometries().add(ODRGeometryFactory.create(JTSConstants.LINESTRING, lsp.getCenterLine()));

            }

        });
    }

    /**
     * Return centerline point for given s-position. Includes lane offset.
     *
     * @param s    global s-position of road
     * @param ls   LaneSection
     * @param road Road
     * @return The Point of center line at givern s-position
     */
    private Point fillCenterLine(Double s, LaneSection ls, Road road) {
        double offset;
        if (road.getLanes().getLaneOffsets().isEmpty()) {
            offset = 0.0;
        } else {
            double localS = road.getLanes().getLaneOffsets().floorEntry(s).getKey();
            offset = PolynomHelper.calcPolynomValue(s - localS, road.getLanes().getLaneOffsets().floorEntry(s).getValue());
        }
        return Transformation.st2xyPoint(road, s, offset);
    }

}

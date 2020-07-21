package de.vcs.area;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
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
                ArrayList<Double> sPositions = Discretisation.generateSRunner(2.0, sEnd - sStart);
                sPositions.forEach(s -> {
                    lsp.getCenterLine().add(fillCenterLine(sStart + s, ls));
                });
                ls.getCenterLane().getGmlGeometries()
                        .add(ODRGeometryFactory.create(JTSConstants.LINESTRING, lsp.getCenterLine()));
            }
        });
    }

    /**
     * Return centerline point for given s-position. Includes lane offset.
     *
     * @param s  global s-position of road
     * @param ls LaneSection
     * @return The Point of center line at givern s-position
     */
    private Point fillCenterLine(Double s, LaneSection ls) {
        AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
        double offset;
        if (road.getLanes().getLaneOffsets().isEmpty()) {
            offset = 0.0;
        } else {
            double localS = road.getLanes().getLaneOffsets().floorEntry(s).getKey();
            offset = PolynomHelper
                    .calcPolynomValue(road.getLanes().getLaneOffsets().floorEntry(s).getValue(), s - localS);
        }
        return pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, s, offset);
    }


}

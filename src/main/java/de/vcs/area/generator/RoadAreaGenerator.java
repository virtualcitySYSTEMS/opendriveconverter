package de.vcs.area.generator;

import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.datatypes.RoadParameters;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.AbstractSTGeometry;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.lane.LaneHelper;
import de.vcs.utils.lanesection.LaneSectionHelper;
import de.vcs.utils.log.ODRLogger;
import de.vcs.utils.road.RoadHelper;
import de.vcs.utils.roadmark.RoadMarkHelper;
import de.vcs.utils.transformation.PointFactory;
import org.locationtech.jts.geom.*;

import java.util.*;

public class RoadAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;
    ArrayList<LaneSectionParameters> laneSectionParameters;
    ArrayList<Double> sRunner;
    double step = 0.2;
    PointFactory pointFactory;
    GeometryFactory factory;
    RoadParameters rp;

    public RoadAreaGenerator(Road road) {
        this.road = road;
        rp = new RoadParameters();
        pointFactory = new PointFactory();
        factory = new GeometryFactory();
    }

    @Override
    public void generateArea() {
        //validateRoadGeometry();
        applySRunner();
        RoadHelper.createRoadPolygons(road, rp);
    }

    private void validateRoadGeometry() {
        for (Map.Entry<Double, AbstractODRGeometry> entry : road.getPlanView().getOdrGeometries().entrySet()) {
            Double s = entry.getKey();
            AbstractODRGeometry g = entry.getValue();
            AbstractSTGeometry geom = (AbstractSTGeometry) g;
            Point endPoint = pointFactory.getODRGeometryHandler(geom.getClass())
                    .sth2xyzPoint(geom, s + geom.getLength(), 0.0, 0.0);
            if (road.getPlanView().getOdrGeometries().higherEntry(s) != null) {
                AbstractSTGeometry next =
                        (AbstractSTGeometry) road.getPlanView().getOdrGeometries().higherEntry(s).getValue();
                double X = next.getInertialReference().getPos().getValue().get(0);
                double Y = next.getInertialReference().getPos().getValue().get(1);
                double dx = endPoint.getX() - X;
                double dy = endPoint.getY() - Y;
                if (Math.abs(dx) > 0.05 || Math.abs(dy) > 0.05) {
                    ODRLogger.getInstance().warn("Planview of Road " + road.getId() + " is discontinuous at s = " +
                                                 next.getLinearReference().getS() + " ! dx = " + dx + "; dy = " + dy +
                                                 "; length = " + geom.getLength());
                } else {
                    ODRLogger.getInstance().debug("Planview of Road " + road.getId() + " is valid at s = " +
                                                  next.getLinearReference().getS() + " length = " + geom.getLength());
                }
            }
        }
    }

    /**
     * Apply functions on S-Runner. ATTENTION: This is only for LaneSection and Lane level.
     */
    private void applySRunner() {
        road.getLanes().getLaneSections().keySet().forEach(key -> {
            double sStart = key;
            double sEnd;
            if (road.getLanes().getLaneSections().size() <= 1 | key == road.getLanes().getLaneSections().lastKey()) {
                sEnd = road.getLength();
            } else {
                try {
                    sEnd = road.getLanes().getLaneSections().higherKey(key);
                } catch (Exception e) {
                    sEnd = sStart;
                }
            }
            if (sStart != sEnd) {
                LaneSection ls = road.getLanes().getLaneSections().get(key);
                LaneSectionParameters lsp = new LaneSectionParameters();
                ArrayList<Double> sPositions = Discretisation.generateSRunner(step, sEnd - sStart);
                //---- calc points
                sPositions.forEach(s -> {
                    LaneHelper.createLaneGroundPoints(s, sStart, lsp, ls, road, pointFactory, factory);
                });
                //---- calc line, polygons
                LaneHelper.createCenterLine(ls, lsp);
                LaneHelper.createLanePolygons3D(ls, lsp, step);
                RoadMarkHelper.createRoadMarkPolygons3D(ls, lsp);
                LaneSectionHelper.createLaneSectionPolygons(road, rp, ls, lsp, step);
            }
        });
    }
}

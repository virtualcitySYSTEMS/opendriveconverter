package de.vcs.utils.lanesection;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.datatypes.RoadParameters;
import de.vcs.model.odr.lane.Height;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.ODRHelper;
import de.vcs.utils.geometry.Transformation;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class LaneSectionHelper {

    public static void createLaneSectionPolygons(Road road, RoadParameters rp, LaneSection ls,
            LaneSectionParameters lsp, double step) {
        createPoints(road, rp, ls, lsp, step);
    }

    public static void createLaneSectionPolygons(LaneSection ls, LaneSectionParameters lsp, double step) {
        createPoints(null, null, ls, lsp, step);
    }

    private static void createPoints(Road road, RoadParameters rp, LaneSection ls, LaneSectionParameters lsp,
            double step) {
        int minLaneID = 0;
        int maxLaneID = 0;
        try {
            minLaneID = ls.getRightLanes().firstKey();
        } catch (NoSuchElementException e) {
        }
        try {
            maxLaneID = ls.getLeftLanes().lastKey();
        } catch (NoSuchElementException e) {
        }
        Lane laneFirst = ODRHelper.getLanesWithCenterLane(ls).get(minLaneID);
        Lane laneLast = ODRHelper.getLanesWithCenterLane(ls).get(maxLaneID);
        ArrayList<Point> lanePointsFirst = lsp.getLanes().get(minLaneID);
        ArrayList<Point> lanePoints = new ArrayList<>();
        ArrayList<Point> lanePointsLast = lsp.getLanes().get(maxLaneID);
        double innerHeight = 0.0;
        double outerHeight = 0.0;
        for (int j = 0; j < lanePointsFirst.size(); j++) {
            double sLocal = step * j;
            if (laneFirst != null && laneFirst.getHeights().floorEntry(sLocal) != null) {
                Height heights = laneFirst.getHeights().floorEntry(sLocal).getValue();
                innerHeight = minLaneID < 0 ? heights.getOuter() : heights.getInner();
            }
            Point firstPoint = Transformation.translatePoint(lanePointsFirst.get(j), 0, 0, innerHeight);
            lanePoints.add(firstPoint);
            if (road != null & rp != null) {
                rp.getRoadPointsMin().add(firstPoint);
                if (laneLast != null && laneLast.getHeights().floorEntry(sLocal) != null) {
                    Height heights = laneLast.getHeights().floorEntry(sLocal).getValue();
                    outerHeight = maxLaneID < 0 ? heights.getInner() : heights.getOuter();
                }
                Point lastPoint = Transformation.translatePoint(lanePointsLast.get(j), 0, 0, outerHeight);
                rp.getRoadPointsMax().add(0, lastPoint);
            }
            outerHeight = 0.0;
        }
        for (int k = lanePointsLast.size() - 1; k >= 0; k--) {
            double sLocal = step * k;
            if (laneLast != null && laneLast.getHeights().floorEntry(sLocal) != null) {
                Height heights = laneLast.getHeights().floorEntry(sLocal).getValue();
                outerHeight = maxLaneID < 0 ? heights.getInner() : heights.getOuter();
            }
            Point lastPoint = Transformation.translatePoint(lanePointsLast.get(k), 0, 0, outerHeight);
            lanePoints.add(lastPoint);
        }
        lanePoints.add(lanePoints.get(0));
        Polygon polygon = (Polygon) ODRGeometryFactory.create(JTSConstants.POLYGON, lanePoints);
        try {
            ls.getGmlGeometries().add(polygon);
        } catch (NullPointerException e) {
        }
    }
}

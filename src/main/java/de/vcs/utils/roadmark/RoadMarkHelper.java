package de.vcs.utils.roadmark;

import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.lane.RoadMark;
import de.vcs.utils.transformation.PointFactory;

public class RoadMarkHelper {

    public static void addRoadMarkPoints(LaneSectionParameters lsp, int laneID, PointFactory pointFactory,
            AbstractODRGeometry geom, double sGlobal, double projectedWidth, double height, RoadMark roadMark) {
        double roadmarkWidth = 0.06;
        switch (roadMark.getType()) {
            case "solid":
            case "custom":
            case "edge":
            case "broken":
            case "grass":
            case "curb":
                lsp.getRoadMarks().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, projectedWidth - roadmarkWidth, height, roadMark));
                lsp.getRoadMarks().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, projectedWidth + roadmarkWidth, height, roadMark));
                break;
            case "solid broken":
            case "solid solid":
                lsp.getRoadMarks().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, projectedWidth - roadmarkWidth - 2 * roadmarkWidth, height,
                                roadMark));
                lsp.getRoadMarks().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, projectedWidth - roadmarkWidth, height, roadMark));
                lsp.getRoadMarks().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, projectedWidth + roadmarkWidth, height, roadMark));
                lsp.getRoadMarks().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, projectedWidth + roadmarkWidth + 2 * roadmarkWidth, height,
                                roadMark));
                break;
            case "botts dots":
                lsp.getRoadMarks().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, projectedWidth, height, roadMark));
                break;
        }
    }
}


package de.vcs.utils.roadmark;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.area.roadmarkfactory.RoadMarkGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.datatypes.RoadMarkPoint;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.lane.Height;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.lane.RoadMark;
import de.vcs.utils.ODRHelper;
import de.vcs.utils.constants.RoadMarkConstants;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.log.ODRLogger;
import de.vcs.utils.transformation.PointFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;

public class RoadMarkHelper {

    public static void addRoadMarkPoints(LaneSectionParameters lsp, int laneID, PointFactory pointFactory,
            AbstractODRGeometry geom, double sGlobal, double width, double height, RoadMark roadMark,
            GeometryFactory factory) {
        double roadmarkWidth = 0.06;
        if (roadMark.getWidth() > 0.0) {
            roadmarkWidth = roadMark.getWidth() / 2.0;
        }
        switch (roadMark.getType()) {
            case RoadMarkConstants.SOLID:
//            case RoadMarkConstants.CUSTOM:
//            case RoadMarkConstants.EDGE:
            case RoadMarkConstants.BROKEN:
//            case RoadMarkConstants.GRASS:
//            case RoadMarkConstants.CURB:
            case RoadMarkConstants.NONE:
                lsp.getSingleLaneRoadMark().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth, height, roadMark, factory));
                lsp.getSingleLaneRoadMark().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth, height, roadMark, factory));
                break;
            case RoadMarkConstants.SOLID_BROKEN:
            case RoadMarkConstants.BROKEN_SOLID:
            case RoadMarkConstants.SOLID_SOLID:
                lsp.getDoubleLaneRoadMarkDown().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth - 2 * roadmarkWidth, height, roadMark,
                                factory));
                lsp.getDoubleLaneRoadMarkDown().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth, height, roadMark, factory));
                lsp.getDoubleLaneRoadMarkUp().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth, height, roadMark, factory));
                lsp.getDoubleLaneRoadMarkUp().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth + 2 * roadmarkWidth, height, roadMark,
                                factory));
                break;
            case "botts dots":
                lsp.getBottsDots().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width, height, roadMark, factory));
                break;
        }
    }

    public static void createRoadMarkPolygons3D(LaneSection ls, LaneSectionParameters lsp) {
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
        for (int i = minLaneID; i <= maxLaneID; i++) {
            if (!lsp.getSingleLaneRoadMark().get(i).isEmpty()) {
                String currentRMType = lsp.getSingleLaneRoadMark().get(i).get(0).getRoadMark().getType();
                String preRMType = currentRMType;
                ArrayList<RoadMarkPoint> rmpPointsDown = new ArrayList<>();
                ArrayList<RoadMarkPoint> rmpPointsUp = new ArrayList<>();
                boolean isSingleRoadMark = true;
                for (int j = 0; j < lsp.getSingleLaneRoadMark().get(i).size() - 1; j++) {
                    RoadMarkPoint rmp = lsp.getSingleLaneRoadMark().get(i).get(j);
                    currentRMType = rmp.getRoadMark().getType();
                    if (!currentRMType.equals(preRMType) & !preRMType.equals(RoadMarkConstants.NONE)) {
                        //hier polygon erzeugen und an roadmark haengen
                        isSingleRoadMark = false;
                        rmpPointsDown.add(lsp.getSingleLaneRoadMark().get(i).get(j));
                        rmpPointsUp.add(lsp.getSingleLaneRoadMark().get(i).get(j + 1));
                        Collections.reverse(rmpPointsUp);
                        rmpPointsDown.addAll(rmpPointsUp);
                        rmpPointsDown.add(rmpPointsDown.get(0));
                        double sRoradMark = rmpPointsDown.get(0).getRoadMark().getStTransform().getsOffset();
                        Geometry polygon = RoadMarkGeometryFactory.createRoadMark(preRMType, rmpPointsDown);
                        try {
                            if (i < 0) {
                                ls.getRightLanes().get(i).getRoadMarks().get(sRoradMark).getGmlGeometries()
                                        .add(polygon);
                            } else if (i == 0) {
                                ls.getCenterLane().getRoadMarks().get(sRoradMark).getGmlGeometries().add(polygon);
                            } else {
                                ls.getLeftLanes().get(i).getRoadMarks().get(sRoradMark).getGmlGeometries().add(polygon);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        rmpPointsDown = new ArrayList<>();
                        rmpPointsDown.add(lsp.getSingleLaneRoadMark().get(i).get(j));
                        rmpPointsUp = new ArrayList<>();
                    } else {
                        if (j % 2 == 0) {
                            rmpPointsDown.add(rmp);
                        } else {
                            rmpPointsUp.add(rmp);
                        }
                    }
                    preRMType = currentRMType;
                }
                if (isSingleRoadMark & !preRMType.equals(RoadMarkConstants.NONE)) {
                    //hier polygon erzeugen und an roadmark haengen
                    rmpPointsUp
                            .add(lsp.getSingleLaneRoadMark().get(i).get(lsp.getSingleLaneRoadMark().get(i).size() - 1));
                    Collections.reverse(rmpPointsUp);
                    rmpPointsDown.addAll(rmpPointsUp);
                    rmpPointsDown.add(rmpPointsDown.get(0));
                    double sRoradMark = rmpPointsDown.get(0).getRoadMark().getStTransform().getsOffset();
                    Geometry polygon = RoadMarkGeometryFactory.createRoadMark(currentRMType, rmpPointsDown);
                    try {
                        if (i < 0) {
                            ls.getRightLanes().get(i).getRoadMarks().floorEntry(sRoradMark).getValue()
                                    .getGmlGeometries().add(polygon);
                        } else if (i == 0) {
                            ls.getCenterLane().getRoadMarks().floorEntry(sRoradMark).getValue().getGmlGeometries()
                                    .add(polygon);
                        } else {
                            ls.getLeftLanes().get(i).getRoadMarks().floorEntry(sRoradMark).getValue().getGmlGeometries()
                                    .add(polygon);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}


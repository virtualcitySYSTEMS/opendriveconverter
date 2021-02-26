package de.vcs.area.generator;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.AbstractSTGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.lane.Height;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.lane.RoadMark;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.ODRHelper;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.log.ODRLogger;
import de.vcs.utils.math.ElevationHelper;
import de.vcs.utils.math.PolynomHelper;
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

    public RoadAreaGenerator(Road road) {
        this.road = road;
        pointFactory = new PointFactory();
        factory = new GeometryFactory();
    }

    @Override
    public void generateArea() {
        //validateRoadGeometry();
        applySRunner();
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
                    createLaneGroundPoints(s, sStart, lsp, ls);
                });
                //---- calc line, polygons
                createCenterLine(ls, lsp);
//                createLanePolygons(ls, lsp);
                createLanePolygons3D(ls, lsp);
                RoadMarkHelper.createRoadMarkPolygons3D(ls, lsp);
            }
        });
    }

    private void createLaneGroundPoints(double sLocal, double sStart, LaneSectionParameters lsp, LaneSection ls) {
        double sGlobal = sLocal + sStart;
        AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(sGlobal).getValue();
        Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(sGlobal).getValue();
        Polynom superelevation = null;
        try {
            superelevation = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(sGlobal).getValue();
        } catch (Exception e) {
            ODRLogger.getInstance()
                    .error("Error creating RoadArea. Found no superelevation for road with id " + road.getId());
        }
        // TODO shape
        double offset = 0.0;
        if (road.getLanes().getLaneOffsets().isEmpty()) {
            offset = 0.0;
        } else {
            double sPolyOffset = road.getLanes().getLaneOffsets().floorEntry(sGlobal).getKey();
            offset = PolynomHelper.calcPolynomValue(road.getLanes().getLaneOffsets().floorEntry(sGlobal).getValue(),
                    sGlobal - sPolyOffset);
        }
        // init Treemap
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
        lsp.initLSPTreeMap(minLaneID, maxLaneID);
        //right lanes
        double currentRightWidth = offset;
        double currentRightHeight = 0;
        for (int i = -1; i >= minLaneID; i--) {
            Lane currentRightLane = ls.getRightLanes().floorEntry(i).getValue();
            Polynom poly = currentRightLane.getWidths().floorEntry(sLocal).getValue();
            double sPolyWidth = sLocal - poly.getStTransform().getsOffset();
            double width = PolynomHelper.calcPolynomValue(poly, sPolyWidth);
            currentRightWidth -= width;
            double projectedWidth = ElevationHelper
                    .getProjectedWidth(sGlobal, currentRightWidth, superelevation, currentRightLane.getLevel());
            double h = ElevationHelper
                    .getElevation(sGlobal, currentRightWidth, elevation, superelevation, currentRightLane.getLevel());
            if (!currentRightLane.getLevel() || currentRightHeight == 0) {
                currentRightHeight = h;
            }
            if (!currentRightLane.getRoadMarks().isEmpty()) {
                RoadMark roadMark = currentRightLane.getRoadMarks().floorEntry(sLocal).getValue();
                RoadMarkHelper
                        .addRoadMarkPoints(lsp, i, pointFactory, geom, sGlobal, projectedWidth, currentRightHeight,
                                roadMark, factory);
            }
            lsp.getLanes().get(i).add(pointFactory.getODRGeometryHandler(geom.getClass())
                    .sth2xyzPoint(geom, sGlobal, projectedWidth, currentRightHeight));
        }
        //left lanes
        double currentLeftWidth = offset;
        double currentLeftHeight = 0;
        for (int i = 1; i <= maxLaneID; i++) {
            Lane currentLeftLane = ls.getLeftLanes().floorEntry(i).getValue();
            Polynom poly = currentLeftLane.getWidths().floorEntry(sLocal).getValue();
            double sPolyWidth = sLocal - poly.getStTransform().getsOffset();
            double width = PolynomHelper.calcPolynomValue(poly, sPolyWidth);
            currentLeftWidth += width;
            double projectedWidth = ElevationHelper
                    .getProjectedWidth(sGlobal, currentLeftWidth, superelevation, currentLeftLane.getLevel());
            double h = ElevationHelper
                    .getElevation(sGlobal, currentLeftWidth, elevation, superelevation, currentLeftLane.getLevel());
            if (!currentLeftLane.getLevel() || currentLeftHeight == 0) {
                currentLeftHeight = h;
            }
            //RoadMarkParameter
            if (!currentLeftLane.getRoadMarks().isEmpty()) {
                RoadMark roadMark = currentLeftLane.getRoadMarks().floorEntry(sLocal).getValue();
                RoadMarkHelper.addRoadMarkPoints(lsp, i, pointFactory, geom, sGlobal, projectedWidth, currentLeftHeight,
                        roadMark, factory);
            }
            lsp.getLanes().get(i).add(pointFactory.getODRGeometryHandler(geom.getClass())
                    .sth2xyzPoint(geom, sGlobal, projectedWidth, currentLeftHeight));
        }
        double projectedOffset = ElevationHelper.getProjectedWidth(sGlobal, offset, superelevation, false);
        double h = ElevationHelper.getElevation(sGlobal, offset, elevation, superelevation);
        lsp.getLanes().get(0).add(pointFactory.getODRGeometryHandler(geom.getClass())
                .sth2xyzPoint(geom, sGlobal, projectedOffset, h));
        if (!ls.getCenterLane().getRoadMarks().isEmpty()) {
            RoadMark roadMark = ls.getCenterLane().getRoadMarks().floorEntry(sLocal).getValue();
            RoadMarkHelper
                    .addRoadMarkPoints(lsp, 0, pointFactory, geom, sGlobal, projectedOffset, h, roadMark, factory);
        }
    }

    private void createCenterLine(LaneSection ls, LaneSectionParameters lsp) {
        ls.getCenterLane().getGmlGeometries()
                .add(ODRGeometryFactory.create(JTSConstants.LINESTRING, lsp.getLanes().get(0)));
    }

    private void createLanePolygons(LaneSection ls, LaneSectionParameters lsp) {
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
        for (int i = minLaneID; i < maxLaneID; i++) {
            ArrayList<Point> lanePointsFirst = lsp.getLanes().get(i);
            ArrayList<Point> lanePoints = new ArrayList<>();
            ArrayList<Point> lanePointsNext = lsp.getLanes().get(i + 1);
            for (Point p : lanePointsFirst) {
                lanePoints.add(p);
            }
            for (int j = lanePointsNext.size() - 1; j >= 0; j--) {
                lanePoints.add(lanePointsNext.get(j));
            }
            lanePoints.add(lanePointsFirst.get(0));
            Polygon polygon = (Polygon) ODRGeometryFactory.create(JTSConstants.POLYGON, lanePoints);
            try {
                if (i < 0) {
                    ls.getRightLanes().get(i).getGmlGeometries().add(polygon);
                } else {
                    ls.getLeftLanes().get(i + 1).getGmlGeometries().add(polygon);
                }
            } catch (NullPointerException e) {
            }
        }
    }

    private void createLanePolygons3D(LaneSection ls, LaneSectionParameters lsp) {
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
        for (int i = minLaneID; i < maxLaneID; i++) {
            Lane lane = ODRHelper.getLanes(ls).get(i < 0 ? i : i + 1);
            ArrayList<Point> lanePointsFirst = lsp.getLanes().get(i);
            ArrayList<Point> lanePoints = new ArrayList<>();
            ArrayList<Point> lanePointsNext = lsp.getLanes().get(i + 1);
            double innerHeight = 0.0;
            double outerHeight = 0.0;
            for (int j = 0; j < lanePointsFirst.size(); j++) {
                double sLocal = step * j;
                if (lane != null && lane.getHeights().floorEntry(sLocal) != null) {
                    Height heights = lane.getHeights().floorEntry(sLocal).getValue();
                    innerHeight = i < 0 ? heights.getOuter() : heights.getInner();
                }
                lanePoints.add(Transformation.translatePoint(lanePointsFirst.get(j), 0, 0, innerHeight));
            }
            for (int k = lanePointsNext.size() - 1; k >= 0; k--) {
                double sLocal = step * k;
                if (lane != null && lane.getHeights().floorEntry(sLocal) != null) {
                    Height heights = lane.getHeights().floorEntry(sLocal).getValue();
                    outerHeight = i < 0 ? heights.getInner() : heights.getOuter();
                }
                lanePoints.add(Transformation.translatePoint(lanePointsNext.get(k), 0, 0, outerHeight));
            }
            lanePoints.add(lanePoints.get(0));
            Polygon polygon = (Polygon) ODRGeometryFactory.create(JTSConstants.POLYGON, lanePoints);
            try {
                if (i < 0) {
                    ls.getRightLanes().get(i).getGmlGeometries().add(polygon);
                } else {
                    ls.getLeftLanes().get(i + 1).getGmlGeometries().add(polygon);
                }
            } catch (NullPointerException e) {
            }
        }
    }
}

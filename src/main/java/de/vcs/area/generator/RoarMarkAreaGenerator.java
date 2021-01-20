package de.vcs.area.generator;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.math.PolynomHelper;
import de.vcs.utils.transformation.PointFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class RoarMarkAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;
    PointFactory pointFactory;

    public RoarMarkAreaGenerator(Road road) {
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
                    //TODO 0.01 change to little bit less than delta s
                    sEnd = road.getLanes().getLaneSections().ceilingKey(key + 0.01);
                } catch (Exception e) {
                    sEnd = sStart;
                }
            }
            if (sStart != sEnd) {
                LaneSection ls = road.getLanes().getLaneSections().get(key);
                LaneSectionParameters lsp = new LaneSectionParameters();
                ArrayList<Double> sPositions = Discretisation.generateSRunner(0.2, sEnd - sStart);
                //---- calc points
                sPositions.forEach(s -> {
                    fillCenterLine(s, sStart, lsp, ls);
                });
                //---- calc line, polygons
                createCenterLine(ls, lsp);
                createLanePolygons(ls, lsp);
//                createLaneSectionSemanticPolygons(ls);
            }
        });
    }

    private void fillCenterLine(double sLocal, double sStart, LaneSectionParameters lsp, LaneSection ls) {
        double sGlobal = sLocal + sStart;
        AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(sGlobal).getValue();
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
        initLSPTreeMap(minLaneID, maxLaneID, lsp);
        //right lanes
        double currentRightWidth = offset;
        for (int i = -1; i >= minLaneID; i--) {
            Polynom poly = ls.getRightLanes().floorEntry(i).getValue().getWidths().floorEntry(sLocal).getValue();
            double sPolyWidth = sLocal - poly.getStTransform().getsOffset();
            double width = PolynomHelper.calcPolynomValue(poly, sPolyWidth);
            currentRightWidth -= width;
            lsp.getLanes().get(i).add(pointFactory.getODRGeometryHandler(geom.getClass())
                    .sth2xyzPoint(geom, sGlobal, currentRightWidth));
        }
        double currentLeftWidth = offset;
        for (int i = 1; i <= maxLaneID; i++) {
            Polynom poly = ls.getLeftLanes().floorEntry(i).getValue().getWidths().floorEntry(sLocal).getValue();
            double sPolyWidth = sLocal - poly.getStTransform().getsOffset();
            double width = PolynomHelper.calcPolynomValue(poly, sPolyWidth);
            currentLeftWidth += width;
            lsp.getLanes().get(i).add(pointFactory.getODRGeometryHandler(geom.getClass())
                    .sth2xyzPoint(geom, sGlobal, currentLeftWidth));
        }
        lsp.getLanes().get(0)
                .add(pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, sGlobal, offset));
    }

    private void initLSPTreeMap(int minLaneID, int maxLaneID, LaneSectionParameters lsp) {
        for (int i = minLaneID; i <= maxLaneID; i++) {
            if (!isLSPTreeMap(i, lsp)) {
                lsp.getLanes().put(i, new ArrayList<>());
            }
        }
    }

    private boolean isLSPTreeMap(int key, LaneSectionParameters lsp) {
        if (lsp.getLanes().get(key) == null) {
            return false;
        } else {
            return true;
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
}

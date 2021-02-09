package de.vcs.utils.roadmark;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.LaneSectionParameters;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.lane.Height;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.lane.RoadMark;
import de.vcs.utils.ODRHelper;
import de.vcs.utils.constants.RoadMarkConstants;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.transformation.PointFactory;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class RoadMarkHelper {

    public static void addRoadMarkPoints(LaneSectionParameters lsp, int laneID, PointFactory pointFactory,
            AbstractODRGeometry geom, double sGlobal, double width, double height, RoadMark roadMark,
            GeometryFactory factory) {
        double roadmarkWidth = 0.06;
        switch (roadMark.getType()) {
            case RoadMarkConstants.SOLID:
            case RoadMarkConstants.CUSTOM:
            case RoadMarkConstants.EDGE:
            case RoadMarkConstants.BROKEN:
            case RoadMarkConstants.GRASS:
            case RoadMarkConstants.CURB:
                lsp.getRoadMarksSolidDown().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth, height, roadMark, factory));
                lsp.getRoadMarksSolidUp().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth, height, roadMark, factory));
                break;
            case RoadMarkConstants.SOLID_BROKEN:
                lsp.getRoadMarksSolidBrokenDown1().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth - 2 * roadmarkWidth, height, roadMark,
                                factory));
                lsp.getRoadMarksSolidBrokenUp1().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth, height, roadMark, factory));
                lsp.getRoadMarksSolidBrokenDown2().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth, height, roadMark, factory));
                lsp.getRoadMarksSolidBrokenUp2().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth + 2 * roadmarkWidth, height, roadMark,
                                factory));
                break;
            case RoadMarkConstants.BROKEN_SOLID:
                lsp.getRoadMarksBrokenSolidDown1().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth - 2 * roadmarkWidth, height, roadMark,
                                factory));
                lsp.getRoadMarksBrokenSolidUp1().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth, height, roadMark, factory));
                lsp.getRoadMarksBrokenSolidDown2().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth, height, roadMark, factory));
                lsp.getRoadMarksBrokenSolidUp2().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth + 2 * roadmarkWidth, height, roadMark,
                                factory));
                break;
            case RoadMarkConstants.SOLID_SOLID:
                lsp.getRoadMarksSolidSolidDown1().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth - 2 * roadmarkWidth, height, roadMark,
                                factory));
                lsp.getRoadMarksSolidSolidUp1().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width - roadmarkWidth, height, roadMark, factory));
                lsp.getRoadMarksSolidSolidDown2().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth, height, roadMark, factory));
                lsp.getRoadMarksSolidSolidUp2().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width + roadmarkWidth + 2 * roadmarkWidth, height, roadMark,
                                factory));
                break;
            case "botts dots":
                lsp.getRoadMarksBottsDots().get(laneID).add(pointFactory.getODRGeometryHandler(geom.getClass())
                        .sth2xyzPoint(geom, sGlobal, width, height, roadMark, factory));
                break;
        }
    }
}


package de.vcs.area.roadmarkfactory;

import de.vcs.datatypes.RoadMarkPoint;
import de.vcs.utils.log.ODRLogger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;
import java.util.Collections;

public class RoadMarkBroken implements RoadMarkGeometry {

    @Override
    public Geometry createRoadMark(ArrayList<RoadMarkPoint> points) {
        if (points.size() > 12) {
            ArrayList<RoadMarkPoint> pointsUp = new ArrayList<>();
            ArrayList<RoadMarkPoint> pointsDown = new ArrayList<>();
            ArrayList<Polygon> polys = new ArrayList<>();
            preparePointsArray(points, pointsUp, pointsDown);
            if (pointsUp.size() == pointsDown.size()) {
                for (int i = 0; i < pointsDown.size() - 11; i += 12) {
                    RoadMarkPoint rmp0 = pointsDown.get(i);
                    RoadMarkPoint rmp1 = pointsDown.get(i + 1);
                    RoadMarkPoint rmp2 = pointsDown.get(i + 2);
                    RoadMarkPoint rmp3 = pointsDown.get(i + 3);
                    RoadMarkPoint rmp4 = pointsDown.get(i + 4);
                    RoadMarkPoint rmp5 = pointsDown.get(i + 5);
                    RoadMarkPoint rmp6 = pointsDown.get(i + 6);
                    RoadMarkPoint rmp7 = pointsUp.get(i + 6);
                    RoadMarkPoint rmp8 = pointsUp.get(i + 5);
                    RoadMarkPoint rmp9 = pointsUp.get(i + 3);
                    RoadMarkPoint rmp10 = pointsUp.get(i + 2);
                    RoadMarkPoint rmp11 = pointsUp.get(i + 1);
                    RoadMarkPoint rmp12 = pointsUp.get(i);
                    ArrayList<RoadMarkPoint> polyPoints = new ArrayList<>();
                    polyPoints.add(rmp0);
                    polyPoints.add(rmp1);
                    polyPoints.add(rmp2);
                    polyPoints.add(rmp3);
                    polyPoints.add(rmp4);
                    polyPoints.add(rmp5);
                    polyPoints.add(rmp6);
                    polyPoints.add(rmp7);
                    polyPoints.add(rmp8);
                    polyPoints.add(rmp9);
                    polyPoints.add(rmp10);
                    polyPoints.add(rmp11);
                    polyPoints.add(rmp12);
                    polyPoints.add(rmp0);
                    Polygon poly = new GeometryFactory().createPolygon(points2Coordinates(polyPoints));
                    polys.add(poly);
                }
            }
            return new GeometryFactory().createMultiPolygon(polygonList2Array(polys));
        } else {
            return null;
        }
    }

    private void preparePointsArray(ArrayList<RoadMarkPoint> points, ArrayList<RoadMarkPoint> pointsUp,
            ArrayList<RoadMarkPoint> pointsDown) {
        points.remove(points.size() - 1);
        if (points.size() % 2 == 0) {
            points.stream().limit(points.size() / 2).forEach(p -> {
                pointsDown.add(p);
            });
            points.stream().skip(points.size() / 2).forEach(p -> {
                pointsUp.add(p);
            });
            Collections.reverse(pointsUp);
        } else {
            ODRLogger.getInstance().error("Invalid number of points for RoadMark type broken.");
        }
    }
}


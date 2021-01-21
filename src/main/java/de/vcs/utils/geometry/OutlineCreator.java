package de.vcs.utils.geometry;

import org.locationtech.jts.geom.*;
import java.util.ArrayList;
import java.util.Collections;

public class OutlineCreator {

    public static Polygon createCircularOutline(Point point, double radius) {
        return (Polygon) point.buffer(radius); // TODO buffer is 2D!!!
    }

    public static Polygon createRectangularOutline(Point p1, Point p2) {
        Coordinate[] coordinates = new Coordinate[]{
                p1.getCoordinate(),
                new Coordinate( p2.getX(), p1.getY(), p2.getCoordinate().getZ()),
                p2.getCoordinate(),
                new Coordinate(p1.getX(), p2.getY(), p1.getCoordinate().getZ()),
                p1.getCoordinate(),
        };
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPolygon(coordinates);
    }

    public static Polygon createPolygonalOutline(ArrayList<Coordinate> inner, ArrayList<Coordinate> outer) {
        CoordinateList coords = new CoordinateList();
        coords.addAll(outer);
        Collections.reverse(inner); // does this change the global state of the array?
        coords.addAll(inner);
        coords.closeRing();
        GeometryFactory geometryFactory = new GeometryFactory();
        if (coords.size() > 3) {
            return geometryFactory.createPolygon(coords.toCoordinateArray());
        }
        return null;
    }
}

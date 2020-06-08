package de.vcs.utils.geometry;

import de.vcs.model.odr.geometry.STHPosition;
import de.vcs.model.odr.road.Road;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class OutlineCreator {

    public static Polygon createCircularOutline(Point point, double radius) {
        return (Polygon) point.buffer(radius);
    }

    public static Polygon createRectangularOutline(Road road, STHPosition sth, double length, double width) {
        Point p1 = Transformation.st2xyPoint(
                road,
                sth.getS() - length / 2,
                sth.getT() - width / 2
        );
        Point p2 = Transformation.st2xyPoint(
                road,
                sth.getS() + length / 2,
                sth.getT() + width / 2
        );
        Coordinate[] coordinates = new Coordinate[]{
                p1.getCoordinate(),
                new Coordinate( p2.getX(), p1.getY()),
                p2.getCoordinate(),
                new Coordinate(p1.getX(), p2.getY()),
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
        coords.add(outer.get(0));
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPolygon(coords.toCoordinateArray());
    }
}

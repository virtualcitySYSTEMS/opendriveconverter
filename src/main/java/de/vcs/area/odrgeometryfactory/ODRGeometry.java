package de.vcs.area.odrgeometryfactory;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;

public interface ODRGeometry {

    public Geometry create(ArrayList<Point> points);

    default Coordinate[] points2Coordinates(ArrayList<Point> points) {
        return points.stream().map(Point::getCoordinate).toArray(size -> new Coordinate[size]);
    }
}

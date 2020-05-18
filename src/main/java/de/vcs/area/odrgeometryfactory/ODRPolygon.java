package de.vcs.area.odrgeometryfactory;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;

public class ODRPolygon implements ODRGeometry {

    @Override
    public Geometry create(ArrayList<Point> points) {
        return new GeometryFactory().createPolygon(points2Coordinates(points));
    }
}
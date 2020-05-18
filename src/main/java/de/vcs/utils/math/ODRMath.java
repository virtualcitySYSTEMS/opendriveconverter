package de.vcs.utils.math;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class ODRMath {

    public static double normalizeComponent(double a, double b) {
        return a / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }

    public static Point vectorAdd(Point a, Point b){
        return new GeometryFactory().createPoint(new Coordinate(a.getX()+b.getX(), a.getY()+b.getY()));
    }

    public static Point vectorMulti(double length, Point p){
        return new GeometryFactory().createPoint(new Coordinate(length*p.getX(), length*p.getY()));
    }
}

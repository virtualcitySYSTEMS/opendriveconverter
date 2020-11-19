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

    public static double interpolate(double start, double end, double value) throws IllegalArgumentException {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("value " + value + " out of range [0,1]");
        }
        return start + value * (end - start);
    }

    public static double normalizeAngle(double angle) {
        return angle - 2 * Math.PI * Math.floor((angle + Math.PI) / (2 * Math.PI));
    }
}

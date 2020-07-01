package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Polynom;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class PolynomHelper {

    public static double calcPolynomValue(double ds, Polynom p) {
        return p.getA() + p.getB() * ds + p.getC() * Math.pow(ds, 2) + p.getD() * Math.pow(ds, 3);
    }

    private static double getFirstDerivation(double ds, Polynom p) {
        return p.getB() + 2 * ds * p.getC() + 3 * p.getD() * Math.pow(ds, 2);
    }

    public static Point calcUVPoint(double ds, Polynom p) {
        double v = calcPolynomValue(ds, p);
        double u = ds;
        return new GeometryFactory().createPoint(new Coordinate(u, v));
    }

    public static Point calcNormalVector(double ds, Point point, Polynom p) {
        double tu = ds;
        double tv = getFirstDerivation(ds, p);
        double tun = ODRMath.normalizeComponent(tu, tv);
        double tvn = ODRMath.normalizeComponent(tv, tu);
        return new GeometryFactory().createPoint(new Coordinate(-tvn, tun));
    }

    public static Point calcUVPointPerpendicularToCurve(Point uvpoint, Point nvpoint, double distance) {
        return new GeometryFactory().createPoint(
                new Coordinate(uvpoint.getX() - nvpoint.getX() * distance, uvpoint.getY() + nvpoint.getY() * distance));
    }

    public static Point calcUVPointPerpendicularToCurve(double ds, double distance, Polynom p) {
        Point uvpoint = calcUVPoint(ds, p);
        Point nvpoint = calcNormalVector(ds, uvpoint, p);
        return new GeometryFactory().createPoint(
                new Coordinate(uvpoint.getX() - nvpoint.getY() * distance, uvpoint.getY() + nvpoint.getY() * distance));
    }
}

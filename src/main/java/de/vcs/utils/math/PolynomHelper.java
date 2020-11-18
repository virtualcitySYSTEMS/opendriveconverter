package de.vcs.utils.math;

import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.model.odr.geometry.Polynom;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class PolynomHelper {

    /**
     * @param p  ODR geometry
     * @param ds local s on geometry
     * @param t  offset perpendicular to curve
     * @return uv point
     */
    public static Point calcUVPoint(Polynom p, double ds, double t) {
        double v = calcPolynomValue(p, ds);
        double u = ds;
        Point uvpoint = new GeometryFactory().createPoint(new Coordinate(u, v));
        double offsetX = 0.0;
        double offsetY = 0.0;
        if (t != 0.0) {
            Point nvpoint = calcNormalVector(p, ds);
            offsetX = nvpoint.getX() * t;
            offsetY = nvpoint.getY() * t;
        }
        return new GeometryFactory().createPoint(
                new Coordinate(uvpoint.getX() - offsetX, uvpoint.getY() + offsetY));
    }

    /**
     * calculates the local heading at a point p of the geometry
     * scalar product of u vector [1 0] and tangent vector [t1 t2] = [-n1 n2] (with normal vector n)
     * @param p  ODR geometry
     * @param ds local s on geometry
     * @return heading in radian
     */
    public static double calcLocalHdg(Polynom p, double ds) {
        Point nvpoint = calcNormalVector(p, ds);
        return Math.acos(-nvpoint.getX());
    }

    /**
     * normal vector at position s
     *
     * @param p  ODR param poly geometry
     * @param ds position along geometry
     * @return normal vector
     */
    public static Point calcNormalVector(Polynom p, double ds) {
        double tu = 1.0;
        double tv = getFirstDerivation(p, ds);
        double tun = ODRMath.normalizeComponent(tu, tv);
        double tvn = ODRMath.normalizeComponent(tv, tu);
        return new GeometryFactory().createPoint(new Coordinate(-tvn, tun));
    }

    /**
     * @param p  ODR param poly geometry
     * @param ds position along geometry
     * @return polynom value at ds
     */
    public static double calcPolynomValue(Polynom p, double ds) {
        return p.getA() + p.getB() * ds + p.getC() * Math.pow(ds, 2) + p.getD() * Math.pow(ds, 3);
    }

    /**
     * first derivation
     *
     * @param p  ODR param poly geometry
     * @param ds position along geometry
     * @return derivation at ds
     */
    private static double getFirstDerivation(Polynom p, double ds) {
        return p.getB() + 2 * ds * p.getC() + 3 * p.getD() * Math.pow(ds, 2);
    }
}

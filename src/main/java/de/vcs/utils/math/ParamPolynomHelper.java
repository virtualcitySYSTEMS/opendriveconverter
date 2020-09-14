package de.vcs.utils.math;

import de.vcs.model.odr.geometry.ParamPolynom;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class ParamPolynomHelper {

    /**
     * @param p  ODR geometry
     * @param ds local s on geometry
     * @param t  offset perpendicular to curve
     * @return uv point
     */
    public static Point calcUVPoint(ParamPolynom p, double ds, double t) {
        double u = calcParamPolynomValueU(p, ds);
        double v = calcParamPolynomValueV(p, ds);
        Point uvpoint = new GeometryFactory().createPoint(new Coordinate(u, v));
        double offsetX = 0.0;
        double offsetY = 0.0;
        if (t != 0.0) {
            Point nvpoint = calcNormalVector(p, ds);
            offsetX = nvpoint.getX() * t;
            offsetY = nvpoint.getY() * t;
        }
        return new GeometryFactory().createPoint(
                new Coordinate(uvpoint.getX() + offsetX, uvpoint.getY() + offsetY));
    }

    /**
     * normal vector at position s
     *
     * @param p  ODR param poly geometry
     * @param ds position along geometry
     * @return normal vector
     */
    public static Point calcNormalVector(ParamPolynom p, double ds) {
        double u = calcParamPolynomValueU(p, ds);
        double v = calcParamPolynomValueV(p, ds);
        double tu = getFirstDerivationU(p, ds);
        double tv = getFirstDerivationV(p, ds);
        double d = Math.sqrt(Math.pow(tu, 2) + Math.pow(tv, 2));
        double na = d * -1 * v;
        double nb = d * u;
        return new GeometryFactory().createPoint(new Coordinate(na, nb));
    }

    /**
     * @param p  ODR param poly geometry
     * @param ds position along geometry
     * @return polynom value u at ds
     */
    public static double calcParamPolynomValueU(ParamPolynom p, double ds) {
        return p.getaU() + p.getbU() * ds + p.getcU() * Math.pow(ds, 2) + p.getdU() * Math.pow(ds, 3);
    }

    /**
     * @param p  ODR param poly geometry
     * @param ds position along geometry
     * @return polynom value v at ds
     */
    public static double calcParamPolynomValueV(ParamPolynom p, double ds) {
        return p.getaV() + p.getbV() * ds + p.getcV() * Math.pow(ds, 2) + p.getdV() * Math.pow(ds, 3);
    }

    /**
     * first derivation
     *
     * @param p  ODR param poly geometry
     * @param ds position along geometry
     * @return derivation at ds
     */
    private static double getFirstDerivationU(ParamPolynom p, double ds) {
        return p.getbU() + 2 * ds * p.getcU() + 3 * p.getdU() * Math.pow(ds, 2);
    }

    /**
     * first derivation
     *
     * @param p  ODR param poly geometry
     * @param ds position along geometry
     * @return derivation at ds
     */
    private static double getFirstDerivationV(ParamPolynom p, double ds) {
        return p.getbV() + 2 * ds * p.getcV() + 3 * p.getdV() * Math.pow(ds, 2);
    }
}

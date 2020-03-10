package de.vcs.utils.math;

import de.vcs.datatypes.PolynomValue;
import de.vcs.model.odr.geometry.ParamPolynom;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class ParamPolynomHelper {

    public static double calcParamPolynomValueU(double ds, ParamPolynom p) {
        return p.getaU() + p.getbU() * ds + p.getcU() * Math.pow(ds, 2) + p.getdU() * Math.pow(ds, 3);
    }

    public static double calcParamPolynomValueV(double ds, ParamPolynom p) {
        return p.getaV() + p.getbV() * ds + p.getcV() * Math.pow(ds, 2) + p.getdV() * Math.pow(ds, 3);
    }

    //to-do not needed?
    public static PolynomValue calcPolynomValues(double ds, ParamPolynom p) {
        return new PolynomValue(
                (p.getaU() + p.getbU() * ds + p.getcU() * Math.pow(ds, 2) + p.getdU() * Math.pow(ds, 3)),
                (p.getaV() + p.getbV() * ds + p.getcV() * Math.pow(ds, 2) + p.getdV() * Math.pow(ds, 3)));
    }

    public static Point calcUVPointPerpendicularToCurve(double ds, double distance, ParamPolynom p) {
        double u = calcParamPolynomValueU(ds, p);
        double v = calcParamPolynomValueV(ds, p);
        double tu = getFirstDerivationU(ds, p);
        double tv = getFirstDerivationV(ds, p);
        double tun = normalizeComponent(tu, tv);
        double tvn = normalizeComponent(tv, tu);
        return new GeometryFactory().createPoint(new Coordinate(u - tvn * distance, v + tun * distance));
    }

    private static double getFirstDerivationU(double ds, ParamPolynom p) {
        return p.getbU() + 2 * ds * p.getcU() + 3 * p.getdU() * Math.pow(ds, 2);
    }

    private static double getFirstDerivationV(double ds, ParamPolynom p) {
        return p.getbV() + 2 * ds * p.getcV() + 3 * p.getdV() * Math.pow(ds, 2);
    }

    private static double normalizeComponent(double a, double b) {
        return a / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
}

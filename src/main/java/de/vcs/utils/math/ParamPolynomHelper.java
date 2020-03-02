package de.vcs.utils.math;

import de.vcs.datatypes.PolynomValue;
import de.vcs.model.odr.geometry.ParamPolynom;

public class ParamPolynomHelper {

    public static double calcParamPolynomValueU(double ds, ParamPolynom p) {
        return p.getaU() + p.getbU() * ds + p.getcU() * Math.pow(ds, 2) + p.getdU() * Math.pow(ds, 3);
    }

    public static double calcParamPolynomValueV(double ds, ParamPolynom p) {
        return p.getaV() + p.getbV() * ds + p.getcV() * Math.pow(ds, 2) + p.getdV() * Math.pow(ds, 3);
    }

    public static PolynomValue calcPolynomValues(double ds, ParamPolynom p) {
        return new PolynomValue(
                (p.getaU() + p.getbU() * ds + p.getcU() * Math.pow(ds, 2) + p.getdU() * Math.pow(ds, 3)),
                (p.getaV() + p.getbV() * ds + p.getcV() * Math.pow(ds, 2) + p.getdV() * Math.pow(ds, 3)));
    }
}

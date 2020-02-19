package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Polynom;

public class PolynomHelper {

    public static double calcPolynomValue(double ds, Polynom p) {
        return p.getA() + p.getB() * ds + p.getC() * Math.pow(ds, 2) + p.getD() * Math.pow(ds, 3);
    }
}

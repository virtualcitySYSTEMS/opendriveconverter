package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Polynom;

public class ElevationHelper {

    /**
     * get elevation value at stPosition
     * @param s global s-value
     * @param t global t-value
     * @param zOffset vertical offset
     * @param elevation Polynom describing elevation
     * @param superelevation Polynom describing superelevation
     * @return elevation at given position
     */
    public static double getElevation(double s, double t, double zOffset, Polynom elevation, Polynom superelevation) {
        double sHeight = 0.0;
        if (elevation != null) {
            sHeight = PolynomHelper.calcPolynomValue(elevation, s - elevation.getLinearReference().getS());
        }
        double tHeight = 0.0;
        if (superelevation != null) {
            tHeight = PolynomHelper.calcPolynomValue(superelevation, t);
        }
        return sHeight + tHeight + zOffset;
    }

}

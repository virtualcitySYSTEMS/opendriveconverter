package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Polynom;

public class ElevationHelper {

    /**
     * get elevation value at stPosition
     * @param s global s-value
     * @param t global t-value
     * @param elevation Polynom describing elevation
     * @param superelevation Polynom describing superelevation
     * @param level ignore superelevation if false
     * @return elevation at given position
     */
    public static double getElevation(double s, double t, Polynom elevation, Polynom superelevation, boolean level) {
        double sHeight = 0.0;
        if (elevation != null) {
            double ds = s - elevation.getLinearReference().getS();
            sHeight = PolynomHelper.calcPolynomValue(elevation, ds);
        }
        double tHeight = 0.0;
        if (superelevation != null && !level) {
            double ds = s - superelevation.getLinearReference().getS();
            tHeight = t * Math.sin(PolynomHelper.calcPolynomValue(superelevation, ds));
        }
        return sHeight + tHeight;
    }

    /**
     * get elevation value at stPosition
     * @param s global s-value
     * @param t global t-value
     * @param elevation Polynom describing elevation
     * @param superelevation Polynom describing superelevation
     * @return elevation at given position
     */
    public static double getElevation(double s, double t, Polynom elevation, Polynom superelevation) {
        return getElevation(s, t, elevation, superelevation, false);
    }

    /**
     *
     * @param s global s-value
     * @param t global t-value
     * @param superelevation Polynom describing superelevation
     * @param level ignore superelevation if false
     * @return projected width due to superelevation roll
     */
    public static double getProjectedWidth(double s, double t, Polynom superelevation, boolean level) {
        double width = t;
        if (superelevation != null && !level) {
            double ds = s - superelevation.getLinearReference().getS();
            width = t * Math.cos(PolynomHelper.calcPolynomValue(superelevation, ds));
        }
        return width;
    }
}

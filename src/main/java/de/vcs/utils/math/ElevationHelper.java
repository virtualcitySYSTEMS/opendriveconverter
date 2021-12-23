package de.vcs.utils.math;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;

import java.util.TreeMap;

public class ElevationHelper {

    /**
     * get elevation value at stPosition
     * @param s global s-value
     * @param t global t-value
     * @param elevation Polynom describing elevation
     * @param superelevation Polynom describing superelevation
     * @param shapes Polynoms describing lateral shape
     * @param level ignore superelevation if false
     * @return elevation at given position
     */
    public static double getElevation(double s, double t, Polynom elevation, Polynom superelevation,
            TreeMap<Double, TreeMap<Double, AbstractODRGeometry>> shapes, boolean level) {
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
        double shapeHeight = 0.0;
        if (shapes != null) {
            shapeHeight = getElevationFromShapes(s, t, shapes);
        }
        return sHeight + tHeight + shapeHeight;
    }

    /**
     * get elevation value at stPosition
     * @param s global s-value
     * @param t global t-value
     * @param elevation Polynom describing elevation
     * @param superelevation Polynom describing superelevation
     * @param shapes Polynoms describing lateral shape
     * @return elevation at given position
     */
    public static double getElevation(double s, double t, Polynom elevation, Polynom superelevation,
            TreeMap<Double, TreeMap<Double, AbstractODRGeometry>> shapes) {
        return getElevation(s, t, elevation, superelevation, shapes, false);
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

    /**
     * get elevation value at stPosition between to lateral shapes
     * @param s global s-value
     * @param t global t-value
     * @param shapes all shape definitions of a road
     * @return linear interpolated elevation at given position
     */
    public static double getElevationFromShapes(double s, double t, TreeMap<Double, TreeMap<Double, AbstractODRGeometry>> shapes) {
        Polynom shapeSR1 = (Polynom) shapes.floorEntry(s).getValue().floorEntry(t).getValue();
        Polynom shapeSR2 = (Polynom) shapes.ceilingEntry(s).getValue().floorEntry(t).getValue();
        double hSR1 = PolynomHelper.calcPolynomValue(shapeSR1, t - shapeSR1.getLinearReference().getT());
        double hSR2 = PolynomHelper.calcPolynomValue(shapeSR2, t - shapeSR2.getLinearReference().getT());
        return ((hSR1 + hSR2) * (s - shapeSR1.getLinearReference().getS())) /
                (shapeSR2.getLinearReference().getS() - shapeSR1.getLinearReference().getS());
    }
}

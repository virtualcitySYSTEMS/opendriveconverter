package de.vcs.utils.math;

public class ODRMath {

    public static double normalizeComponent(double a, double b) {
        return a / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }
}

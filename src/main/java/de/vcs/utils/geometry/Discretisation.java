package de.vcs.utils.geometry;

import java.util.ArrayList;

public class Discretisation {

    public static ArrayList<Double> generateSRunner(double step, double roadLength) {
        return generateSRunner(step, roadLength, 0.0);
    }

    public static ArrayList<Double> generateSRunner(double step, double length, double start) {
        ArrayList<Double> sRunners = new ArrayList<>();
        double currentS = start;
        while (currentS < length) {
            sRunners.add(currentS);
            currentS += step;
        }
        sRunners.add(length);
        return sRunners;
    }
}

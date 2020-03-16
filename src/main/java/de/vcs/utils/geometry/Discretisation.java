package de.vcs.utils.geometry;

import java.util.ArrayList;

public class Discretisation {

    public static ArrayList<Double> generateSRunner(double step, double roadLength) {
        ArrayList<Double> sRunners = new ArrayList<>();
        double currentS = 0.0;
        while (currentS < roadLength) {
            sRunners.add(currentS);
            currentS += step;
        }
        sRunners.add(roadLength);
        return sRunners;
    }
}

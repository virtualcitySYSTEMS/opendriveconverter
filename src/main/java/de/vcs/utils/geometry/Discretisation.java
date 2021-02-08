package de.vcs.utils.geometry;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.log.ODRLogger;
import de.vcs.utils.math.PolynomHelper;
import org.locationtech.jts.geom.Point;

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

    public static Point getPointGlobal(double sGlobal, double preWidth, double width, Road road) {
        //global polynoms
        AbstractODRGeometry geom;
        Polynom elevationPoly;
        Polynom superElevationPoly;
        Polynom offsetPoly;
        //global Values
        double elevation = 0.0;
        double superElevation = 0.0;
        double offset = 0.0;
        if (!road.getPlanView().getOdrGeometries().isEmpty()) {
            geom = road.getPlanView().getOdrGeometries().floorEntry(sGlobal).getValue();
        }
        if (!road.getElevationProfile().getElevations().isEmpty()) {
            elevationPoly = (Polynom) road.getElevationProfile().getElevations().floorEntry(sGlobal).getValue();
            double SElevationPoly = elevationPoly.getStTransform().getsOffset();
        }
        if (!road.getLateralProfile().getSuperElevations().isEmpty()) {
            superElevationPoly = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(sGlobal).getValue();
            double sSuperElevationPoly = superElevationPoly.getStTransform().getsOffset();
        }
        if (!road.getLanes().getLaneOffsets().isEmpty()) {
            offsetPoly = road.getLanes().getLaneOffsets().floorEntry(sGlobal).getValue();
            double sOffsetPoly = offsetPoly.getStTransform().getsOffset();
        }
        return null;
    }

    public static Point getPointGlobal(double sGlobal, int laneId, Road road) {
        //global polynoms
        AbstractODRGeometry geom;
        Polynom elevationPoly;
        Polynom superElevationPoly;
        Polynom offsetPoly;
        //global Values
        double elevation = 0.0;
        double superElevation = 0.0;
        double offset = 0.0;
        if (!road.getPlanView().getOdrGeometries().isEmpty()) {
            geom = road.getPlanView().getOdrGeometries().floorEntry(sGlobal).getValue();
        }
        if (!road.getElevationProfile().getElevations().isEmpty()) {
            elevationPoly = (Polynom) road.getElevationProfile().getElevations().floorEntry(sGlobal).getValue();
            double SElevationPoly = elevationPoly.getStTransform().getsOffset();
        }
        if (!road.getLateralProfile().getSuperElevations().isEmpty()) {
            superElevationPoly = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(sGlobal).getValue();
            double sSuperElevationPoly = superElevationPoly.getStTransform().getsOffset();
        }
        if (!road.getLanes().getLaneOffsets().isEmpty()) {
            offsetPoly = road.getLanes().getLaneOffsets().floorEntry(sGlobal).getValue();
            double sOffsetPoly = offsetPoly.getStTransform().getsOffset();
        }
        return null;
    }
}

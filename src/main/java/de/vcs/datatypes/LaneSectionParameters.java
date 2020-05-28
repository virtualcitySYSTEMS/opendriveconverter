package de.vcs.datatypes;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.TreeMap;

public class LaneSectionParameters {

    private double absolutSmin;
    private double absoluteSmax;
    private ArrayList<Coordinate> refLine;
    private TreeMap<Integer, Coordinate> lanes;

    public LaneSectionParameters() {
        refLine = new ArrayList<>();
        lanes = new TreeMap<>();
    }

    public LaneSectionParameters(double absolutSmin, double absoluteSmax, ArrayList<Coordinate> refLine, TreeMap<Integer, Coordinate> lanes) {
        this.absolutSmin = absolutSmin;
        this.absoluteSmax = absoluteSmax;
        this.refLine = refLine;
        this.lanes = lanes;
    }

    public double getAbsolutSmin() {
        return absolutSmin;
    }

    public void setAbsolutSmin(double absolutSmin) {
        this.absolutSmin = absolutSmin;
    }

    public double getAbsoluteSmax() {
        return absoluteSmax;
    }

    public void setAbsoluteSmax(double absoluteSmax) {
        this.absoluteSmax = absoluteSmax;
    }

    public ArrayList<Coordinate> getRefLine() {
        return refLine;
    }

    public void setRefLine(ArrayList<Coordinate> refLine) {
        this.refLine = refLine;
    }

    public TreeMap<Integer, Coordinate> getLanes() {
        return lanes;
    }

    public void setLanes(TreeMap<Integer, Coordinate> lanes) {
        this.lanes = lanes;
    }
}

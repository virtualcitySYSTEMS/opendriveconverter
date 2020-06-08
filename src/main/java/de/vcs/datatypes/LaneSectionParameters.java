package de.vcs.datatypes;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.TreeMap;

public class LaneSectionParameters {

    private ArrayList<Point> centerLine;
    private TreeMap<Integer, Point> lanes;

    public LaneSectionParameters() {
        centerLine = new ArrayList<>();
        lanes = new TreeMap<>();
    }

    public ArrayList<Point> getCenterLine() {
        return centerLine;
    }

    public void setCenterLine(ArrayList<Point> centerLine) {
        this.centerLine = centerLine;
    }

    public TreeMap<Integer, Point> getLanes() {
        return lanes;
    }

    public void setLanes(TreeMap<Integer, Point> lanes) {
        this.lanes = lanes;
    }
}

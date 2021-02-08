package de.vcs.datatypes;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.TreeMap;

public class LaneSectionParameters {

    private TreeMap<Integer, ArrayList<Point>> lanes;
    private TreeMap<Integer, ArrayList<Point>> roadMarks;

    public LaneSectionParameters() {
        lanes = new TreeMap<>();
        roadMarks = new TreeMap<>();
    }

    public TreeMap<Integer, ArrayList<Point>> getLanes() {
        return lanes;
    }

    public void setLanes(TreeMap<Integer, ArrayList<Point>> lanes) {
        this.lanes = lanes;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarks() {
        return roadMarks;
    }

    public void setRoadMarks(TreeMap<Integer, ArrayList<Point>> roadMarks) {
        this.roadMarks = roadMarks;
    }
}


package de.vcs.datatypes;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.TreeMap;

public class LaneSectionParameters {

    private TreeMap<Integer, ArrayList<Point>> lanes;

    public LaneSectionParameters() {
        lanes = new TreeMap<>();
    }

    public TreeMap<Integer, ArrayList<Point>> getLanes() {
        return lanes;
    }

    public void setLanes(TreeMap<Integer, ArrayList<Point>> lanes) {
        this.lanes = lanes;
    }
}


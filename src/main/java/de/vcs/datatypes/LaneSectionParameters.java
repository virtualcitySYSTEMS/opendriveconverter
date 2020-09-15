package de.vcs.datatypes;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.TreeMap;

public class LaneSectionParameters {

    private TreeMap<Integer, ArrayList<LaneParameter>> lanes;

    public LaneSectionParameters() {
        lanes = new TreeMap<>();
    }

    public TreeMap<Integer, ArrayList<LaneParameter>> getLanes() {
        return lanes;
    }

    public void setLanes(TreeMap<Integer, ArrayList<LaneParameter>> lanes) {
        this.lanes = lanes;
    }
}


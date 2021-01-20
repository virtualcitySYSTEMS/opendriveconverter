package de.vcs.utils;

import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;

import java.util.TreeMap;

public class ODRHelper {

    public static TreeMap<Integer, Lane> getLanes(LaneSection ls) {
        TreeMap<Integer, Lane> lanes = new TreeMap<>();
        lanes.putAll(ls.getLeftLanes());
        lanes.putAll(ls.getRightLanes());
        return lanes;
    }

}

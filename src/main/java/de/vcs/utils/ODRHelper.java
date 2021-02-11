package de.vcs.utils;

import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;

import java.util.ArrayList;
import java.util.TreeMap;

public class ODRHelper {

    public static TreeMap<Integer, Lane> getLanes(LaneSection ls) {
        TreeMap<Integer, Lane> lanes = new TreeMap<>();
        lanes.putAll(ls.getLeftLanes());
        lanes.putAll(ls.getRightLanes());
        return lanes;
    }

    public static <newType, oldType> ArrayList<newType> castArrayList(ArrayList<oldType> list) {
        ArrayList<newType> newlyCastedArrayList = new ArrayList<newType>();
        for (oldType listObject : list) {
            newlyCastedArrayList.add((newType) listObject);
        }
        return newlyCastedArrayList;
    }
}

package de.vcs.datatypes;

import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.TreeMap;

public class LaneSectionParameters {

    private TreeMap<Integer, ArrayList<Point>> lanes;
    private TreeMap<Integer, ArrayList<RoadMarkPoint>> singleLaneRoadMark;
    private TreeMap<Integer, ArrayList<RoadMarkPoint>> doubleLaneRoadMarkDown;
    private TreeMap<Integer, ArrayList<RoadMarkPoint>> doubleLaneRoadMarkUp;
    private TreeMap<Integer, ArrayList<RoadMarkPoint>> bottsDots;

    public LaneSectionParameters() {
        lanes = new TreeMap<>();
        singleLaneRoadMark = new TreeMap<>();
        doubleLaneRoadMarkUp = new TreeMap<>();
        doubleLaneRoadMarkDown = new TreeMap<>();
        bottsDots = new TreeMap<>();
    }

    public TreeMap<Integer, ArrayList<Point>> getLanes() {
        return lanes;
    }

    public void setLanes(TreeMap<Integer, ArrayList<Point>> lanes) {
        this.lanes = lanes;
    }

    public TreeMap<Integer, ArrayList<RoadMarkPoint>> getSingleLaneRoadMark() {
        return singleLaneRoadMark;
    }

    public void setSingleLaneRoadMark(TreeMap<Integer, ArrayList<RoadMarkPoint>> singleLaneRoadMark) {
        this.singleLaneRoadMark = singleLaneRoadMark;
    }

    public TreeMap<Integer, ArrayList<RoadMarkPoint>> getDoubleLaneRoadMarkDown() {
        return doubleLaneRoadMarkDown;
    }

    public void setDoubleLaneRoadMarkDown(TreeMap<Integer, ArrayList<RoadMarkPoint>> doubleLaneRoadMarkDown) {
        this.doubleLaneRoadMarkDown = doubleLaneRoadMarkDown;
    }

    public TreeMap<Integer, ArrayList<RoadMarkPoint>> getDoubleLaneRoadMarkUp() {
        return doubleLaneRoadMarkUp;
    }

    public void setDoubleLaneRoadMarkUp(TreeMap<Integer, ArrayList<RoadMarkPoint>> doubleLaneRoadMarkUp) {
        this.doubleLaneRoadMarkUp = doubleLaneRoadMarkUp;
    }

    public TreeMap<Integer, ArrayList<RoadMarkPoint>> getBottsDots() {
        return bottsDots;
    }

    public void setBottsDots(TreeMap<Integer, ArrayList<RoadMarkPoint>> bottsDots) {
        this.bottsDots = bottsDots;
    }

    private boolean isLSPTreeMap(int key, LaneSectionParameters lsp) {
        if (this.getLanes().get(key) == null) {
            return false;
        } else {
            return true;
        }
    }

    public void initLSPTreeMap(int minLaneID, int maxLaneID) {
        for (int i = minLaneID; i <= maxLaneID; i++) {
            if (!isLSPTreeMap(i, this)) {
                this.getLanes().put(i, new ArrayList<>());
                this.singleLaneRoadMark.put(i, new ArrayList<>());
                this.doubleLaneRoadMarkDown.put(i, new ArrayList<>());
                this.doubleLaneRoadMarkUp.put(i, new ArrayList<>());
                this.bottsDots.put(i, new ArrayList<>());
            }
        }
    }
}


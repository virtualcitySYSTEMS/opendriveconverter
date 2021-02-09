package de.vcs.datatypes;

import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.TreeMap;

public class LaneSectionParameters {

    private TreeMap<Integer, ArrayList<Point>> lanes;
    private TreeMap<Integer, ArrayList<Point>> roadMarksBottsDots;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidUp;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidDown;
    private TreeMap<Integer, ArrayList<Point>> roadMarksBrokenUp;
    private TreeMap<Integer, ArrayList<Point>> roadMarksBrokenDown;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidSolidUp1;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidSolidDown1;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidSolidUp2;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidSolidDown2;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidBrokenUp1;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidBrokenDown1;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidBrokenUp2;
    private TreeMap<Integer, ArrayList<Point>> roadMarksSolidBrokenDown2;
    private TreeMap<Integer, ArrayList<Point>> roadMarksBrokenSolidUp1;
    private TreeMap<Integer, ArrayList<Point>> roadMarksBrokenSolidDown1;
    private TreeMap<Integer, ArrayList<Point>> roadMarksBrokenSolidUp2;
    private TreeMap<Integer, ArrayList<Point>> roadMarksBrokenSolidDown2;

    public LaneSectionParameters() {
        lanes = new TreeMap<>();
        roadMarksBottsDots = new TreeMap<>();
        roadMarksSolidUp = new TreeMap<>();
        roadMarksSolidDown = new TreeMap<>();
        roadMarksBrokenUp = new TreeMap<>();
        roadMarksBrokenDown = new TreeMap<>();
        roadMarksSolidSolidUp1 = new TreeMap<>();
        roadMarksSolidSolidDown1 = new TreeMap<>();
        roadMarksSolidSolidUp2 = new TreeMap<>();
        roadMarksSolidSolidDown2 = new TreeMap<>();
        roadMarksSolidBrokenUp1 = new TreeMap<>();
        roadMarksSolidBrokenDown1 = new TreeMap<>();
        roadMarksSolidBrokenUp2 = new TreeMap<>();
        roadMarksSolidBrokenDown2 = new TreeMap<>();
        roadMarksBrokenSolidUp1 = new TreeMap<>();
        roadMarksBrokenSolidDown1 = new TreeMap<>();
        roadMarksBrokenSolidUp2 = new TreeMap<>();
        roadMarksBrokenSolidDown2 = new TreeMap<>();
    }

    public TreeMap<Integer, ArrayList<Point>> getLanes() {
        return lanes;
    }

    public void setLanes(TreeMap<Integer, ArrayList<Point>> lanes) {
        this.lanes = lanes;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksBottsDots() {
        return roadMarksBottsDots;
    }

    public void setRoadMarksBottsDots(TreeMap<Integer, ArrayList<Point>> roadMarksBottsDots) {
        this.roadMarksBottsDots = roadMarksBottsDots;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidUp() {
        return roadMarksSolidUp;
    }

    public void setRoadMarksSolidUp(TreeMap<Integer, ArrayList<Point>> roadMarksSolidUp) {
        this.roadMarksSolidUp = roadMarksSolidUp;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidDown() {
        return roadMarksSolidDown;
    }

    public void setRoadMarksSolidDown(TreeMap<Integer, ArrayList<Point>> roadMarksSolidDown) {
        this.roadMarksSolidDown = roadMarksSolidDown;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksBrokenUp() {
        return roadMarksBrokenUp;
    }

    public void setRoadMarksBrokenUp(TreeMap<Integer, ArrayList<Point>> roadMarksBrokenUp) {
        this.roadMarksBrokenUp = roadMarksBrokenUp;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksBrokenDown() {
        return roadMarksBrokenDown;
    }

    public void setRoadMarksBrokenDown(TreeMap<Integer, ArrayList<Point>> roadMarksBrokenDown) {
        this.roadMarksBrokenDown = roadMarksBrokenDown;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidSolidUp1() {
        return roadMarksSolidSolidUp1;
    }

    public void setRoadMarksSolidSolidUp1(TreeMap<Integer, ArrayList<Point>> roadMarksSolidSolidUp1) {
        this.roadMarksSolidSolidUp1 = roadMarksSolidSolidUp1;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidSolidDown1() {
        return roadMarksSolidSolidDown1;
    }

    public void setRoadMarksSolidSolidDown1(TreeMap<Integer, ArrayList<Point>> roadMarksSolidSolidDown1) {
        this.roadMarksSolidSolidDown1 = roadMarksSolidSolidDown1;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidSolidUp2() {
        return roadMarksSolidSolidUp2;
    }

    public void setRoadMarksSolidSolidUp2(TreeMap<Integer, ArrayList<Point>> roadMarksSolidSolidUp2) {
        this.roadMarksSolidSolidUp2 = roadMarksSolidSolidUp2;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidSolidDown2() {
        return roadMarksSolidSolidDown2;
    }

    public void setRoadMarksSolidSolidDown2(TreeMap<Integer, ArrayList<Point>> roadMarksSolidSolidDown2) {
        this.roadMarksSolidSolidDown2 = roadMarksSolidSolidDown2;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidBrokenUp1() {
        return roadMarksSolidBrokenUp1;
    }

    public void setRoadMarksSolidBrokenUp1(TreeMap<Integer, ArrayList<Point>> roadMarksSolidBrokenUp1) {
        this.roadMarksSolidBrokenUp1 = roadMarksSolidBrokenUp1;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidBrokenDown1() {
        return roadMarksSolidBrokenDown1;
    }

    public void setRoadMarksSolidBrokenDown1(TreeMap<Integer, ArrayList<Point>> roadMarksSolidBrokenDown1) {
        this.roadMarksSolidBrokenDown1 = roadMarksSolidBrokenDown1;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidBrokenUp2() {
        return roadMarksSolidBrokenUp2;
    }

    public void setRoadMarksSolidBrokenUp2(TreeMap<Integer, ArrayList<Point>> roadMarksSolidBrokenUp2) {
        this.roadMarksSolidBrokenUp2 = roadMarksSolidBrokenUp2;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksSolidBrokenDown2() {
        return roadMarksSolidBrokenDown2;
    }

    public void setRoadMarksSolidBrokenDown2(TreeMap<Integer, ArrayList<Point>> roadMarksSolidBrokenDown2) {
        this.roadMarksSolidBrokenDown2 = roadMarksSolidBrokenDown2;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksBrokenSolidUp1() {
        return roadMarksBrokenSolidUp1;
    }

    public void setRoadMarksBrokenSolidUp1(TreeMap<Integer, ArrayList<Point>> roadMarksBrokenSolidUp1) {
        this.roadMarksBrokenSolidUp1 = roadMarksBrokenSolidUp1;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksBrokenSolidDown1() {
        return roadMarksBrokenSolidDown1;
    }

    public void setRoadMarksBrokenSolidDown1(TreeMap<Integer, ArrayList<Point>> roadMarksBrokenSolidDown1) {
        this.roadMarksBrokenSolidDown1 = roadMarksBrokenSolidDown1;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksBrokenSolidUp2() {
        return roadMarksBrokenSolidUp2;
    }

    public void setRoadMarksBrokenSolidUp2(TreeMap<Integer, ArrayList<Point>> roadMarksBrokenSolidUp2) {
        this.roadMarksBrokenSolidUp2 = roadMarksBrokenSolidUp2;
    }

    public TreeMap<Integer, ArrayList<Point>> getRoadMarksBrokenSolidDown2() {
        return roadMarksBrokenSolidDown2;
    }

    public void setRoadMarksBrokenSolidDown2(TreeMap<Integer, ArrayList<Point>> roadMarksBrokenSolidDown2) {
        this.roadMarksBrokenSolidDown2 = roadMarksBrokenSolidDown2;
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
                this.roadMarksBottsDots.put(i, new ArrayList<>());
                this.roadMarksSolidUp.put(i, new ArrayList<>());
                this.roadMarksSolidDown.put(i, new ArrayList<>());
                this.roadMarksBrokenUp.put(i, new ArrayList<>());
                this.roadMarksBrokenDown.put(i, new ArrayList<>());
                this.roadMarksSolidSolidUp1.put(i, new ArrayList<>());
                this.roadMarksSolidSolidDown1.put(i, new ArrayList<>());
                this.roadMarksSolidSolidUp2.put(i, new ArrayList<>());
                this.roadMarksSolidSolidDown2.put(i, new ArrayList<>());
                this.roadMarksSolidBrokenUp1.put(i, new ArrayList<>());
                this.roadMarksSolidBrokenDown1.put(i, new ArrayList<>());
                this.roadMarksSolidBrokenUp2.put(i, new ArrayList<>());
                this.roadMarksSolidBrokenDown2.put(i, new ArrayList<>());
                this.roadMarksBrokenSolidUp1.put(i, new ArrayList<>());
                this.roadMarksBrokenSolidDown1.put(i, new ArrayList<>());
                this.roadMarksBrokenSolidUp2.put(i, new ArrayList<>());
                this.roadMarksBrokenSolidDown2.put(i, new ArrayList<>());
            }
        }
    }
}


package de.vcs.datatypes;

import java.util.ArrayList;

public class TestContainer {

    private ArrayList<String> road;
    private ArrayList<String> lane;

    public TestContainer() {
        road = new ArrayList<String>();
        lane = new ArrayList<String>();
    }

    public ArrayList<String> getRoad() {
        return road;
    }

    public void setRoad(ArrayList<String> road) {
        this.road = road;
    }

    public ArrayList<String> getLane() {
        return lane;
    }

    public void setLane(ArrayList<String> lane) {
        this.lane = lane;
    }
}

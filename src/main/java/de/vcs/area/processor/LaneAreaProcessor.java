package de.vcs.area.processor;

import de.vcs.model.odr.road.Road;

import java.util.ArrayList;

public class LaneAreaProcessor extends AbstractAreaProcessor {

    private ArrayList<Road> roads;


    public LaneAreaProcessor(ArrayList<Road> roads) {
        this.roads = roads;
    }

    @Override
    public void processAreas() {
    }
}

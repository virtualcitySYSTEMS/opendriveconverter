package de.vcs.area;

import de.vcs.model.odr.road.Road;

public class RoadAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;

    public RoadAreaGenerator(Road road) {
        this.road = road;
    }

    @Override
    public void generateArea() {
    }
}

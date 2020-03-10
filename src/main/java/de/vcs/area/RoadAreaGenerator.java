package de.vcs.area;

import de.vcs.model.odr.road.Road;

import java.util.ArrayList;

public class RoadAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;

    public RoadAreaGenerator(Road road) {
        this.road = road;
    }

    @Override
    public void generateArea() {
        calcOffsetPoints();
    }

    private void calcOffsetPoints() {
        road.getLanes().getLaneSections().forEach(ls -> System.out.println(ls.getLinearReference().getS()));
    }

    private void calcReferenceLine() {
    }
}

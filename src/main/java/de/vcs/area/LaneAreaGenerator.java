package de.vcs.area;

import de.vcs.model.odr.lane.Lanes;

public class LaneAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Lanes lanes;

    public LaneAreaGenerator(Lanes lanes) {
        this.lanes = lanes;
    }

    @Override
    public void generateArea() {
    }
}

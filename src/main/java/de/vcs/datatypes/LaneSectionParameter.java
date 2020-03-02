package de.vcs.datatypes;

import java.util.HashMap;
import java.util.Map;

public class LaneSectionParameter {

    private double absolutS;
    private double laneOffset;
    private Map<Integer, LaneParameter> laneParameters;

    public LaneSectionParameter() {
        laneParameters = new HashMap<Integer, LaneParameter>();
    }

    public LaneSectionParameter(double absolutS, double laneOffset,
            Map<Integer, LaneParameter> laneParameters) {
        this.absolutS = absolutS;
        this.laneOffset = laneOffset;
        this.laneParameters = laneParameters;
    }

    public double getAbsolutS() {
        return absolutS;
    }

    public void setAbsolutS(double absolutS) {
        this.absolutS = absolutS;
    }

    public double getLaneOffset() {
        return laneOffset;
    }

    public void setLaneOffset(double laneOffset) {
        this.laneOffset = laneOffset;
    }

    public Map<Integer, LaneParameter> getLaneParameters() {
        return laneParameters;
    }

    public void setLaneParameters(Map<Integer, LaneParameter> laneParameters) {
        this.laneParameters = laneParameters;
    }
}

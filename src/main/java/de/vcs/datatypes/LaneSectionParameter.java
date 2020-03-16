package de.vcs.datatypes;

import org.locationtech.jts.geom.Point;

import java.util.HashMap;
import java.util.Map;

public class LaneSectionParameter {

    private double absolutS;
    private double laneOffset;
    private Point refLinePoint;
    private Map<Integer, LaneParameter> laneParameters;

    public LaneSectionParameter() {
        laneParameters = new HashMap<Integer, LaneParameter>();
    }

    public LaneSectionParameter(double absolutS, double laneOffset, Point refLinePoint,
            Map<Integer, LaneParameter> laneParameters) {
        this.absolutS = absolutS;
        this.laneOffset = laneOffset;
        this.refLinePoint = refLinePoint;
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

    public Point getRefLinePoint() {
        return refLinePoint;
    }

    public void setRefLinePoint(Point refLinePoint) {
        this.refLinePoint = refLinePoint;
    }

    public Map<Integer, LaneParameter> getLaneParameters() {
        return laneParameters;
    }

    public void setLaneParameters(Map<Integer, LaneParameter> laneParameters) {
        this.laneParameters = laneParameters;
    }
}

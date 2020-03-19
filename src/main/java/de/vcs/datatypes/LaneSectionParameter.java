package de.vcs.datatypes;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.TreeMap;

public class LaneSectionParameter {

    private double absolutS;
    private Point laneOffsetPoint;
    private Point refLinePoint;
    private TreeMap<Integer, LaneParameter> laneParameters;

    public LaneSectionParameter() {
        laneOffsetPoint = new GeometryFactory().createPoint();
        refLinePoint = new GeometryFactory().createPoint();
        laneParameters = new TreeMap<Integer, LaneParameter>();
    }

    public LaneSectionParameter(double absolutS, Point laneOffsetPoint, Point refLinePoint,
            TreeMap<Integer, LaneParameter> laneParameters) {
        this.absolutS = absolutS;
        this.laneOffsetPoint = laneOffsetPoint;
        this.refLinePoint = refLinePoint;
        this.laneParameters = laneParameters;
    }

    public double getAbsolutS() {
        return absolutS;
    }

    public void setAbsolutS(double absolutS) {
        this.absolutS = absolutS;
    }

    public Point getLaneOffsetPoint() {
        return laneOffsetPoint;
    }

    public void setLaneOffsetPoint(Point laneOffsetPoint) {
        this.laneOffsetPoint = laneOffsetPoint;
    }

    public Point getRefLinePoint() {
        return refLinePoint;
    }

    public void setRefLinePoint(Point refLinePoint) {
        this.refLinePoint = refLinePoint;
    }

    public TreeMap<Integer, LaneParameter> getLaneParameters() {
        return laneParameters;
    }

    public void setLaneParameters(TreeMap<Integer, LaneParameter> laneParameters) {
        this.laneParameters = laneParameters;
    }
}

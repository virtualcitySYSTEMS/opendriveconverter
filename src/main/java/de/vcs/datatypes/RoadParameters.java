package de.vcs.datatypes;

import org.locationtech.jts.geom.Point;

import java.util.ArrayList;

public class RoadParameters {

    private ArrayList<Point> roadPointsMin;
    private ArrayList<Point> roadPointsMax;

    public RoadParameters() {
        roadPointsMin = new ArrayList<>();
        roadPointsMax = new ArrayList<>();
    }

    public RoadParameters(ArrayList<Point> roadPointsMin, ArrayList<Point> roadPointsMax) {
        this.roadPointsMin = roadPointsMin;
        this.roadPointsMax = roadPointsMax;
    }

    public ArrayList<Point> getRoadPointsMin() {
        return roadPointsMin;
    }

    public void setRoadPointsMin(ArrayList<Point> roadPointsMin) {
        this.roadPointsMin = roadPointsMin;
    }

    public ArrayList<Point> getRoadPointsMax() {
        return roadPointsMax;
    }

    public void setRoadPointsMax(ArrayList<Point> roadPointsMax) {
        this.roadPointsMax = roadPointsMax;
    }

    public ArrayList<Point> getPointsPrepared() {
        //Collections.reverse(roadPointsMax);
        roadPointsMin.addAll(roadPointsMax);
        roadPointsMin.add(roadPointsMin.get(0));
        return roadPointsMin;
    }
}
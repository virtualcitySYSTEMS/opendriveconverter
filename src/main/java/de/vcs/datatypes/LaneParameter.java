package de.vcs.datatypes;

import org.locationtech.jts.geom.Coordinate;

public class LaneParameter {

    private double width;
    private Coordinate coord;

    public LaneParameter() {
        coord = new Coordinate();
    }

    public LaneParameter(double width, Coordinate coord) {
        this.width = width;
        this.coord = coord;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public Coordinate getCoord() {
        return coord;
    }

    public void setCoord(Coordinate coord) {
        this.coord = coord;
    }
}

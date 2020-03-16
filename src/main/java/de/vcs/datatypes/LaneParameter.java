package de.vcs.datatypes;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class LaneParameter {

    private double width;
    private Point point;

    public LaneParameter() {
        point = new GeometryFactory().createPoint();
    }

    public LaneParameter(double width, Point point) {
        this.width = width;
        this.point = point;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}

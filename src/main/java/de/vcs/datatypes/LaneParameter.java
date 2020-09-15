package de.vcs.datatypes;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class LaneParameter {

    private AbstractODRGeometry geom;
    private double s;
    private double width;


    public LaneParameter() {
    }

    public LaneParameter(AbstractODRGeometry geom, double s, double width) {
        this.geom = geom;
        this.s = s;
        this.width = width;
    }

    public AbstractODRGeometry getGeom() {
        return geom;
    }

    public void setGeom(AbstractODRGeometry geom) {
        this.geom = geom;
    }

    public double getS() {
        return s;
    }

    public void setS(double s) {
        this.s = s;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }
}

package de.vcs.datatypes;

import org.locationtech.jts.geom.*;

public class LaneSectionPolygon extends MultiPolygon {

    private String laneType;

    public LaneSectionPolygon(Polygon[] polygons, GeometryFactory factory) {
        super(polygons, factory);
    }

    public String getLaneType() {
        return laneType;
    }

    public void setLaneType(String laneType) {
        this.laneType = laneType;
    }
}

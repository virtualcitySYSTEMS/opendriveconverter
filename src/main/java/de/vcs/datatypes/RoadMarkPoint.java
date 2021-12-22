package de.vcs.datatypes;

import de.vcs.model.odr.lane.RoadMark;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class RoadMarkPoint extends Point {

    private RoadMark roadMark;

    public RoadMarkPoint(CoordinateSequence coordinates, GeometryFactory factory) {
        super(coordinates, factory);
    }

    public RoadMark getRoadMark() {
        return roadMark;
    }

    public void setRoadMark(RoadMark roadMark) {
        this.roadMark = roadMark;
    }
}

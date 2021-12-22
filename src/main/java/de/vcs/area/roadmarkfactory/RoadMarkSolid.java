package de.vcs.area.roadmarkfactory;

import de.vcs.datatypes.RoadMarkPoint;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.ArrayList;

public class RoadMarkSolid implements RoadMarkGeometry {

    @Override
    public Geometry createRoadMark(ArrayList<RoadMarkPoint> points) {
        return new GeometryFactory().createPolygon(points2Coordinates(points));
    }
}

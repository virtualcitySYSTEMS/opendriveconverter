package de.vcs.area.roadmarkfactory;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;

public interface RoadMarkGeometry {

    public Geometry createRoadMark(ArrayList<Point> points);
}

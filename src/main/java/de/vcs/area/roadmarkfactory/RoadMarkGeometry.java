package de.vcs.area.roadmarkfactory;

import de.vcs.datatypes.RoadMarkPoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;

public interface RoadMarkGeometry {

    public Geometry createRoadMark(ArrayList<RoadMarkPoint> points);
    default Coordinate[] points2Coordinates(ArrayList<RoadMarkPoint> points) {
        return points.stream().map(Point::getCoordinate).toArray(size -> new Coordinate[size]);
    }
    default Polygon[] polygonList2Array(ArrayList<Polygon> pointsList) {
        Polygon[] polys = new Polygon[pointsList.size()];
        return pointsList.toArray(polys);
    }
}

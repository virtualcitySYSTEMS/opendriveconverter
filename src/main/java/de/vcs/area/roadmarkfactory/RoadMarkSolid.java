package de.vcs.area.roadmarkfactory;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import java.util.ArrayList;

public class RoadMarkSolid implements RoadMarkGeometry {

    @Override
    public Geometry createRoadMark(ArrayList<Point> points) {
        Polygon polygon = (Polygon) ODRGeometryFactory.create(JTSConstants.POLYGON, points);
        return null;
    }
}

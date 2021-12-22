package de.vcs.area.roadmarkfactory;

import de.vcs.area.odrgeometryfactory.ODRLineString;
import de.vcs.area.odrgeometryfactory.ODRPolygon;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.RoadMarkPoint;
import de.vcs.utils.constants.RoadMarkConstants;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;

public class RoadMarkGeometryFactory {

    public static Geometry createRoadMark(String type, ArrayList<RoadMarkPoint> points) {
        if (type.equalsIgnoreCase(RoadMarkConstants.SOLID)) {
            return new RoadMarkSolid().createRoadMark(points);
        } else if (type.equalsIgnoreCase(RoadMarkConstants.CUSTOM)) {
            return new RoadMarkSolid().createRoadMark(points);
        } else if (type.equalsIgnoreCase(RoadMarkConstants.EDGE)) {
            return new RoadMarkSolid().createRoadMark(points);
        } else if (type.equalsIgnoreCase(RoadMarkConstants.GRASS)) {
            return new RoadMarkSolid().createRoadMark(points);
        } else if (type.equalsIgnoreCase(RoadMarkConstants.CURB)) {
            return new RoadMarkSolid().createRoadMark(points);
        } else if (type.equalsIgnoreCase(RoadMarkConstants.BROKEN)) {
            return new RoadMarkBroken().createRoadMark(points);
        } else {
            return null;
        }
    }
}

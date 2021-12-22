package de.vcs.utils.road;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.datatypes.RoadParameters;
import de.vcs.model.odr.road.Road;
import org.locationtech.jts.geom.GeometryFactory;

public class RoadHelper {

    public static void createRoadPolygons(Road road, RoadParameters rp) {
        ODRGeometryFactory factory = new ODRGeometryFactory();
        road.getGmlGeometries().add(factory.create(JTSConstants.POLYGON, rp.getPointsPrepared()));
    }
}
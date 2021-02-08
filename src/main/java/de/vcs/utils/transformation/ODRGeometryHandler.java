package de.vcs.utils.transformation;

import de.vcs.datatypes.RoadMarkPoint;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.lane.RoadMark;
import org.locationtech.jts.geom.Point;

public interface ODRGeometryHandler {

    Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t, double h);
    RoadMarkPoint sth2xyzPoint(AbstractODRGeometry geom, double s, double t, double h, RoadMark roadMark);
    double calcHdg(AbstractODRGeometry geom, double s);
}

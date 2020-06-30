package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import org.locationtech.jts.geom.Point;

public interface ODRGeometryHandler {

    Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t);
}

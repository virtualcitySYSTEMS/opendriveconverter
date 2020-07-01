package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Line;
import org.locationtech.jts.geom.Point;

public class LineHandler implements ODRGeometryHandler {

    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t) {
        if (geom.getClass().equals(Line.class)) {
            Line line = (Line) geom;
            double ds = s - line.getLinearReference().getS();
        } else {
        }
        return null;
    }
}

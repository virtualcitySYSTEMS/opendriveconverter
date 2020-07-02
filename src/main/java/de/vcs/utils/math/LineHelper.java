package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Line;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class LineHelper {
    /**
     *
     * @param line ODR geometry
     * @param ds local s on geometry
     * @param t offset perpendicular to curve
     * @return uv point
     */
    public static Point calcUVPoint(Line line, double ds, double t) {
        return new GeometryFactory().createPoint(new Coordinate(ds, t));
    }
}

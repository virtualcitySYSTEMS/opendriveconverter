package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Arc;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class ArcHelper {
    /**
     *
     * @param arc ODR geometry
     * @param ds local s on geometry
     * @param t offset perpendicular to curve
     * @return uv point
     */
    public static Point calcUVPoint(Arc arc, double ds, double t) {
        double sign = Math.signum(arc.getCurvature());
        double r = Math.abs(1 / arc.getCurvature());
        double theta = ds * (1 / r) + sign * Math.PI / 2;
        return new GeometryFactory().createPoint(new Coordinate(- sign * (r + t) * Math.cos(theta), (r + t) * Math.sin(theta) - sign * r));
    }

    /**
     * calculates the local heading at a point p of the geometry
     * @param arc  ODR geometry
     * @param ds local s on geometry
     * @return heading in radian
     */
    public static double calcLocalHdg(Arc arc, double ds) {
        double r = Math.abs(1 / arc.getCurvature());
        return ds * (1 / r);
    }
}

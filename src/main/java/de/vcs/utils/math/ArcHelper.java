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
     * @return
     */
    public static Point calcUVPoint(Arc arc, double ds, double t) {
        double r_sign = 1 / arc.getCurvature();
        double r = Math.abs(r_sign) + t;
        double theta = ds * (1 / r) + Math.PI / 2;
        return new GeometryFactory().createPoint(new Coordinate(- r * Math.cos(theta), r * Math.sin(theta) + r + r_sign));
    }
}

package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Arc;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class ArcHelperTest {
    @Test
    public void test() {
        // positive curvature
        Arc arc = new Arc(0.5);
        Point point0 = ArcHelper.calcUVPoint(arc, 0.0,0.0);
        Assert.assertEquals("+Arc Point0 X", 0.0, point0.getX(), 1e-10);
        Assert.assertEquals("+Arc Point0 Y", 0.0, point0.getY(), 1e-10);

        Point point2 = ArcHelper.calcUVPoint(arc, Math.PI,1.0);
        Assert.assertEquals("+Arc Point2 with offset X", 3.0, point2.getX(), 1e-10);
        Assert.assertEquals("+Arc Point2 with offset Y", -2.0, point2.getY(), 1e-10);

        // negative curvature
        Arc arc_nc = new Arc(-0.5);
        Point point_0 = ArcHelper.calcUVPoint(arc_nc, 0.0,0.0);
        Assert.assertEquals("-Arc Point0 X", 0.0, point_0.getX(), 1e-10);
        Assert.assertEquals("-Arc Point0 Y", 0.0, point_0.getY(), 1e-10);

        Point point_2 = ArcHelper.calcUVPoint(arc_nc, Math.PI,1.0);
        Assert.assertEquals("-Arc Point2 with offset X", 3.0, point_2.getX(), 1e-10);
        Assert.assertEquals("-Arc Point2 with offset Y", 2.0, point_2.getY(), 1e-10);
    }
}

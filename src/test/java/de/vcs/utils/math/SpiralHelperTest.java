package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Spiral;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Point;

public class SpiralHelperTest {
    @Test
    public void test() {
        Spiral spiral = new Spiral(0.0, 1.0);
        spiral.setLength(1.0);
        Point point0 = SpiralHelper.calcUVPoint(spiral, 0.0,0.0);
        Assert.assertEquals("Spiral Point0 X", 0.0, point0.getX(), 1e-10);
        Assert.assertEquals("Spiral Point0 Y", 0.0, point0.getY(), 1e-10);

        // TODO results seems to be correct, but no reference calculation
//        Point point2 = SpiralHelper.calcUVPoint(spiral, 1.0,0.0);
//        Assert.assertEquals("Spiral Point2 X", 0.97528, point2.getX(), 1e-5);
//        Assert.assertEquals("Spiral Point2 Y", 0.16371, point2.getY(), 1e-5);
    }
}

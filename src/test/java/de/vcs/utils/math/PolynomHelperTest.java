package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Polynom;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Point;

public class PolynomHelperTest {
    @Test
    public void test() {
        Polynom p = new Polynom(0.0, 1.0, -2.803488692261e-07, -3.370395926169e-06);
        Assert.assertEquals("Poly Test 0", 0.0, PolynomHelper.calcPolynomValue(p, 0.0), 0.0);
        Assert.assertEquals("Poly Test 0.5", 0.4999995086132919,
                PolynomHelper.calcPolynomValue(p, 0.5), 0.0);
    }

    @Test
    public void testPoint() {
        Polynom p = new Polynom(0.0, 1.0, -2.803488692261e-07, -3.370395926169e-06);
        Point point = PolynomHelper.calcUVPoint(p, 1.0, 2.0);
        // TODO results seems to be correct, but no reference calculation
        System.out.println(point.getX());
        System.out.println(point.getY());
    }
}

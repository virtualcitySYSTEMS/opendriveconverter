package de.vcs.utils.math;

import de.vcs.model.odr.geometry.ParamPolynom;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Point;

public class ParamPolynomHelperTest {

    @Test
    public void test() {
        ParamPolynom p = new ParamPolynom(0.0, 1.0, -2.803488692261e-07, -3.370395926169e-06, 0.0, 5.551115123126e-17,
                -2.231025430440e-03, -3.050744148436e-06);
        Assert.assertEquals("U ParamPoly3 Test 0", 0.0, ParamPolynomHelper.calcParamPolynomValueU(p, 0.0), 0.0);
        Assert.assertEquals("U ParamPoly3 Test 0.5", 0.4999995086132919,
                ParamPolynomHelper.calcParamPolynomValueU(p, 0.5), 0.0);
        Assert.assertEquals("V ParamPoly3 Test 0", 0.0,
                ParamPolynomHelper.calcParamPolynomValueV(p, 0.0),
                0.0);
        Assert.assertEquals("V ParamPoly3 Test 0.5", -0.0005581377006285267,
                ParamPolynomHelper.calcParamPolynomValueV(p, 0.5), 0.0);
    }

    @Test
    public void testPoint() {
        ParamPolynom p = new ParamPolynom(0.0, 1.0, -2.803488692261e-07, -3.370395926169e-06, 0.0, 5.551115123126e-17,
                -2.231025430440e-03, -3.050744148436e-06);
        Point point = ParamPolynomHelper.calcUVPoint(p, 1.0, 2.0);
        // TODO results seems to be correct, but no reference calculation
        System.out.println(point.getX());
        System.out.println(point.getY());
    }

    @Test
    public void testNormal() {
        ParamPolynom p = new ParamPolynom(0, 1, 1, 1, 0, 1, 1, 1);
        Point point = ParamPolynomHelper.calcNormalVector(p, 12.0);
        System.out.println(ParamPolynomHelper.getFirstDerivationU(p, 12.0));
        System.out.println(point.getX());
        System.out.println(point.getY());
    }
}

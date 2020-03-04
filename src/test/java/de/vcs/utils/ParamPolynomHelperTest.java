package de.vcs.utils;

import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.utils.math.ParamPolynomHelper;
import org.junit.Assert;
import org.junit.Test;

public class ParamPolynomHelperTest {

    @Test
    public void test() {
        ParamPolynom p = new ParamPolynom(0.0, 1.0, -2.803488692261e-07, -3.370395926169e-06, 0.0, 5.551115123126e-17,
                -2.231025430440e-03, -3.050744148436e-06);
        Assert.assertEquals("U ParamPoly3 Test 0", 0.0, ParamPolynomHelper.calcParamPolynomValueU(0.0, p), 0.0);
        Assert.assertEquals("U ParamPoly3 Test 0.5", 0.4999995086132919,
                ParamPolynomHelper.calcParamPolynomValueU(0.5, p), 0.0);
        Assert.assertEquals("V ParamPoly3 Test 0", 0.0,
                ParamPolynomHelper.calcParamPolynomValueV(0.0, p),
                0.0);
        Assert.assertEquals("V ParamPoly3 Test 0.5", -0.0005581377006285267,
                ParamPolynomHelper.calcParamPolynomValueV(0.5, p), 0.0);
    }
}
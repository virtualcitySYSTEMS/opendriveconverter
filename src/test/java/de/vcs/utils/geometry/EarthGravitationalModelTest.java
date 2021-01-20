package de.vcs.utils.geometry;

import de.vcs.utils.transformation.GeoidTransformation;
import org.junit.Assert;
import org.junit.Test;

public class EarthGravitationalModelTest {

    /**
     *
     */
    @Test
    public void testHeightOffsetWGS84() throws Exception {
        // Set up a MathTransform based on the EarthGravitationalModel
        Assert.assertEquals(1.505, GeoidTransformation.getDelta(45, 45, 0), 0.001);
        System.out.println(GeoidTransformation.getDelta(48.76992, 11.439, 373));
        //Assert.assertEquals(1.515, GeoidTransformation.getDelta(45, 45, 1000), 0.001);
        //Assert.assertEquals(46.908, GeoidTransformation.getDelta(0, 45, 0), 0.001);
    }
}
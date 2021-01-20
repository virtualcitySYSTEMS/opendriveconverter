package de.vcs.utils.transformation;

import org.geotools.geometry.DirectPosition3D;
import org.geotools.referencing.operation.DefaultMathTransformFactory;
import org.geotools.referencing.operation.transform.EarthGravitationalModel;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public final class GeoidTransformation {

    public static double getDelta(double lat, double lon, double height) throws TransformException, FactoryException {
        EarthGravitationalModel.Provider provider = new EarthGravitationalModel.Provider();
        DefaultMathTransformFactory factory = new DefaultMathTransformFactory();
        MathTransform mt = factory.createParameterizedTransform(provider.getParameters().createValue());
        DirectPosition3D dest = new DirectPosition3D();
        mt.transform(new DirectPosition3D(lon, lat, height), dest);
        return dest.z;
    }
}


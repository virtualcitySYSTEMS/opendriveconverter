package de.vcs.utils.transformation;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.operation.DefaultMathTransformFactory;
import org.geotools.referencing.operation.transform.EarthGravitationalModel;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public final class GeoidTransformation {

    public static Geometry transformWGSGeoid(Geometry g) throws TransformException, FactoryException {
        if (g.getSRID() == 4326) {
            EarthGravitationalModel.Provider provider = new EarthGravitationalModel.Provider();
            DefaultMathTransformFactory factory = new DefaultMathTransformFactory();
            MathTransform mt = factory.createParameterizedTransform(provider.getParameters().createValue());
            return JTS.transform(g, mt);
        } else {
            return g;
        }
    }
}


package de.vcs.utils.transformation;

import java.lang.reflect.Field;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.operation.DefaultMathTransformFactory;
import org.geotools.referencing.operation.transform.EarthGravitationalModel;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

public class GeoidTransformation {

    private static GeoidTransformation instance;
    EarthGravitationalModel.Provider provider;
    DefaultMathTransformFactory factory;
    static MathTransform mt;
    Field gravaField;

    private GeoidTransformation() {
        provider = new EarthGravitationalModel.Provider();
        factory = new DefaultMathTransformFactory();
        try {
            mt = factory.createParameterizedTransform(provider.getParameters().createValue());
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        try {
            gravaField = mt.getClass().getDeclaredField("grava");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        gravaField.setAccessible(true);
        try {
            gravaField.setDouble(mt, -1 * gravaField.getDouble(mt));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Geometry transformWGSGeoid(Geometry g) throws TransformException, FactoryException {
        return JTS.transform(g, mt);
    }

    public static synchronized GeoidTransformation getInstance() {
        if (instance == null) {
            instance = new GeoidTransformation();
        }
        return instance;
    }
}


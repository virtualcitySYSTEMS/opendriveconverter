package de.vcs.utils.geometry;

import de.vcs.utils.transformation.GeoidTransformation;
import org.junit.Test;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import java.util.Arrays;

public class GeoidTransformationTest {

    @Test
    public void transformWGSGeoidTest() throws TransformException, FactoryException {
        GeoidTransformation geoidTransformation = GeoidTransformation.getInstance();
        Coordinate c1 = new Coordinate(0.5, 0, 0);
        Coordinate c0 = new Coordinate(45, 45, 0);
        Coordinate c2 = new Coordinate(0, 45, 0);
        Coordinate[] coordinats = {c0, c1, c2, c0};
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(45, 45, 100));
        p.setSRID(4326);
        Polygon poly = gf.createPolygon(coordinats);
        poly.setSRID(4326);
        System.out.println(geoidTransformation.transformWGSGeoid(p).getCoordinates()[0].getZ());
        Polygon trans = (Polygon) geoidTransformation.transformWGSGeoid(poly);
        Arrays.stream(trans.getExteriorRing().getCoordinates()).forEach(c -> {
            System.out.println(c.getZ());
        });
    }

    @Test
    public void transformWGSGeoidTest2() throws TransformException, FactoryException {
        GeoidTransformation geoidTransformation = GeoidTransformation.getInstance();
        GeometryFactory gf = new GeometryFactory();
        Point p = gf.createPoint(new Coordinate(11.42299844, 48.77489221, 419.1176692));
        p.setSRID(4326);
        System.out.println(geoidTransformation.transformWGSGeoid(p).getCoordinates()[0].getZ());
    }
}

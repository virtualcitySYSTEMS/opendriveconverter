package de.vcs.utils.geometry;

import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.util.ArrayList;

public class TransformationTest {

    Coordinate c = new Coordinate(3.0, 3.0);
    Coordinate c1 = new Coordinate(3.0, 0.0);
    Coordinate c2 = new Coordinate(0.0, 3.0);

    @Test
    public void rotateOneGeom() {
        Geometry geom = new GeometryFactory().createPoint(c);
        geom = Transformation.transform(geom, 1.5707963267948966, 0.0, 0.0);
        Assert.assertEquals("rotate one geom X value", -3.0, geom.getCoordinate().getX(), 1.0E-15);
        Assert.assertEquals("rotate one geom Y value", 3.0, geom.getCoordinate().getY(), 1.0E-15);
    }

    @Test
    public void rotateGeomArray() {
        ArrayList<Geometry> geoms = new ArrayList<Geometry>();
        Geometry geom1 = new GeometryFactory().createPoint(c1);
        Geometry geom2 = new GeometryFactory().createPoint(c2);
        geoms.add(geom1);
        geoms.add(geom2);
        geoms = Transformation.transform(geoms, Math.toRadians(90.0), 0.0, 0.0);
        Assert.assertEquals("rotate geom array - Geom1 X value", 0.0, geoms.get(0).getCoordinate().getX(), 1.0E-15);
        Assert.assertEquals("rotate geom array - Geom1 Y value", 3.0, geoms.get(0).getCoordinate().getY(), 1.0E-15);
        Assert.assertEquals("rotate geom array - Geom2 X value", -3.0, geoms.get(1).getCoordinate().getX(), 1.0E-15);
        Assert.assertEquals("rotate geom array - Geom2 Y value", 0.0, geoms.get(1).getCoordinate().getY(), 1.0E-15);
    }

    @Test
    public void translateOneGeom() {
        Geometry geom = new GeometryFactory().createPoint(c);
        geom = Transformation.transform(geom, 0.0, -3.0, -3.0);
        Assert.assertEquals("heading no translation Y value", 0.0, geom.getCoordinate().getY(), 1.0E-15);
        Assert.assertEquals("heading no translation X value", 0.0, geom.getCoordinate().getX(), 1.0E-15);
    }

    @Test
    public void translateGeomArray() {
        ArrayList<Geometry> geoms = new ArrayList<Geometry>();
        Geometry geom1 = new GeometryFactory().createPoint(c1);
        Geometry geom2 = new GeometryFactory().createPoint(c2);
        geoms.add(geom1);
        geoms.add(geom2);
        geoms = Transformation.transform(geoms, 0.0, -3.0, -3.0);
        Assert.assertEquals("translate geom array - Geom1 X value", 0.0, geoms.get(0).getCoordinate().getX(), 1.0E-15);
        Assert.assertEquals("translate geom array - Geom1 Y value", -3.0, geoms.get(0).getCoordinate().getY(), 1.0E-15);
        Assert.assertEquals("translate geom array - Geom2 X value", -3.0, geoms.get(1).getCoordinate().getX(), 1.0E-15);
        Assert.assertEquals("translate geom array - Geom2 Y value", 0.0, geoms.get(1).getCoordinate().getY(), 1.0E-15);
    }

    @Test
    public void crsTransform() {
        Coordinate c_ref = new Coordinate(678190.07, 5405309.46, 372.36);
        Geometry geom = new GeometryFactory().createPoint(c_ref);
        Geometry gt_transformed = null;

        String src = "EPSG:25832";
        String trg = "EPSG:4326";
        try {
            CoordinateReferenceSystem sourceCRS = CRS.decode(src);
            CoordinateReferenceSystem targetCRS = CRS.decode(trg);
            boolean geoid = true;
            gt_transformed = Transformation.crsTransform(geom, sourceCRS, targetCRS, geoid);
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }

        Assert.assertEquals("transform - Y = longitude (!)", 11.4254045, gt_transformed.getCoordinate().getY(), 1.0E-6);
        Assert.assertEquals("transform - X = latitude (!)", 48.7752512, gt_transformed.getCoordinate().getX(), 1.0E-6);
    }
}

package de.vcs.utils.geometry;

import de.vcs.utils.transformation.GeoidTransformation;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.ArrayList;
import java.util.Objects;

public class Transformation {

    public static Geometry transform(Geometry geom, double hdg, double xOffset, double yOffset) {
        AffineTransformation trans = new AffineTransformation();
        trans.rotate(hdg);
        trans.translate(xOffset, yOffset);
        return trans.transform(geom);
    }

    public static ArrayList<Geometry> transform(ArrayList<Geometry> geoms, double hdg, double xOffset, double yOffset) {
        ArrayList<Geometry> transformedGeometries = new ArrayList<>();
        AffineTransformation trans = new AffineTransformation();
        trans.rotate(hdg);
        trans.translate(xOffset, yOffset);
        geoms.forEach(g -> transformedGeometries.add(trans.transform(g)));
        return transformedGeometries;
    }

    /**
     * coordinate system transformation
     * @param geom geometry
     * @param sourceCRS source coordinate system
     * @param targetCRS target coordinate system
     * @param geoid flag to apply geoid offset
     * @return transformed geometry
     * @throws FactoryException
     * @throws TransformException
     */
    public static Geometry crsTransform(Geometry geom, CoordinateReferenceSystem sourceCRS,
            CoordinateReferenceSystem targetCRS, boolean geoid) throws FactoryException, TransformException {
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        if (geoid) {
            GeoidTransformation geoidTransformation = GeoidTransformation.getInstance();
            return geoidTransformation.transformWGSGeoid(JTS.transform(geom, transform));
        }
        return JTS.transform(geom, transform);
    }

    /**
     * coordinate system transformation
     * @param geoms geometries
     * @param sourceCRS source coordinate system
     * @param targetCRS target coordinate system
     * @param geoid flag to apply geoid offset
     * @return transformed geometries
     * @throws FactoryException
     * @throws TransformException
     */
    public static ArrayList<Geometry> crsTransform(ArrayList<Geometry> geoms, CoordinateReferenceSystem sourceCRS,
            CoordinateReferenceSystem targetCRS, boolean geoid) throws FactoryException, TransformException {
        GeoidTransformation geoidTransformation = GeoidTransformation.getInstance();
        ArrayList<Geometry> transformedGeometries = new ArrayList<>();
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        geoms.parallelStream().forEach(g -> {
            if (!Objects.isNull(g)) {
                try {
                    if (geoid) {
                        transformedGeometries.add(geoidTransformation.transformWGSGeoid(JTS.transform(g, transform)));
                    }
                    transformedGeometries.add(JTS.transform(g, transform));
                } catch (TransformException | FactoryException e) {
                    e.printStackTrace();
                }
            }
        });
        return transformedGeometries;
    }

    public static Point translatePoint(Point p1, double dx, double dy, double dz) {
        double x = p1.getCoordinate().getX() + dx;
        double y = p1.getCoordinate().getY() + dy;
        double z = p1.getCoordinate().getZ() + dz;
        Coordinate coord = new Coordinate(x, y, z);
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(coord);
    }
}

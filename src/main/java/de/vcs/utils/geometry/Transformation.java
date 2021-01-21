package de.vcs.utils.geometry;

import de.vcs.utils.transformation.GeoidTransformation;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.geometry.STHPosition;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.math.ParamPolynomHelper;
import de.vcs.utils.math.PolynomHelper;
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

    public static Geometry crsTransform(Geometry geom, CoordinateReferenceSystem sourceCRS,
            CoordinateReferenceSystem targetCRS) throws FactoryException, TransformException {
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        return JTS.transform(geom, transform);
    }

    public static ArrayList<Geometry> crsTransform(ArrayList<Geometry> geoms, CoordinateReferenceSystem sourceCRS,
            CoordinateReferenceSystem targetCRS) throws FactoryException, TransformException {
        ArrayList<Geometry> transformedGeometries = new ArrayList<>();
        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        for (Geometry g : geoms) {
            //TODO check if undulation is needed. Perform geoid undulation.
            GeoidTransformation geoidTransformation = GeoidTransformation.getInstance();
            transformedGeometries.add(geoidTransformation.transformWGSGeoid(JTS.transform(g, transform)));
        }
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

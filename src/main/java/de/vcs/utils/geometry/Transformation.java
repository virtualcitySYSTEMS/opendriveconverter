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
            transformedGeometries.add(GeoidTransformation.transformWGSGeoid(JTS.transform(g, transform)));
        }
        return transformedGeometries;
    }
    //TODO delete
/*
    public static Point st2xyPoint(ParamPolynom ppoly, STHPosition sth) {
        return Transformation.st2xyPoint(ppoly, sth.getS(), sth.getT());
    }

    //TODO this is tailored to Road. Could it be general?
    public static Point st2xyPoint(Road road, STHPosition sth) {
        return Transformation.st2xyPoint(road, sth.getS(), sth.getT());
    }

    public static Point st2xyPoint(ParamPolynom ppoly, double s, double t) {
        double ds = s - ppoly.getLinearReference().getS();
        Point point = ParamPolynomHelper.calcUVPoint(ds, ppoly);
        if (t != 0.0) {
            Point normal = ParamPolynomHelper.calcNormalVector(ds, point, ppoly);
            point = ParamPolynomHelper.calcUVPointPerpendicularToCurve(point, normal, t);
        }
        Point xyz = (Point) Transformation.transform(point, ppoly.getIntertialTransform().getHdg(),
                ppoly.getInertialReference().getPos().getValue().get(0),
                ppoly.getInertialReference().getPos().getValue().get(1));
        return xyz;
    }

    //TODO this is tailored to Road. Could it be general?
    public static Point st2xyPoint(Road road, double s, double t) {
        ParamPolynom ppoly = (ParamPolynom) road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
        double ds = s - ppoly.getLinearReference().getS();
        Point point = ParamPolynomHelper.calcUVPoint(ds, ppoly);
        if (t != 0.0) {
            Point normal = ParamPolynomHelper.calcNormalVector(ds, point, ppoly);
            point = ParamPolynomHelper.calcUVPointPerpendicularToCurve(point, normal, t);
        }
        Point xyz = (Point) Transformation.transform(point, ppoly.getIntertialTransform().getHdg(),
                ppoly.getInertialReference().getPos().getValue().get(0),
                ppoly.getInertialReference().getPos().getValue().get(1));
        return xyz;
    }

    //TODO this is tailored to Road. Could it be general?
    public static Point sth2xyzPoint(Road road, STHPosition sth) {
        return Transformation.sth2xyzPoint(road, sth.getS(), sth.getT(), sth.getH());
    }

    //TODO this is tailored to Road. Could it be general?
    public static Point sth2xyzPoint(Road road, double s, double t, double h) {
        Point point = st2xyPoint(road, s, t);
        Polynom poly = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
        double elevation = PolynomHelper.calcPolynomValue(s,poly);
        return new GeometryFactory().createPoint(new Coordinate(point.getX(), point.getY(), elevation + h));
    } */
}

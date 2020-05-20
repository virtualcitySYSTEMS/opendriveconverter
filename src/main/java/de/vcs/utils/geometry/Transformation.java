package de.vcs.utils.geometry;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
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

    public static Point st2xyPoint(Road road, STHPosition sth) {
        return Transformation.st2xyPoint(road, sth.getS(), sth.getT());
    }

    public static Point st2xyPoint(Road road, double s, double t) {
        ParamPolynom ppoly = (ParamPolynom) road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
        Point point = ParamPolynomHelper.calcUVPoint(s, ppoly);
        point = (Point) Transformation.transform(point, ppoly.getIntertialTransform().getHdg(),
                ppoly.getInertialReference().getPos().getValue().get(0),
                ppoly.getInertialReference().getPos().getValue().get(1));
        if (t != 0.0) {
            Point normal = ParamPolynomHelper.calcNormalVector(s, point, ppoly);
            normal = (Point) Transformation.transform(normal, ppoly.getIntertialTransform().getHdg(),
                    ppoly.getInertialReference().getPos().getValue().get(0),
                    ppoly.getInertialReference().getPos().getValue().get(1));
            point = ParamPolynomHelper.calcUVPointPerpendicularToCurve(s, t, point, normal);
        }
        return point;
    }

    public static Point sth2xyzPoint(Road road, STHPosition sth) {
        return Transformation.sth2xyzPoint(road, sth.getS(), sth.getT(), sth.getH());
    }

    public static Point sth2xyzPoint(Road road, double s, double t, double h) {
        Point point = st2xyPoint(road, s, t);
        Polynom poly = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
        double elevation = PolynomHelper.calcPolynomValue(s,poly);
        return new GeometryFactory().createPoint(new Coordinate(point.getX(), point.getY(), elevation + h));
    }
}

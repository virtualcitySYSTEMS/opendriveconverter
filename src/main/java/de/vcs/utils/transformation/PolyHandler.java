package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ODRMath;
import de.vcs.utils.math.ParamPolynomHelper;
import de.vcs.utils.math.PolynomHelper;
import org.locationtech.jts.geom.Point;

public class PolyHandler implements ODRGeometryHandler {

    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t) {
        if (geom.getClass().equals(Polynom.class)) {
            Polynom poly = (Polynom) geom;
            double ds = s - poly.getLinearReference().getS();
            Point point = PolynomHelper.calcUVPoint(poly, ds, t);
            Point xyz = (Point) Transformation.transform(point, poly.getIntertialTransform().getHdg(),
                    poly.getInertialReference().getPos().getValue().get(0),
                    poly.getInertialReference().getPos().getValue().get(1));
            return xyz;
        }
        return null;
    }

    @Override
    public double calcHdg(AbstractODRGeometry geom, double s) {
        if (geom.getClass().equals(Polynom.class)) {
            Polynom poly = (Polynom) geom;
            double ds = s - poly.getLinearReference().getS();
            double localHdg = PolynomHelper.calcLocalHdg(poly, ds);
            return ODRMath.normalizeAngle(poly.getIntertialTransform().getHdg() + localHdg);
        }
        return 0.0;
    }
}

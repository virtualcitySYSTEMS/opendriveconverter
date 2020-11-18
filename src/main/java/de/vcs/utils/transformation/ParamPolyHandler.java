package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ODRMath;
import de.vcs.utils.math.ParamPolynomHelper;
import org.locationtech.jts.geom.Point;

public class ParamPolyHandler implements ODRGeometryHandler {

    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t) {
        if (geom.getClass().equals(ParamPolynom.class)) {
            ParamPolynom ppoly = (ParamPolynom) geom;
            double ds = s - ppoly.getLinearReference().getS();
            Point point = ParamPolynomHelper.calcUVPoint(ppoly, ds, t);
            Point xyz = (Point) Transformation.transform(point, ppoly.getIntertialTransform().getHdg(),
                    ppoly.getInertialReference().getPos().getValue().get(0),
                    ppoly.getInertialReference().getPos().getValue().get(1));
            return xyz;
        }
        return null;
    }

    @Override
    public double calcHdg(AbstractODRGeometry geom, double s) {
        if (geom.getClass().equals(ParamPolynom.class)) {
            ParamPolynom ppoly = (ParamPolynom) geom;
            double ds = s - ppoly.getLinearReference().getS();
            double localHdg = ParamPolynomHelper.calcLocalHdg(ppoly, ds);
            return ODRMath.normalizeAngle(ppoly.getIntertialTransform().getHdg() + localHdg);
        }
        return 0.0;
    }
}

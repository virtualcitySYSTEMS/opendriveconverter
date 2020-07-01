package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.log.ODRLogger;
import de.vcs.utils.math.ParamPolynomHelper;
import org.locationtech.jts.geom.Point;

public class ParamPolyHandler implements ODRGeometryHandler {

    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t) {
        if (geom.getClass().equals(ParamPolynom.class)) {
            ParamPolynom ppoly = (ParamPolynom) geom;
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
        } else {
            return null;
        }
    }
}

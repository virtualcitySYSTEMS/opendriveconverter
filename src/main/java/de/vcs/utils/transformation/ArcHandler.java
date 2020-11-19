package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Arc;
import de.vcs.model.odr.geometry.ParamPolynom;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ArcHelper;
import de.vcs.utils.math.ODRMath;
import de.vcs.utils.math.ParamPolynomHelper;
import org.locationtech.jts.geom.Point;

public class ArcHandler implements ODRGeometryHandler {
    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t) {
        if (geom.equals(Arc.class)) {
            Arc arc = (Arc) geom;
            double ds = s - arc.getLinearReference().getS();
            Point point = ArcHelper.calcUVPoint(arc, ds, t);
            Point xyz = (Point) Transformation.transform(point, arc.getIntertialTransform().getHdg(),
                    arc.getInertialReference().getPos().getValue().get(0),
                    arc.getInertialReference().getPos().getValue().get(1));
            return xyz;
        }
        return null;
    }

    @Override
    public double calcHdg(AbstractODRGeometry geom, double s) {
        if (geom.equals(Arc.class)) {
            Arc arc = (Arc) geom;
            double ds = s - arc.getLinearReference().getS();
            double localHdg = ArcHelper.calcLocalHdg(arc, ds);
            return ODRMath.normalizeAngle(arc.getIntertialTransform().getHdg() + localHdg);
        }
        return 0;
    }
}

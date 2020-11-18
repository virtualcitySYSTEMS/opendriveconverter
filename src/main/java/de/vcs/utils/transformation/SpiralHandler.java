package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.geometry.Spiral;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ODRMath;
import de.vcs.utils.math.PolynomHelper;
import de.vcs.utils.math.SpiralHelper;
import org.locationtech.jts.geom.Point;

public class SpiralHandler implements ODRGeometryHandler {

    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t) {
        if (geom.equals(Spiral.class)) {
            Spiral spiral = (Spiral) geom;
            double ds = s - spiral.getLinearReference().getS();
            Point point = SpiralHelper.calcUVPoint(spiral, ds, t);
            Point xyz = (Point) Transformation.transform(point, spiral.getIntertialTransform().getHdg(),
                    spiral.getInertialReference().getPos().getValue().get(0),
                    spiral.getInertialReference().getPos().getValue().get(1));
            return xyz;
        }
        return null;
    }

    @Override
    public double calcHdg(AbstractODRGeometry geom, double s) {
        if (geom.equals(Spiral.class)) {
            Spiral spiral = (Spiral) geom;
            double ds = s - spiral.getLinearReference().getS();
            double localHdg = SpiralHelper.calcLocalHdg(spiral, ds);
            return ODRMath.normalizeAngle(spiral.getIntertialTransform().getHdg() + localHdg);
        }
        return 0.0;
    }
}

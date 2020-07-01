package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Arc;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ArcHelper;
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
}

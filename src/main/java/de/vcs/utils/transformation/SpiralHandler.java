package de.vcs.utils.transformation;

import de.vcs.datatypes.RoadMarkPoint;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.geometry.Spiral;
import de.vcs.model.odr.lane.RoadMark;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ODRMath;
import de.vcs.utils.math.PolynomHelper;
import de.vcs.utils.math.SpiralHelper;
import org.locationtech.jts.geom.Point;

public class SpiralHandler implements ODRGeometryHandler {

    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t, double h) {
        if (geom.equals(Spiral.class)) {
            Spiral spiral = (Spiral) geom;
            double ds = s - spiral.getLinearReference().getS();
            Point point = SpiralHelper.calcUVPoint(spiral, ds, t);
            Point xyz = (Point) Transformation.transform(point, spiral.getIntertialTransform().getHdg(),
                    spiral.getInertialReference().getPos().getValue().get(0),
                    spiral.getInertialReference().getPos().getValue().get(1));
            xyz.getCoordinate().setZ(h);
            return xyz;
        }
        return null;
    }

    @Override
    public RoadMarkPoint sth2xyzPoint(AbstractODRGeometry geom, double s, double t, double h, RoadMark roadMark) {
        if (geom.equals(Spiral.class)) {
            Spiral spiral = (Spiral) geom;
            double ds = s - spiral.getLinearReference().getS();
            Point point = SpiralHelper.calcUVPoint(spiral, ds, t);
            RoadMarkPoint xyz = (RoadMarkPoint) Transformation.transform(point, spiral.getIntertialTransform().getHdg(),
                    spiral.getInertialReference().getPos().getValue().get(0),
                    spiral.getInertialReference().getPos().getValue().get(1));
            xyz.getCoordinate().setZ(h);
            xyz.setRoadMark(roadMark);
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

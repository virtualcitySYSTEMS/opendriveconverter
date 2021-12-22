package de.vcs.utils.transformation;

import de.vcs.datatypes.RoadMarkPoint;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Arc;
import de.vcs.model.odr.lane.RoadMark;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ArcHelper;
import de.vcs.utils.math.ODRMath;
import org.citygml4j.model.citygml.transportation.Road;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class ArcHandler implements ODRGeometryHandler {

    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t, double h) {
        if (geom.equals(Arc.class)) {
            Arc arc = (Arc) geom;
            double ds = s - arc.getLinearReference().getS();
            Point point = ArcHelper.calcUVPoint(arc, ds, t);
            Point xyz = (Point) Transformation.transform(point, arc.getIntertialTransform().getHdg(),
                    arc.getInertialReference().getPos().getValue().get(0),
                    arc.getInertialReference().getPos().getValue().get(1));
            xyz.getCoordinate().setZ(h);
            return xyz;
        }
        return null;
    }

    @Override
    public RoadMarkPoint sth2xyzPoint(AbstractODRGeometry geom, double s, double t, double h, RoadMark roadMark,
            GeometryFactory factory) {
        if (geom.equals(Arc.class)) {
            Arc arc = (Arc) geom;
            double ds = s - arc.getLinearReference().getS();
            Point point = ArcHelper.calcUVPoint(arc, ds, t);
            Point xyz = (RoadMarkPoint) Transformation.transform(point, arc.getIntertialTransform().getHdg(),
                    arc.getInertialReference().getPos().getValue().get(0),
                    arc.getInertialReference().getPos().getValue().get(1));
            xyz.getCoordinate().setZ(h);
            RoadMarkPoint rmp = new RoadMarkPoint(xyz.getCoordinateSequence(), factory);
            rmp.setRoadMark(roadMark);
            return rmp;
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

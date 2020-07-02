package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Line;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.LineHelper;
import org.locationtech.jts.geom.Point;

public class LineHandler implements ODRGeometryHandler {

    @Override
    public Point sth2xyzPoint(AbstractODRGeometry geom, double s, double t) {
        if (geom.getClass().equals(Line.class)) {
            Line line = (Line) geom;
            double ds = s - line.getLinearReference().getS();
            Point point = LineHelper.calcUVPoint(line, ds, t);
            Point xyz = (Point) Transformation.transform(point, line.getIntertialTransform().getHdg(),
                    line.getInertialReference().getPos().getValue().get(0),
                    line.getInertialReference().getPos().getValue().get(1));
            return xyz;
        }
        return null;
    }
}

package de.vcs.area.generator;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.STHPosition;
import de.vcs.model.odr.object.AbstractObject;
import de.vcs.model.odr.road.Road;
import de.vcs.model.odr.signal.Signal;
import de.vcs.utils.transformation.PointFactory;
import org.locationtech.jts.geom.Point;

public class SignalAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;
    PointFactory pointFactory;

    public SignalAreaGenerator(Road road) {
        this.road = road;
        pointFactory = new PointFactory();
    }

    @Override
    public void generateArea() {
        for (Signal signal : road.getSignals().getSignals()) {
            Point point = createPoint(signal);
            signal.getGmlGeometries().add(point);
        }
    }

    /**
     * returns the signal position as a point geometry
     * sets the inertial heading to the signal
     * @param signal - OpenDRIVE object
     * @return point geometry
     */
    private Point createPoint(Signal signal) {
        STHPosition sth = signal.getLinearReference();
        AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(sth.getS()).getValue();
        Point point = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, sth.getS(), sth.getT());
        double hdg = pointFactory.getODRGeometryHandler(geom.getClass()).calcHdg(geom, sth.getS());
        signal.getInertialTransform().setHdg(hdg + signal.getStTransform().getHdg());
        return point;
    }
}

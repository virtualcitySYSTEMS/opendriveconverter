package de.vcs.area.generator;

import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.Polynom;
import de.vcs.model.odr.road.Road;
import de.vcs.model.odr.signal.Signal;
import de.vcs.utils.log.ODRLogger;
import de.vcs.utils.math.ElevationHelper;
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
        double s = signal.getLinearReference().getS();
        double t = signal.getLinearReference().getT();
        AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
        Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
        Polynom superelevation = null;
        try {
            superelevation = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(s).getValue();
        } catch (Exception e) {
            ODRLogger.getInstance().error("Error creating Signal. Found no superelevation for road with id " + road.getId());
        }
        double h =  ElevationHelper.getElevation(s, t, signal.getInertialTransform().getzOffset(), elevation, superelevation);
        Point point = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, s, t, h);
        double hdg = pointFactory.getODRGeometryHandler(geom.getClass()).calcHdg(geom, s);
        signal.getInertialTransform().setHdg(hdg + signal.getStTransform().getHdg());
        return point;
    }
}

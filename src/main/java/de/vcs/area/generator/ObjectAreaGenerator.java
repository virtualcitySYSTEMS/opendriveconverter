package de.vcs.area.generator;

import de.vcs.model.odr.geometry.*;
import de.vcs.model.odr.object.AbstractObject;
import de.vcs.model.odr.object.Outline;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.geometry.OutlineCreator;
import de.vcs.utils.math.ElevationHelper;
import de.vcs.utils.math.ODRMath;
import de.vcs.utils.transformation.PointFactory;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Map;

public class ObjectAreaGenerator extends AbstractAreaGenerator {

    Road road;
    ArrayList<Double> sRunner;
    PointFactory pointFactory;

    public ObjectAreaGenerator(Road road) {
        this.road = road;
        pointFactory = new PointFactory();
    }

    @Override
    public void generateArea() {
        apply2D();
    }

    //TODO all outlines are 2D => no elevation data for now
    private void apply2D() {
        for (AbstractObject obj : road.getObjects()) {
            Point point = createPoint(obj);
            if (obj.getOutlines().size() > 0) {
                addComplexOutline(obj);
            } else if (obj.getRepeat().size() > 0) {
                addRepeatedOutline(obj);
            } else {
                addSimpleOutline(obj, point);
            }
        }
    }

    /**
     * returns the objects position as a point geometry
     * sets the inertial heading to the object
     * @param obj - OpenDRIVE object
     * @return point geometry
     */
    private Point createPoint(AbstractObject obj) {
        double s = obj.getLinearReference().getS();
        double t = obj.getLinearReference().getT();
        AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
        Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
        Polynom superelevation = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(s).getValue();
        double h = ElevationHelper.getElevation(s, t, obj.getIntertialTransform().getzOffset(), elevation, superelevation);
        Point point = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, s, t, h);
        double hdg = pointFactory.getODRGeometryHandler(geom.getClass()).calcHdg(geom, s);
        obj.getIntertialTransform().setHdg(hdg + obj.getStTransform().getHdg());
        return point;
    }

    /**
     * adds a simple 2D (!) outline created from radius or length and width
     * if object has no outline at all, a point is added
     * @param obj   - OpenDRIVE object
     * @param point - objects position as a point geometry
     */
    private void addSimpleOutline(AbstractObject obj, Point point) {
        if (obj.getRadius() > 0.0) {
            obj.getGmlGeometries().add(OutlineCreator.createCircularOutline(point, obj.getRadius()));
        } else if (obj.getLength() > 0.0 && obj.getWidth() > 0.0) {
            STHPosition sth = obj.getLinearReference();
            AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(sth.getS()).getValue();
            Point p1 = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                    geom,
                    sth.getS() - obj.getLength() / 2,
                    sth.getT() - obj.getWidth() / 2,
                    0.0 //TODO or also with elevation?
                    );
            Point p2 = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                    geom,
                    sth.getS() + obj.getLength() / 2,
                    sth.getT() + obj.getWidth() / 2,
                    0.0 //TODO or also with elevation?
            );
            obj.getGmlGeometries().add(OutlineCreator.createRectangularOutline(p1, p2));
        } else {
            obj.getGmlGeometries().add(point);
        }
    }

    /**
     * adds a complex 2D (!) outline created from cornerRoad or cornerLocal
     *
     * @param obj - OpenDRIVE object
     */
    private void addComplexOutline(AbstractObject obj) {
        for (Outline outline : obj.getOutlines()) {
            CoordinateList coordinates = new CoordinateList();
            if (outline.getCornerRoad() != null) {
                for (STHPosition position : outline.getCornerRoad()) {
                    AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(position.getS()).getValue();
                    //TODO or also with elevation?
                    Point p = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, position.getS(), position.getT(), 0.0);

                    coordinates.add(p.getCoordinate());
                }
            } else {
                STHPosition position = obj.getLinearReference();
                for (UVZPosition uvz : outline.getCornerLocal()) {
                    AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(position.getS()).getValue();
                    Point p = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                            geom,
                            position.getS() + uvz.getU(),
                            position.getT() + uvz.getV(),
                            0.0 //TODO or also with elevation?
                    );
                    coordinates.add(p.getCoordinate());
                }
            }
            coordinates.closeRing();
            GeometryFactory geometryFactory = new GeometryFactory();
            obj.getGmlGeometries().add(geometryFactory.createPolygon(coordinates.toCoordinateArray()));
        }
    }

    private void addRepeatedOutline(AbstractObject obj) {
        if (obj.getRepeat().firstEntry().getValue().getDistance() == 0.0) {
            // continuous object
            ArrayList<Coordinate> inner = new ArrayList<>();
            ArrayList<Coordinate> outer = new ArrayList<>();
            double start = obj.getLinearReference().getS();
            double end = start + obj.getValidLength();
            sRunner = Discretisation.generateSRunner(1.0, end, start);
            sRunner.forEach(s -> {
                STHRepeat repeat = obj.getRepeat().floorEntry(s).getValue();
                double interp = Math.min(s - repeat.getLinearReference().getS(), repeat.getLength()) / repeat.getLength();
                double t = ODRMath.interpolate(repeat.getStart().getT(), repeat.getEnd().getT(), interp);
                double width = ODRMath.interpolate(repeat.getWidthStart(), repeat.getWidthEnd(), interp);
                double zOffset = ODRMath.interpolate(
                        repeat.getStart().getIntertialTransform().getzOffset(),
                        repeat.getEnd().getIntertialTransform().getzOffset(),
                        interp
                );
                AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
                inner.add(pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, s, t - width / 2, 0.0).getCoordinate()); //TODO zOffset
                outer.add(pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, s, t + width / 2, 0.0).getCoordinate()); //TODO zOffset
            });
            obj.getGmlGeometries().add(OutlineCreator.createPolygonalOutline(inner, outer));
        } else {
            // discrete objects
            for (Map.Entry<Double, STHRepeat> entry : obj.getRepeat().entrySet()) {
                STHRepeat repeat = entry.getValue();
                double s = repeat.getLinearReference().getS();
                int i = 0;
                while (s < repeat.getLength()) {
                    AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
                    double interp = Math.min(i * repeat.getDistance(), repeat.getLength()) / repeat.getLength();
                    double t = ODRMath.interpolate(repeat.getStart().getT(), repeat.getEnd().getT(), interp);
                    if (repeat.getRadiusStart() != 0.0 && repeat.getRadiusEnd() != 0.0) {
                        double radius = ODRMath.interpolate(repeat.getRadiusStart(), repeat.getRadiusEnd(), interp);
                        //TODO or also with elevation?
                        Point point = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, s, t, 0.0);
                        obj.getGmlGeometries().add(OutlineCreator.createCircularOutline(point, radius));
                    } else {
                        double length = ODRMath.interpolate(repeat.getLengthStart(), repeat.getLengthEnd(), interp);
                        double width = ODRMath.interpolate(repeat.getWidthStart(), repeat.getWidthEnd(), interp);
                        double zOffset = ODRMath.interpolate(
                                repeat.getStart().getIntertialTransform().getzOffset(),
                                repeat.getEnd().getIntertialTransform().getzOffset(),
                                interp
                        );
                        //TODO zOffset
                        Point p1 = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                                geom,
                                s - length / 2,
                                t - width / 2,
                                0.0 //TODO or also with elevation?
                        );
                        //TODO zOffset
                        Point p2 = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                                geom,
                                s + length / 2,
                                t + width / 2,
                                0.0 //TODO or also with elevation?
                        );
                        obj.getGmlGeometries().add(OutlineCreator.createRectangularOutline(p1, p2));
                    }
                    s += repeat.getDistance();
                    i++;
                }
            }
        }

    }
}

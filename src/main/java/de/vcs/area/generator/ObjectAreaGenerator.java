package de.vcs.area.generator;

import de.vcs.area.odrgeometryfactory.ODRGeometryFactory;
import de.vcs.constants.JTSConstants;
import de.vcs.model.odr.geometry.*;
import de.vcs.model.odr.object.*;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.geometry.OutlineCreator;
import de.vcs.utils.log.ODRLogger;
import de.vcs.utils.math.ElevationHelper;
import de.vcs.utils.math.ODRMath;
import de.vcs.utils.transformation.PointFactory;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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

    //TODO 3D BREP
    private void apply2D() {
        for (AbstractObject obj : road.getObjects()) {
            Point point = createPoint(obj);
            if (obj.getOutlines().size() > 0) {
                addComplexOutline(obj);
            } else if (obj.getRepeat().size() > 0) {
                addRepeatedOutline(obj);
            } else if (obj instanceof Bridge || obj instanceof Tunnel) {
                addLineObject(obj);
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
//      zOffset relative to reference line --> no superelevation, no shape
//        Polynom superelevation = null;
//        if (!road.getLateralProfile().getSuperElevations().isEmpty()) {
//            superelevation = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(s).getValue();
//        }
//        TreeMap<Double, TreeMap<Double, AbstractODRGeometry>> shapes = null;
//        if (!road.getLateralProfile().getShapes().isEmpty()) {
//            shapes = road.getLateralProfile().getShapes();
//        }
        double h = ElevationHelper.getElevation(s, t, elevation, null, null);
        h += obj.getIntertialTransform().getzOffset();
        Point point = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(geom, s, t, h);
        double hdg = pointFactory.getODRGeometryHandler(geom.getClass()).calcHdg(geom, s);
        if (obj.getOrientation() != null && obj.getOrientation().equals(Orientation.MINUS.toString())) {
            hdg += Math.PI / 2;
        }
        if (t < 0) {
            hdg += Math.PI;
        }
        obj.getIntertialTransform().setHdg(hdg + obj.getStTransform().getHdg());
        return point;
    }

    /**
     * adds an elevated, simple 2D (!) outline created from radius or length and width
     * if object has no outline at all, a point is added
     * @param obj   - OpenDRIVE object
     * @param point - objects position as a point geometry
     */
    private void addSimpleOutline(AbstractObject obj, Point point) {
        if (obj.getRadius() > 0.0) {
            // TODO buffer is 2D ! => currently use point 3D
            // OutlineCreator.createCircularOutline(point, obj.getRadius())
            obj.getGmlGeometries().add(point);
        } else if (obj.getLength() > 0.0 && obj.getWidth() > 0.0) {
            double s = obj.getLinearReference().getS();
            double t = obj.getLinearReference().getT();
            AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
            Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
//          zOffset relative to reference line --> no superelevation, no shape
//            Polynom superelevation = null;
//            if (!road.getLateralProfile().getSuperElevations().isEmpty()) {
//                superelevation = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(s).getValue();
//            }
//            TreeMap<Double, TreeMap<Double, AbstractODRGeometry>> shapes = null;
//            if (!road.getLateralProfile().getShapes().isEmpty()) {
//                shapes = road.getLateralProfile().getShapes();
//            }
            double h = ElevationHelper.getElevation(s, t, elevation, null, null);
            h += obj.getIntertialTransform().getzOffset();
            // apply elevation of object center point to all object points, to create simple lod1 shape
            Point p1 = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                    geom,
                    s - obj.getLength() / 2,
                    t - obj.getWidth() / 2,
                    h
            );
            Point p2 = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                    geom,
                    s + obj.getLength() / 2,
                    t + obj.getWidth() / 2,
                    h
            );
            obj.getGmlGeometries().add(OutlineCreator.createRectangularOutline(p1, p2));
        } else {
            obj.getGmlGeometries().add(point);
        }
    }

    /**
     * adds an elevated, complex 2D (!) outline created from cornerRoad or cornerLocal
     * @param obj - OpenDRIVE object
     */
    private void addComplexOutline(AbstractObject obj) {
        for (Outline outline : obj.getOutlines()) {
            CoordinateList coordinates = new CoordinateList();
            if (outline.getCornerRoad() != null) {
                for (CornerRoad cornerRoad : outline.getCornerRoad()) {
                    double s = cornerRoad.getSthPosition().getS();
                    double t = cornerRoad.getSthPosition().getT();
                    double dz = cornerRoad.getSthPosition().getH(); // relative to ref Line !
                    AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
                    Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
//                  zOffset relative to reference line --> no superelevation, no shape
//                    Polynom superelevation = null;
//                    if (!road.getLateralProfile().getSuperElevations().isEmpty()) {
//                        superelevation = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(s).getValue();
//                    }
//                    TreeMap<Double, TreeMap<Double, AbstractODRGeometry>> shapes = null;
//                    if (!road.getLateralProfile().getShapes().isEmpty()) {
//                        shapes = road.getLateralProfile().getShapes();
//                    }
                    Point p = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                            geom,
                            s,
                            t,
                            ElevationHelper.getElevation(s, 0.0, elevation, null, null) + dz
                    );
                    coordinates.add(p.getCoordinate());
                }
            } else {
                double s = obj.getLinearReference().getS();
                double t = obj.getLinearReference().getT();
                for (CornerLocal cornerLocal : outline.getCornerLocal()) {
                    double u = cornerLocal.getUvzPosition().getU();
                    double v = cornerLocal.getUvzPosition().getV();
                    double z = cornerLocal.getUvzPosition().getZ(); // relative to uvz system center
                    AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
                    Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
                    Polynom superelevation = null;
//                  zOffset relative to reference line --> no superelevation, no shape
//                    if (!road.getLateralProfile().getSuperElevations().isEmpty()) {
//                        superelevation = (Polynom) road.getLateralProfile().getSuperElevations().floorEntry(s).getValue();
//                    }
//                    TreeMap<Double, TreeMap<Double, AbstractODRGeometry>> shapes = null;
//                    if (!road.getLateralProfile().getShapes().isEmpty()) {
//                        shapes = road.getLateralProfile().getShapes();
//                    }
                    Point p = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                            geom,
                            s + u,
                            t + v,
                            ElevationHelper.getElevation(s, t, elevation, null, null) + z
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
                Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
                double zOffset = ODRMath.interpolate(
                        repeat.getStart().getIntertialTransform().getzOffset(),
                        repeat.getEnd().getIntertialTransform().getzOffset(),
                        interp
                );
                double h = ElevationHelper.getElevation(s, 0.0, elevation, null, null) + zOffset;
                AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(s).getValue();
                Point innerPoint = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                        geom,
                        s,
                        t - width / 2,
                        h
                );
                inner.add(innerPoint.getCoordinate());
                Point outerPoint = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                        geom,
                        s,
                        t + width / 2,
                        h
                );
                outer.add(outerPoint.getCoordinate());
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
                    Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(s).getValue();
                    double zOffset = ODRMath.interpolate(
                            repeat.getStart().getIntertialTransform().getzOffset(),
                            repeat.getEnd().getIntertialTransform().getzOffset(),
                            interp
                    );
                    double h = ElevationHelper.getElevation(s, 0.0, elevation, null, null) + zOffset;
                    if (repeat.getRadiusStart() != 0.0 && repeat.getRadiusEnd() != 0.0) {
                        double radius = ODRMath.interpolate(repeat.getRadiusStart(), repeat.getRadiusEnd(), interp);
                        Point point = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                                geom,
                                s,
                                t,
                                h
                        );
                        // TODO buffer is 2D ! => currently use point 3D
                        // OutlineCreator.createCircularOutline(point, radius)
                        obj.getGmlGeometries().add(point);
                    } else {
                        double length = ODRMath.interpolate(repeat.getLengthStart(), repeat.getLengthEnd(), interp);
                        double width = ODRMath.interpolate(repeat.getWidthStart(), repeat.getWidthEnd(), interp);
                        Point p1 = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                                geom,
                                s - length / 2,
                                t - width / 2,
                                h
                        );
                        Point p2 = pointFactory.getODRGeometryHandler(geom.getClass()).sth2xyzPoint(
                                geom,
                                s + length / 2,
                                t + width / 2,
                                h
                        );
                        obj.getGmlGeometries().add(OutlineCreator.createRectangularOutline(p1, p2));
                    }
                    s += repeat.getDistance();
                    i++;
                }
            }
        }

    }

    private void addLineObject(AbstractObject obj) {
        ArrayList<Point> points = new ArrayList<>();
        double start = obj.getLinearReference().getS();
        double end = start + obj.getLength();
        sRunner = Discretisation.generateSRunner(1.0, end, start);
        sRunner.forEach(s -> {
            double sGlobal = s;
            AbstractODRGeometry geom = road.getPlanView().getOdrGeometries().floorEntry(sGlobal).getValue();
            Polynom elevation = (Polynom) road.getElevationProfile().getElevations().floorEntry(sGlobal).getValue();
            double h = ElevationHelper.getElevation(s, 0.0, elevation, null, null);
            points.add(pointFactory.getODRGeometryHandler(geom.getClass())
                    .sth2xyzPoint(geom, sGlobal, 0.0, h));
        });
        obj.getGmlGeometries().add(ODRGeometryFactory.create(JTSConstants.LINESTRING, points));
    }
}

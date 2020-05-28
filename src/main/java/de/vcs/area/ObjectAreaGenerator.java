package de.vcs.area;

import de.vcs.datatypes.CoordinateSet;
import de.vcs.model.odr.geometry.STHPosition;
import de.vcs.model.odr.geometry.STHRepeat;
import de.vcs.model.odr.geometry.UVZPosition;
import de.vcs.model.odr.object.AbstractObject;
import de.vcs.model.odr.object.Outline;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Discretisation;
import de.vcs.utils.geometry.OutlineCreator;
import de.vcs.utils.geometry.Transformation;
import de.vcs.utils.math.ODRMath;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;
import java.util.Map;

public class ObjectAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;
    ArrayList<Double> sRunner;

    public ObjectAreaGenerator(Road road) {
        this.road = road;
    }

    @Override
    public void generateArea() {
        apply2D();
    }

    private void apply2D() {
        for(AbstractObject obj : road.getObjects()) {
            if (obj.getOutlines().size() > 0) {
                addComplexOutline(obj);
            } else if (obj.getRepeat().size() > 0) {
                addRepeatedOutline(obj);
            } else {
                Point point = addPoint(obj);
                addSimpleOutline(obj, point);
            }
        }
    }

    /**
     * adds the objects position as a point geometry
     * @param obj - OpenDRIVE object
     * @return point geometry
     */
    private Point addPoint(AbstractObject obj) {
        Point point = Transformation.st2xyPoint(road, obj.getLinearReference());
        obj.getGmlGeometries().add(point);
        return point;
    }

    /**
     * adds a simple 2D (!) outline created from radius or length and width
     * @param obj - OpenDRIVE object
     * @param point - objects position as a point geometry
     */
    private void addSimpleOutline(AbstractObject obj, Point point) {
        if (obj.getRadius() > 0.0) {
            obj.getGmlGeometries().add(OutlineCreator.createCircularOutline(point, obj.getRadius()));
        } else if (obj.getLength() > 0.0 && obj.getWidth() > 0.0) {
            obj.getGmlGeometries().add(OutlineCreator.createRectangularOutline(road, obj.getLinearReference(), obj.getLength(), obj.getWidth()));
        }
    }

    /**
     * adds a complex 2D (!) outline created from cornerRoad or cornerLocal
     * @param obj - OpenDRIVE object
     */
    private void addComplexOutline(AbstractObject obj) {
        for(Outline outline : obj.getOutlines()) {
            CoordinateList coordinates = new CoordinateList();
            if (outline.getCornerRoad() != null) {
                for (STHPosition position : outline.getCornerRoad()) {
                    Point p = Transformation.st2xyPoint(road, position);
                    coordinates.add(p.getCoordinate());
                }
            } else {
                STHPosition position = obj.getLinearReference();
                for (UVZPosition uvz : outline.getCornerLocal()) {
                    Point p = Transformation.st2xyPoint(road, position.getS() + uvz.getU(), position.getT() + uvz.getV());
                    coordinates.add(p.getCoordinate());
                }
            }
            GeometryFactory geometryFactory = new GeometryFactory();
            obj.getGmlGeometries().add(geometryFactory.createPolygon(coordinates.toCoordinateArray()));
        }
    }

    private void addRepeatedOutline(AbstractObject obj) {
        if (obj.getRepeat().firstEntry().getValue().getDistance() == 0.0) {
            // continuous object
            CoordinateSet set = new CoordinateSet();
            ArrayList<Coordinate> inner = new ArrayList<>();
            ArrayList<Coordinate> outer = new ArrayList<>();
            double start = obj.getRepeat().firstEntry().getValue().getLinearReference().getS();
            double end = obj.getRepeat().lastEntry().getValue().getLinearReference().getS();
            sRunner = Discretisation.generateSRunner(2.0, end, start);
            sRunner.forEach(s -> {
                STHRepeat repeat = obj.getRepeat().floorEntry(s).getValue();
                double t = ODRMath.interpolate(repeat.getStart().getT(), repeat.getEnd().getT(), (s - repeat.getLinearReference().getS()) / repeat.getLength());
                double width = ODRMath.interpolate(repeat.getWidthStart(), repeat.getWidthEnd(), (s - repeat.getLinearReference().getS()) / repeat.getLength());
                double zOffset = ODRMath.interpolate(
                        repeat.getStart().getIntertialTransform().getzOffset(),
                        repeat.getEnd().getIntertialTransform().getzOffset(),
                        (s - repeat.getLinearReference().getS()) / repeat.getLength()
                );
                CoordinateSet param = new CoordinateSet();
                inner.add(Transformation.sth2xyzPoint(road, s,t - width / 2, zOffset).getCoordinate());
                outer.add(Transformation.sth2xyzPoint(road, s,t + width / 2, zOffset).getCoordinate());
            });
            set.setPoints(0, inner);
            set.setPoints(1, outer);
            obj.getGmlGeometries().add(OutlineCreator.createPolygonalOutline(inner, outer));
        } else {
            // discrete objects
            for (Map.Entry<Double, STHRepeat> entry : obj.getRepeat().entrySet()) {
                STHRepeat repeat = entry.getValue();
                double s = repeat.getLinearReference().getS();
                int i = 0;
                while (s < repeat.getLength()) {
                    double t = ODRMath.interpolate(repeat.getStart().getT(), repeat.getEnd().getT(), i * repeat.getDistance() / repeat.getLength());
                    if (repeat.getRadiusStart() != 0.0 && repeat.getRadiusEnd() != 0.0) {
                        double radius = ODRMath.interpolate(repeat.getRadiusStart(), repeat.getRadiusEnd(), i * repeat.getDistance() / repeat.getLength());
                        Point point = Transformation.st2xyPoint(road, s, t);
                        obj.getGmlGeometries().add(OutlineCreator.createCircularOutline(point, radius));
                    } else {
                        double length = ODRMath.interpolate(repeat.getLengthStart(), repeat.getLengthEnd(), i * repeat.getDistance() / repeat.getLength());
                        double width = ODRMath.interpolate(repeat.getWidthStart(), repeat.getWidthEnd(), i * repeat.getDistance() / repeat.getLength());
                        double zOffset = ODRMath.interpolate(
                                repeat.getStart().getIntertialTransform().getzOffset(),
                                repeat.getEnd().getIntertialTransform().getzOffset(),
                                i * repeat.getDistance() / repeat.getLength()
                        );
                        obj.getGmlGeometries().add(OutlineCreator.createRectangularOutline(road, new STHPosition(s, t, zOffset), length, width));
                    }
                    s += repeat.getDistance();
                    i++;
                }
            }
        }

    }
}

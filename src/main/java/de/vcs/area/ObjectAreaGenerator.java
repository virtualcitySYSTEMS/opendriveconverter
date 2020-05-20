package de.vcs.area;

import de.vcs.model.odr.geometry.STHPosition;
import de.vcs.model.odr.geometry.UVZPosition;
import de.vcs.model.odr.object.AbstractObject;
import de.vcs.model.odr.object.Outline;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Transformation;
import org.locationtech.jts.geom.*;

import java.util.ArrayList;

public class ObjectAreaGenerator extends AbstractAreaGenerator implements AreaGenerator {

    Road road;

    public ObjectAreaGenerator(Road road) {
        this.road = road;
    }

    @Override
    public void generateArea() {
        apply();
    }

    private void apply() {
        for(AbstractObject obj : road.getObjects()) {
            if (obj.getOutlines().size() < 1) {
                Point point = addPoint(obj);
                addSimpleOutline(obj, point);
            } else {
                addComplexOutline(obj);
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
            Polygon outline = (Polygon) point.buffer(obj.getRadius());
            obj.getGmlGeometries().add(outline);
        } else if (obj.getLength() > 0.0 && obj.getWidth() > 0.0) {
            Point p1 = Transformation.st2xyPoint(
                    road,
                    obj.getLinearReference().getS() - obj.getLength() / 2,
                    obj.getLinearReference().getT() - obj.getWidth() / 2
            );
            Point p2 = Transformation.st2xyPoint(
                    road,
                    obj.getLinearReference().getS() + obj.getLength() / 2,
                    obj.getLinearReference().getT() + obj.getLength() / 2
            );
            Coordinate[] coordinates = new Coordinate[]{
                    p1.getCoordinate(),
                    new Coordinate( p2.getX(), p1.getY()),
                    p2.getCoordinate(),
                    new Coordinate(p1.getX(), p2.getY()),
                    p1.getCoordinate(),
            };
            GeometryFactory geometryFactory = new GeometryFactory();
            obj.getGmlGeometries().add(geometryFactory.createPolygon(coordinates));
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
}

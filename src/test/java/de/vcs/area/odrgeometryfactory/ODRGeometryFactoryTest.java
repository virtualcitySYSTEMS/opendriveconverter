package de.vcs.area.odrgeometryfactory;

import de.vcs.constants.JTSConstants;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;

public class ODRGeometryFactoryTest {

    Coordinate c1 = new Coordinate(1.0, 1.0);
    Coordinate c2 = new Coordinate(2.0, 2.0);
    Coordinate c3 = new Coordinate(3.0, 3.0);

    @Test
    public void odrLineString() {

        GeometryFactory fac = new GeometryFactory();


        ArrayList<Point> points = new ArrayList<>();
        points.add(fac.createPoint(c1));
        points.add(fac.createPoint(c2));
        points.add(fac.createPoint(c3));


        Coordinate[] coord = points.stream().map(Point::getCoordinate).toArray(size -> new Coordinate[size]);
//
//        ODRGeometryFactory.create(JTSConstants.LINESTRING, points);
    }
}

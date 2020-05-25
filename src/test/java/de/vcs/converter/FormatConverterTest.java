package de.vcs.converter;

import de.vcs.model.odr.core.OpenDRIVE;
import de.vcs.model.odr.geometry.STHPosition;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.object.GenericObject;
import de.vcs.model.odr.road.Road;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FormatConverterTest {

    @Test
    public void testGeoJSON() {

        // prepare road with lane
        Road road = new Road();
        road.setId("test123");
        road.setName("testRoad");
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate( 678190.07, 5405309.46),
                new Coordinate(678186.25, 5405317.65),
                new Coordinate(678198.48, 5405323.62),
                new Coordinate( 678201.47, 5405315.31),
                new Coordinate(678190.07, 5405309.46),
        };
        Polygon polygon = geometryFactory.createPolygon(coordinates);
        road.getGmlGeometries().add(polygon);
        LineString line = geometryFactory.createLineString(coordinates);
        road.getGmlGeometries().add(line);
        Lane lane = new Lane();
        lane.setId(1);
        lane.getGmlGeometries().add(polygon);
        LaneSection laneSection = new LaneSection();
        laneSection.getLeftLanes().put(1, lane);
        road.getLanes().getLaneSections().put(0.0, laneSection);
        // add object
        GenericObject obj = new GenericObject("vegetation", "tree", false);
        obj.setId("tree123");
        obj.setName("Eiche");
        obj.getGmlGeometries().add(polygon);
        road.getObjects().add(obj);

        // create odr
        OpenDRIVE odr = new OpenDRIVE();
        odr.getHeader().getGeoReference().setEpsg("EPSG:25832");
        odr.getRoads().add(road);

        // output
        File testRoadRefLine = new File("src/main/resources/testRoadRefLine.json");
        File testRoadPolygon = new File("src/main/resources/testRoadPolygon.json");
        File testLanePolygon = new File("src/main/resources/testLanePolygon.json");
        File testObjectsPolygon = new File("src/main/resources/testObjectsPolygon.json");


        List<FormatConverter> converters = new ArrayList<>();
        converters.add(new GeoJsonConverter(GeoJsonConverter::convertReferenceLine, testRoadRefLine));
        converters.add(new GeoJsonConverter(GeoJsonConverter::convertRoads, testRoadPolygon));
        converters.add(new GeoJsonConverter(GeoJsonConverter::convertLanes, testLanePolygon));
        converters.add(new GeoJsonConverter(GeoJsonConverter::convertObjects, testObjectsPolygon));
        converters.forEach(c -> {
            try {
                c.write(c.convertFromODR(odr));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // test file exists
        Assert.assertTrue(testRoadRefLine.exists());
        Assert.assertTrue(testRoadPolygon.exists());
        Assert.assertTrue(testLanePolygon.exists());
        Assert.assertTrue(testObjectsPolygon.exists());
    }
}

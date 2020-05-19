package de.vcs.converter;

import de.vcs.model.odr.core.OpenDRIVE;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.road.Road;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.xmlobjects.XMLObjects;
import org.xmlobjects.XMLObjectsException;
import org.xmlobjects.builder.ObjectBuildException;
import org.xmlobjects.stream.XMLReadException;
import org.xmlobjects.stream.XMLReader;
import org.xmlobjects.stream.XMLReaderFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FormatConverterTest {

    @Test
    public void test() {

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
        Lane lane = new Lane();
        lane.setId(1);
        lane.getGmlGeometries().add(polygon);
        LaneSection laneSection = new LaneSection();
        laneSection.getLeftLanes().put(1, lane);
        road.getLanes().getLaneSections().put(0.0, laneSection);

        // create odr
        OpenDRIVE odr = new OpenDRIVE();
        odr.getHeader().getGeoReference().setEpsg("EPSG:25832");
        odr.getRoads().add(road);

        // output
        File testRoadPolygon = new File("src/main/resources/testRoadPolygon.json");
        File testLanePolygon = new File("src/main/resources/testLanePolygon.json");

        List<FormatConverter> converters = new ArrayList<>();
        converters.add(new GeoJsonConverter(GeoJsonConverter::convertRoads, testRoadPolygon));
        converters.add(new GeoJsonConverter(GeoJsonConverter::convertLanes, testLanePolygon));
        converters.forEach(c -> {
            try {
                c.write(c.convertFromODR(odr));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // test file exists
        Assert.assertTrue(testRoadPolygon.exists());
        Assert.assertTrue(testLanePolygon.exists());
    }
}

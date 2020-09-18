package de.vcs.converter;

import de.vcs.model.odr.core.OpenDRIVE;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.object.*;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.geometry.Transformation;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.*;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;

public class GeoJsonConverter extends FormatConverter<GeoJsonFormat> {

    public GeoJsonConverter(Function<OpenDRIVE, GeoJsonFormat> fromODR, File outputFile) {
        super(fromODR, outputFile);
    }

    /**
     * Creates a top level feature for each OpenDRIVE Road Object represented by its reference line
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of reference line LineStrings
     */
    public static GeoJsonFormat convertReferenceLine(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem("EPSG:25832");
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");

            SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
            featureTypeBuilder.setName("FEATURE_TYPE");
            featureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);
            featureTypeBuilder.add("GEOMETRY", LineString.class);
            featureTypeBuilder.add("Name", String.class);
            featureTypeBuilder.setDefaultGeometry("GEOMETRY");
            SimpleFeatureType fType = featureTypeBuilder.buildFeatureType();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(fType);

            for (Road road : odr.getRoads()) {
                road.getLanes().getLaneSections().values().forEach(ls -> {
                    ArrayList<Geometry> lines = ls.getCenterLane().getGmlGeometries();
                    lines.removeIf(g -> !(g instanceof LineString));
                    try {
                        lines = Transformation.crsTransform(lines, sourceCRS, targetCRS);
                    } catch (FactoryException | TransformException e) {
                        e.printStackTrace();
                    }
                    for (Geometry line : lines) {
                        // i++ for unique id and name ???
                        featureBuilder.reset();
                        featureBuilder.add(line);
                        //TODO find UUID for lanes. Roadid + LaneSectionID + LaneID?
                        SimpleFeature roadFeature = featureBuilder.buildFeature(String.valueOf(UUID.randomUUID()));
                        roadFeature.setAttribute("Name", road.getName());
                        geojson.getFeatures().add(roadFeature);
                    }
                });


            }
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    /**
     * Creates a top level feature for each OpenDRIVE Road Object represented by its area
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of road polygons
     */
    public static GeoJsonFormat convertRoads(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem(odr.getHeader().getGeoReference().getEpsg());
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");

            SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
            featureTypeBuilder.setName("FEATURE_TYPE");
            featureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);
            featureTypeBuilder.add("GEOMETRY", MultiPolygon.class); //TODO: change geometry type ???
            featureTypeBuilder.add("Name", String.class);
            featureTypeBuilder.setDefaultGeometry("GEOMETRY");
            SimpleFeatureType fType = featureTypeBuilder.buildFeatureType();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(fType);
            GeometryFactory geomFactory = new GeometryFactory();

            for (Road road : odr.getRoads()) {
                ArrayList<Geometry> polygons = road.getGmlGeometries();
                polygons.removeIf(g -> !(g instanceof Polygon));
                polygons = Transformation.crsTransform(polygons, sourceCRS, targetCRS);
                Polygon[] polygonArray = new Polygon[polygons.size()];
                MultiPolygon multiPolygon = geomFactory.createMultiPolygon(polygons.toArray(polygonArray));
                featureBuilder.add(multiPolygon);
                SimpleFeature roadFeature = featureBuilder.buildFeature(String.valueOf(road.getId()));
                roadFeature.setAttribute("Name", road.getName());
                geojson.getFeatures().add(roadFeature);
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    /**
     * Creates a top level feature for each OpenDRIVE Lane Object represented by its area
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of lane polygons
     */
    public static GeoJsonFormat convertLanes(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem("EPSG:25832");
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");

            SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
            featureTypeBuilder.setName("FEATURE_TYPE");
            featureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);
            featureTypeBuilder.add("GEOMETRY", MultiPolygon.class); //TODO: change geometry type ???
            featureTypeBuilder.add("RoadId", String.class);
            featureTypeBuilder.add("LaneSection", Double.class);
            featureTypeBuilder.add("LaneId", String.class);
            featureTypeBuilder.setDefaultGeometry("GEOMETRY");
            SimpleFeatureType fType = featureTypeBuilder.buildFeatureType();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(fType);
            GeometryFactory geomFactory = new GeometryFactory();

            for (Road road : odr.getRoads()) {
                for (Map.Entry<Double, LaneSection> e : road.getLanes().getLaneSections().entrySet()) {
                    Double s = e.getKey();
                    LaneSection laneSection = e.getValue();
                    for (Map.Entry<Integer, Lane> entry : getLanes(laneSection).entrySet()) {
                        Integer laneId = entry.getKey();
                        Lane lane = entry.getValue();
                        ArrayList<Geometry> polygons = lane.getGmlGeometries();
                        polygons.removeIf(g -> !(g instanceof Polygon));
                        polygons = Transformation.crsTransform(polygons, sourceCRS, targetCRS);
                        Polygon[] polygonArray = new Polygon[polygons.size()];
                        MultiPolygon multiPolygon = geomFactory.createMultiPolygon(polygons.toArray(polygonArray));
                        featureBuilder.add(multiPolygon);
                        SimpleFeature laneFeature = featureBuilder.buildFeature(UUID.randomUUID().toString());
                        laneFeature.setAttribute("RoadId", road.getId());
                        laneFeature.setAttribute("LaneSection", s);
                        laneFeature.setAttribute("LaneId", laneId);
                        geojson.getFeatures().add(laneFeature);
                    }
                }
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    // TODO move to utils?
    public static TreeMap<Integer, Lane> getLanes(LaneSection ls) {
        TreeMap<Integer, Lane> lanes = new TreeMap<>();
        lanes.putAll(ls.getLeftLanes());
        lanes.putAll(ls.getRightLanes());
        return lanes;
    }

    public static GeoJsonFormat convertObjects(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem(odr.getHeader().getGeoReference().getEpsg());
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");

            SimpleFeatureTypeBuilder featureTypeBuilder = new SimpleFeatureTypeBuilder();
            featureTypeBuilder.setName("FEATURE_TYPE");
            featureTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);
            featureTypeBuilder.add("GEOMETRY", MultiPolygon.class);
            featureTypeBuilder.add("id", String.class);
            featureTypeBuilder.add("name", String.class);
            featureTypeBuilder.add("type", String.class);
            featureTypeBuilder.add("subtype", String.class);
            featureTypeBuilder.setDefaultGeometry("GEOMETRY");
            SimpleFeatureType fType = featureTypeBuilder.buildFeatureType();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(fType);
            GeometryFactory geomFactory = new GeometryFactory();

            for (Road road : odr.getRoads()) {
                for (AbstractObject obj : road.getObjects()) {
                    ArrayList<Geometry> polygons = obj.getGmlGeometries();
                    polygons.removeIf(g -> !(g instanceof Polygon));
                    polygons = Transformation.crsTransform(polygons, sourceCRS, targetCRS);
                    Polygon[] polygonArray = new Polygon[polygons.size()];
                    MultiPolygon multiPolygon = geomFactory.createMultiPolygon(polygons.toArray(polygonArray));
                    featureBuilder.add(multiPolygon);
                    SimpleFeature objFeature = featureBuilder.buildFeature(obj.getId());
                    objFeature.setAttribute("name", obj.getName());
                    if (obj instanceof GenericObject) {
                        objFeature.setAttribute("subtype", ((GenericObject) obj).getSubtype());
                        objFeature.setAttribute("type", ((GenericObject) obj).getType());
                    } else if (obj instanceof Bridge) {
                        objFeature.setAttribute("type", ((Bridge) obj).getType());
                    } else if (obj instanceof Tunnel) {
                        objFeature.setAttribute("type", ((Tunnel) obj).getType());
                    } else if (obj instanceof ObjectReference) {
                        continue;
                    }
                    geojson.getFeatures().add(objFeature);
                }
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }


    @Override
    public void write(GeoJsonFormat format) throws IOException {
        int decimals = 15;
        GeometryJSON gjson = new GeometryJSON(decimals);
        FeatureJSON fjson = new FeatureJSON(gjson);
        StringWriter writer = new StringWriter();
        SimpleFeatureCollection simpleCollection = DataUtilities.collection(format.getFeatures());
        fjson.writeFeatureCollection(simpleCollection, writer);
        String json = writer.toString();
        OutputStream out = new FileOutputStream(this.getOutputFile());
        out.write(json.getBytes(StandardCharsets.UTF_8));
        out.close();
    }
}

package de.vcs.converter;

import de.vcs.model.odr.core.AbstractOpenDriveElement;
import de.vcs.model.odr.core.OpenDRIVE;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.lane.RoadMark;
import de.vcs.model.odr.object.*;
import de.vcs.model.odr.signal.Signal;
import de.vcs.model.odr.road.Road;
import de.vcs.utils.ODRHelper;
import de.vcs.utils.geometry.Transformation;
import org.apache.commons.lang3.ClassUtils;
import org.geotools.referencing.CRS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.locationtech.jts.geom.*;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.locationtech.jts.io.geojson.*;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

public class GeoJsonConverter extends FormatConverter<GeoJsonFormat> {

    public GeoJsonConverter(Function<OpenDRIVE, GeoJsonFormat> fromODR, File outputFile) {
        super(fromODR, outputFile);
    }

    /**
     * Creates a top level feature for each OpenDRIVE Road represented by its reference line
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of reference line LineStrings
     */
    public static GeoJsonFormat convertReferenceLine(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem("EPSG:25832");
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                road.getLanes().getLaneSections().values().forEach(ls -> {
                    ArrayList<Geometry> lines = ls.getCenterLane().getGmlGeometries();
                    lines.removeIf(g -> !(g instanceof LineString));
                    try {
                        lines = Transformation.crsTransform(lines, sourceCRS, targetCRS, geoid);
                    } catch (FactoryException | TransformException e) {
                        e.printStackTrace();
                    }
                    lines.stream().forEach(f -> {
                        JSONObject feature = createFeature(f);
                        //TODO check if properties of geometries is causing issue
                        JSONObject properties = getProperties(ls.getCenterLane());
                        properties.put("roadId", road.getId());
                        properties.put("name", road.getName());
                        properties.put("sOffset", ls.getLinearReference().getS());
                        feature.put("properties", properties);
                        geojson.getFeatures().add(feature);
                    });
                });
            }
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    public static GeoJsonFormat convertLaneBreakLines(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        GeometryFactory geometryFactory = new GeometryFactory();
        try {
            sourceCRS = factory.createCoordinateReferenceSystem("EPSG:25832");
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                for (Map.Entry<Double, LaneSection> e : road.getLanes().getLaneSections().entrySet()) {
                    Double s = e.getKey();
                    LaneSection laneSection = e.getValue();
                    for (Map.Entry<Integer, Lane> entry : ODRHelper.getLanes(laneSection).entrySet()) {
                        Lane lane = entry.getValue();
                        String junction = road.getJunction();
                        String laneType = lane.getType();
                        if (junction != null && !junction.equals("-1") && laneType != null && laneType.equals("driving")) {
                            break;
                        }
                        ArrayList<Geometry> geometries = lane.getGmlGeometries();
                        geometries = Transformation.crsTransform(geometries, sourceCRS, targetCRS, geoid);
                        geometries.stream().forEach(f -> {
                            Coordinate[] coords = f.getCoordinates();
                            int from = lane.getId() < 0 ? 0 : coords.length / 2;
                            int to = lane.getId() < 0 ? coords.length / 2 : coords.length - 1;
                            LineString line = geometryFactory.createLineString(Arrays.copyOfRange(coords, from, to));
                            JSONObject feature = createFeature(line);
                            JSONObject properties = getProperties(lane);
                            properties.put("roadId", road.getId());
                            feature.put("properties", properties);
                            geojson.getFeatures().add(feature);
                        });
                    }
                }
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    /**
     * Creates a top level feature for each OpenDRIVE Road represented by its area
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of road polygons
     */
    public static GeoJsonFormat convertRoads(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem(odr.getHeader().getGeoReference().getEpsg());
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                ArrayList<Geometry> geometries = road.getGmlGeometries();
                geometries = Transformation.crsTransform(geometries, sourceCRS, targetCRS, geoid);
                geometries.stream().forEach(f -> {
                    JSONObject feature = createFeature(f);
                    JSONObject properties = getProperties(road);
                    feature.put("properties", properties);
                    geojson.getFeatures().add(feature);
                });
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    /**
     * Creates a top level feature for each OpenDRIVE Lane represented by its area
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of lane polygons
     */
    public static GeoJsonFormat convertRoadMarks(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem("EPSG:25832");
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                for (Map.Entry<Double, LaneSection> e : road.getLanes().getLaneSections().entrySet()) {
                    Double s = e.getKey();
                    LaneSection laneSection = e.getValue();
                    for (Map.Entry<Integer, Lane> entry : ODRHelper.getLanesWithCenterLane(laneSection).entrySet()) {
                        Lane lane = entry.getValue();
                        for (Map.Entry<Double, RoadMark> roadMarkEntry : lane.getRoadMarks().entrySet()) {
                            RoadMark roadMark = roadMarkEntry.getValue();
                            ArrayList<Geometry> geometries = roadMarkEntry.getValue().getGmlGeometries();
                            if (!geometries.isEmpty()) {
                                try {
                                    geometries = Transformation.crsTransform(geometries, sourceCRS, targetCRS, geoid);
                                    if (!geometries.isEmpty()) {
                                        geometries.stream().forEach(f -> {
                                            JSONObject feature = createFeature(f);
                                            JSONObject properties = getProperties(roadMark);
                                            properties.put("roadId", road.getId());
                                            properties.put("laneId", lane.getId());
                                            properties.put("sOffset", s);
                                            feature.put("properties", properties);
                                            geojson.getFeatures().add(feature);
                                        });
                                    }
                                } catch (NullPointerException | TransformException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    /**
     * Creates a top level feature for each OpenDRIVE Lane represented by its area
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of lane polygons
     */
    public static GeoJsonFormat convertLanes(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem("EPSG:25832");
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                for (Map.Entry<Double, LaneSection> e : road.getLanes().getLaneSections().entrySet()) {
                    Double s = e.getKey();
                    LaneSection laneSection = e.getValue();
                    for (Map.Entry<Integer, Lane> entry : ODRHelper.getLanes(laneSection).entrySet()) {
                        Lane lane = entry.getValue();
                        String junction = road.getJunction();
                        String laneType = lane.getType();
                        if (junction != null && !junction.equals("-1") && laneType != null && laneType.equals("driving")) {
                            break;
                        }
//                        if (lane.getType().equals("driving")) {
                        ArrayList<Geometry> geometries = lane.getGmlGeometries();
                        geometries = Transformation.crsTransform(geometries, sourceCRS, targetCRS, geoid);
                        geometries.stream().forEach(f -> {
                            JSONObject feature = createFeature(f);
                            JSONObject properties = getProperties(lane);
                            properties.put("roadId", road.getId());
                            properties.put("sOffset", s);
                            feature.put("properties", properties);
                            geojson.getFeatures().add(feature);
                        });
//                        }
                    }
                }
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    /**
     * Creates a top level feature for each OpenDRIVE LaneSection represented by its area
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of laneSection polygons
     */
    public static GeoJsonFormat convertLaneSections(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        // TODO from converter config
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem("EPSG:25832");
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                for (Map.Entry<Double, LaneSection> e : road.getLanes().getLaneSections().entrySet()) {
                    LaneSection laneSection = e.getValue();
                    ArrayList<Geometry> geometries = laneSection.getGmlGeometries();
                    geometries = Transformation.crsTransform(geometries, sourceCRS, targetCRS, geoid);
                    geometries.stream().forEach(f -> {
                        JSONObject feature = createFeature(f);
                        JSONObject properties = getProperties(laneSection);
                        properties.put("roadId", road.getId());
                        feature.put("properties", properties);
                        geojson.getFeatures().add(feature);
                    });
                }
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    /**
     * Creates a top level feature for each OpenDRIVE Junction represented by its area
     * //TODO junction properties missing, better approach see JunctionAreaProcessor
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of junction polygons
     */
    public static GeoJsonFormat convertJunctions(OpenDRIVE odr) {
        HashMap<String, ArrayList<Geometry>> junctionMap = new HashMap<>();
        GeoJsonFormat geojson = new GeoJsonFormat();
        // TODO from converter config
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        GeometryFactory geometryFactory = new GeometryFactory();
        try {
            sourceCRS = factory.createCoordinateReferenceSystem("EPSG:25832");
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                if (!road.getJunction().equals("-1")) {
                    for (Map.Entry<Double, LaneSection> e : road.getLanes().getLaneSections().entrySet()) {
                        Double s = e.getKey();
                        LaneSection laneSection = e.getValue();
                        for (Map.Entry<Integer, Lane> entry : ODRHelper.getLanes(laneSection).entrySet()) {
                            Integer laneId = entry.getKey();
                            Lane lane = entry.getValue();
                            String laneType = lane.getType();
                            if (laneType != null && laneType.equals("driving")) {
                                ArrayList<Geometry> geometries = lane.getGmlGeometries();
                                putConnectingRoads(road.getJunction(), geometries, junctionMap);
                            }
                        }
                    }
                }
            }
            for (Map.Entry<String, ArrayList<Geometry>> entry : junctionMap.entrySet()) {
                String key = entry.getKey();
                ArrayList<Geometry> polygons = entry.getValue();
                polygons.removeIf(g -> !(g instanceof Polygon));
                // TODO find approach for creating a junction area (currently multipolygon)
//                polygons = Transformation.crsTransform(polygons, sourceCRS, targetCRS);
                // build junction geometry by union and exterior ring
                Geometry junctionGeometry = null;
//                try {
//                    junctionGeometry = CascadedPolygonUnion.union(polygons);
//                    if (junctionGeometry instanceof  Polygon) {
//                        Coordinate[] coords = ((Polygon) junctionGeometry).getExteriorRing().getCoordinates();
//                        // filter corods with invalid height
////                        coords = Arrays.stream(coords).filter(c -> c.getZ() > 0.1).toArray(Coordinate[]::new);
//                        if (geometryFactory.createLineString(coords).isClosed()) {
//                            junctionGeometry = geometryFactory.createPolygon(coords);
//                        }
//                    }
//                } catch (TopologyException e) {
//                    ODRLogger.getInstance().warn(e.getMessage());
                junctionGeometry = geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
                // skips overlapping polygons -> FME Buffer is better!
                // junctionGeometry = junctionGeometry.buffer(0);
//                }
                junctionGeometry = Transformation.crsTransform(junctionGeometry, sourceCRS, targetCRS, geoid);
                JSONObject feature = createFeature(junctionGeometry);
                JSONObject properties = new JSONObject();
                feature.put("properties", properties);
                properties.put("JunctionId", key);
                geojson.getFeatures().add(feature);
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    // TODO see JunctionAreaProcessor
    static private void putConnectingRoads(String key, ArrayList<Geometry> geometries,
            HashMap<String, ArrayList<Geometry>> junctionMap) {
        if (junctionMap.get(key) == null) {
            junctionMap.put(key, geometries);
        } else {
            junctionMap.get(key).addAll(geometries);
        }
    }

    /**
     * Creates a top level feature for each OpenDRIVE Object Element
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of object geometries
     */
    public static GeoJsonFormat convertObjects(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem(odr.getHeader().getGeoReference().getEpsg());
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                for (AbstractObject obj : road.getObjects()) {
                    ArrayList<Geometry> geometries = obj.getGmlGeometries();
                    geometries = Transformation.crsTransform(geometries, sourceCRS, targetCRS, geoid);
                    geometries.stream().forEach(f -> {
                        JSONObject feature = createFeature(f);
                        JSONObject properties = getProperties(obj);
                        properties.put("roadId", road.getId());
                        properties.put("s", obj.getLinearReference().getS());
                        properties.put("heading", obj.getIntertialTransform().getHdg());
                        properties.put("zOffset", obj.getIntertialTransform().getzOffset());
                        properties.put("className", obj.getClass().getSimpleName());
                        feature.put("properties", properties);
                        geojson.getFeatures().add(feature);
                    });
                }
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    /**
     * Creates a top level feature for each OpenDRIVE Signal Element
     *
     * @param odr - OpenDRIVE data
     * @return GeoJSON feature list of object geometries
     */
    public static GeoJsonFormat convertSignals(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS;
        CoordinateReferenceSystem targetCRS;
        boolean geoid = false;
        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        try {
            sourceCRS = factory.createCoordinateReferenceSystem(odr.getHeader().getGeoReference().getEpsg());
            targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
            for (Road road : odr.getRoads()) {
                for (Signal signal : road.getSignals().getSignals()) {
                    ArrayList<Geometry> geometries = signal.getGmlGeometries();
                    geometries = Transformation.crsTransform(geometries, sourceCRS, targetCRS, geoid);
                    geometries.stream().forEach(f -> {
                        JSONObject feature = createFeature(f);
                        JSONObject properties = getProperties(signal);
                        properties.put("roadId", road.getId());
                        properties.put("s", signal.getLinearReference().getS());
                        properties.put("heading", signal.getInertialTransform().getHdg());
                        properties.put("zOffset", signal.getInertialTransform().getzOffset());
                        feature.put("properties", properties);
                        geojson.getFeatures().add(feature);
                    });
                }
            }
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    @Override
    public void write(GeoJsonFormat format) throws IOException {
        JSONObject featureCollection = new JSONObject();
        JSONArray features = new JSONArray();
        format.getFeatures().stream().forEach(f -> {
            features.add(f);
        });
        featureCollection.put("features", features);
        featureCollection.put("type", "FeatureCollection");
        String json = featureCollection.toJSONString();
        OutputStream out = new FileOutputStream(this.getOutputFile());
        out.write(json.getBytes(StandardCharsets.UTF_8));
        out.close();
    }

    /**
     * Creates GeoJSON feature with geometry
     *
     * @param geom - the feature's geometry
     * @return feature
     */
    private static JSONObject createFeature(Geometry geom) {
        JSONObject feature = new JSONObject();
        GeoJsonWriter geomWriter = new GeoJsonWriter();
        geomWriter.setEncodeCRS(false);
        JSONParser parser = new JSONParser();
        try {
            JSONObject geomObject = (JSONObject) parser.parse(geomWriter.write(geom));
            feature.put("geometry", geomObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        feature.put("type", "feature");
        return feature;
    }

    /**
     * gets feature properties of OpenDRIVE Element
     *
     * @param element - an OpenDRIVE Element
     * @return feature properties
     */
    private static JSONObject getProperties(AbstractOpenDriveElement element) {
        JSONObject properties = new JSONObject();
        List<Field> fields = getInheritedPrivateFields(element.getClass());
        for (Field f : fields) {
            f.setAccessible(true);
            if (ClassUtils.isPrimitiveOrWrapper(f.getType()) || f.getType().isAssignableFrom(String.class)) {
                try {
                    properties.put(f.getName(), f.get(element));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (f.getType().isAssignableFrom(ArrayList.class)) {
                try {
                    JSONArray array = new JSONArray();
                    ArrayList<?> arrayList = (ArrayList<?>) f.get(element);
                    if (arrayList != null) {
                        arrayList.forEach(v -> {
                            if (!(v == null)) {
                                if (ClassUtils.isPrimitiveOrWrapper(v.getClass())) {
                                    array.add(v);
                                } else if (v instanceof AbstractOpenDriveElement) {
                                    array.add(getProperties((AbstractOpenDriveElement) v));
                                }
                            }
                        });
                        if (!array.isEmpty()) {
                            properties.put(f.getName(), array);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else if (f.getType().isAssignableFrom(TreeMap.class)) {
                try {
                    JSONArray array = new JSONArray();
                    TreeMap<?, ?> map = (TreeMap<?, ?>) f.get(element);
                    if (map != null) {
                        map.forEach((k, v) -> {
                            if (ClassUtils.isPrimitiveOrWrapper(v.getClass())) {
                                array.add(v);
                            } else if (v instanceof AbstractOpenDriveElement) {
                                array.add(getProperties((AbstractOpenDriveElement) v));
                            }
                        });
                        if (!array.isEmpty()) {
                            properties.put(f.getName(), array);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    private static List<Field> getInheritedPrivateFields(Class<?> type) {
        List<Field> result = new ArrayList<Field>();
        Class<?> i = type;
        while (i != null && i != Object.class) {
            for (Field field : i.getDeclaredFields()) {
                if (!field.isSynthetic()) {
                    result.add(field);
                }
            }
            i = i.getSuperclass();
        }
        return result;
    }
}

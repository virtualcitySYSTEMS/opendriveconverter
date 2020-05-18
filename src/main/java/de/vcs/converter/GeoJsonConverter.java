package de.vcs.converter;

import de.vcs.converter.strategies.ConverterStrategy;
import de.vcs.model.odr.core.OpenDRIVE;
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
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.*;
import java.util.ArrayList;

public class GeoJsonConverter extends FormatConverter<GeoJsonFormat> implements ConverterStrategy  {

    public GeoJsonConverter() {
        super(GeoJsonConverter::convertRoads);
    }

    public GeoJsonConverter() {
//        super(GeoJsonConverter::convertLanes);
        FormatConverter<GeoJsonFormat> converter = new FormatConverter<GeoJsonFormat>(GeoJsonConverter::convertRoads);
    }

    private static GeoJsonFormat convertLanes(OpenDRIVE odr) {

    }

    private static GeoJsonFormat convertRoads(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();

        CoordinateReferenceSystem sourceCRS = CRS.decode(odr.getHeader().getGeoReference().getEpsg());
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");

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
            ArrayList<Geometry> geometries = road.getGmlGeometries();
            geometries = Transformation.crsTransform(geometries, sourceCRS, targetCRS);
            Polygon[] polygonArray = new Polygon[geometries.size()];
            MultiPolygon polygons = geomFactory.createMultiPolygon(geometries.toArray(polygonArray));
            featureBuilder.add(polygons);
            SimpleFeature roadFeature = featureBuilder.buildFeature(String.valueOf(road.getId()));
            roadFeature.setAttribute("Name", road.getName());
            geojson.getFeatures().add(roadFeature);
        }
        return geojson;
    }

    @Override
    public void write(OpenDRIVE odr, File outputFile) throws IOException {
        GeoJsonFormat geojson = convertFromODR(odr);
        int decimals = 15;
        GeometryJSON gjson = new GeometryJSON(decimals);
        FeatureJSON fjson = new FeatureJSON(gjson);
        StringWriter writer = new StringWriter();
        SimpleFeatureCollection simpleCollection = DataUtilities.collection(geojson.getFeatures());
        fjson.writeFeatureCollection(simpleCollection, writer);
        String json = writer.toString();
        OutputStream out = new FileOutputStream(outputFile);
        out.write(json.getBytes("UTF-8"));
        out.close();
    }

}

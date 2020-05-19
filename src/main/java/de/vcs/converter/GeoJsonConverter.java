package de.vcs.converter;

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
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.io.*;
import java.util.ArrayList;
import java.util.function.Function;

public class GeoJsonConverter extends FormatConverter<GeoJsonFormat> {

    public GeoJsonConverter(Function<OpenDRIVE, GeoJsonFormat> fromODR) {
        super(fromODR);
    }

    public static GeoJsonFormat convertRoads(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        CoordinateReferenceSystem sourceCRS = null;
        CoordinateReferenceSystem targetCRS = null;
        try {
            sourceCRS = CRS.decode(odr.getHeader().getGeoReference().getEpsg());
            targetCRS = CRS.decode("EPSG:4326");

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
        } catch (FactoryException | TransformException e) {
            e.printStackTrace();
        }
        return geojson;
    }

    public static GeoJsonFormat convertLanes(OpenDRIVE odr) {
        GeoJsonFormat geojson = new GeoJsonFormat();
        return geojson;
    }

    @Override
    public void write(GeoJsonFormat format, File outputFile) throws IOException {
        super.write(format, outputFile);
        int decimals = 15;
        GeometryJSON gjson = new GeometryJSON(decimals);
        FeatureJSON fjson = new FeatureJSON(gjson);
        StringWriter writer = new StringWriter();
        SimpleFeatureCollection simpleCollection = DataUtilities.collection(format.getFeatures());
        fjson.writeFeatureCollection(simpleCollection, writer);
        String json = writer.toString();
        OutputStream out = new FileOutputStream(outputFile);
        out.write(json.getBytes("UTF-8"));
        out.close();
    }
}

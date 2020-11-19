package de.vcs.converter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;
import java.util.List;

public class GeoJsonFormat extends AbstractFormat {

    private List<JSONObject> features = new ArrayList<>();

    public GeoJsonFormat(List<JSONObject> features) {
        this.features = features;
    }

    public GeoJsonFormat() {
    }

    public List<JSONObject> getFeatures() {
        return features;
    }

    public void setFeatures(List<JSONObject> features) {
        this.features = features;
    }
}

package de.vcs.converter;

import org.opengis.feature.simple.SimpleFeature;

import java.util.ArrayList;
import java.util.List;

public class GeoJsonFormat extends AbstractFormat {

    private List<SimpleFeature> features = new ArrayList<SimpleFeature>();

    public GeoJsonFormat(List<SimpleFeature> features) {
        this.features = features;
    }

    public GeoJsonFormat() {

    }

    public List<SimpleFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<SimpleFeature> features) {
        this.features = features;
    }
}

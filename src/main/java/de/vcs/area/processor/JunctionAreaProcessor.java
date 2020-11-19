package de.vcs.area.processor;

import de.vcs.model.odr.junction.Junction;
import de.vcs.model.odr.lane.Lane;
import de.vcs.model.odr.lane.LaneSection;
import de.vcs.model.odr.road.Road;
import org.locationtech.jts.geom.Geometry;

import java.util.*;

public class JunctionAreaProcessor extends AbstractAreaProcessor {

    private ArrayList<Road> roads;
    private ArrayList<Junction> junctions;
    private HashMap<String, ArrayList<Geometry>> junctionMap;


    public JunctionAreaProcessor(ArrayList<Road> roads, ArrayList<Junction> junctions) {
        this.roads = roads;
        this.junctions = junctions;
        this.junctionMap = new HashMap<>();
    }

    @Override
    public void processAreas() { createJunctionPolygons(); }

    private void createJunctionPolygons() {
        for (Road road : this.roads) {
            if (!road.getJunction().equals("-1")) { // is ConnectingRoad
                for (Map.Entry<Double, LaneSection> e : road.getLanes().getLaneSections().entrySet()) {
                    Double s = e.getKey();
                    LaneSection laneSection = e.getValue();
                    for (Map.Entry<Integer, Lane> entry : getLanes(laneSection).entrySet()) {
                        Integer laneId = entry.getKey();
                        Lane lane = entry.getValue();
                        if (lane.getType().equals("driving")) { // driving lanes yield junction area
                            ArrayList<Geometry> geometries = lane.getGmlGeometries();
                            putConnectingRoads(road.getJunction(), geometries, junctionMap);
                        }
                    }
                }
            }

        }
        for (Junction junction : this.junctions) {
            ArrayList<Geometry> polygons = junctionMap.get(junction.getId());
            // TODO opendrive4j add gmlGeometries to junction
            // junction.setGmlGeometries(ArrayUtils.toArray(CascadedPolygonUnion.union(polygons)));
        }
    }

    static private void putConnectingRoads(String key, ArrayList<Geometry> geometries, HashMap<String, ArrayList<Geometry>> junctionMap) {
        if (junctionMap.get(key) == null) {
            junctionMap.put(key, geometries);
        } else {
            junctionMap.get(key).addAll(geometries);
        }
    }

    // TODO copied from GeoJsonConverter, move to utils?
    private static TreeMap<Integer, Lane> getLanes(LaneSection ls) {
        TreeMap<Integer, Lane> lanes = new TreeMap<>();
        lanes.putAll(ls.getLeftLanes());
        lanes.putAll(ls.getRightLanes());
        return lanes;
    }
}

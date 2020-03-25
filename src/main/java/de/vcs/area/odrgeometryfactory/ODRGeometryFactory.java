package de.vcs.area.odrgeometryfactory;

import de.vcs.constants.JTSConstants;
import org.locationtech.jts.geom.*;
import java.util.ArrayList;


public class ODRGeometryFactory {

    public static Geometry create(String type, ArrayList<Point> points){
        if(type.equalsIgnoreCase(JTSConstants.POLYGON)){
            return new ODRPolygon().create(points);
        }
        else if(type.equalsIgnoreCase(JTSConstants.LINESTRING)){
            return new ODRLineString().create(points);
        }
        else return null;
    }
}

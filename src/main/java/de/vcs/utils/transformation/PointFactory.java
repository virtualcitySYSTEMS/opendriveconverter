package de.vcs.utils.transformation;

import de.vcs.model.odr.geometry.*;

public class PointFactory {

    public ODRGeometryHandler getODRGeometryHandler(Class geomClass) {
        if (geomClass.equals(ParamPolynom.class)) {
            return new ParamPolyHandler();
        } else if (geomClass.equals(Polynom.class)) {
            return new PolyHandler();
        } else if (geomClass.equals(Line.class)) {
            return new LineHandler();
        } else if (geomClass.equals(Arc.class)) {
            return new ArcHandler();
        } else if (geomClass.equals(Spiral.class)) {
            return new SpiralHandler();
        } else {
            return null;
        }
    }
}

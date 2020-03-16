package de.vcs.utils.math;

import de.vcs.model.odr.geometry.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class STCompareTest {

    @Test
    public void comparableTestPolynom() {
        Polynom p1 = new Polynom();
        p1.getLinearReference().setS(1.1);
        Polynom p2 = new Polynom();
        p2.getLinearReference().setS(3.2);
        TreeMap<Double, Polynom> lanes = new TreeMap<>();
        lanes.put(3.2, p2);
        lanes.put(1.1, p1);
        Assert.assertEquals("Test based on linearReference - Polynom", 1.1,
                lanes.floorEntry(2.2).getValue().getLinearReference().getS(), 0.0);
    }

    @Test
    public void comparableTestArc() {
        double s = 9.324432;
        double sLow = 8.9;
        double sHigh = 12.3;
        Arc a1 = new Arc();
        a1.getLinearReference().setS(sLow);
        Arc a2 = new Arc();
        a2.getLinearReference().setS(sHigh);
        TreeMap<Double, Arc> arcs = new TreeMap<>();
        arcs.put(sLow, a1);
        arcs.put(sHigh, a2);
        Assert.assertEquals("Test based on linearReference - Arc", sLow,
                arcs.floorEntry(s).getValue().getLinearReference().getS(), 0.0);
    }

    @Test
    public void compareTSTransform() {
        double s = 9.324432;
        double sLow = 8.9;
        double sHigh = 12.3;
        Arc a1 = new Arc();
        a1.getStTransform().setsOffset(sLow);
        Arc a2 = new Arc();
        a2.getStTransform().setsOffset(sHigh);
        TreeMap<Double, Arc> arcs = new TreeMap<>();
        arcs.put(sLow, a1);
        arcs.put(sHigh, a2);
        Assert.assertEquals("Test based on STTransform - Arc", sLow,
                arcs.floorEntry(s).getValue().getStTransform().getsOffset(), 0.0);
    }
}
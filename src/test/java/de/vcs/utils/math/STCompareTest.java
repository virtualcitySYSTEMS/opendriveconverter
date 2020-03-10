package de.vcs.utils.math;

import de.vcs.model.odr.geometry.Arc;
import de.vcs.model.odr.geometry.Polynom;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;
import java.util.TreeSet;

public class STCompareTest {

    @Test
    public void comparableTestPolynom() {
        Polynom p1 = new Polynom();
        p1.getLinearReference().setS(1.1);
        Polynom p2 = new Polynom();
        p2.getLinearReference().setS(3.2);
        Polynom p = new Polynom();
        p.getLinearReference().setS(2.1);
        TreeSet<Polynom> lanes = new TreeSet<>();
        lanes.add(p2);
        lanes.add(p1);
        Assert.assertEquals("Test based on linearReference - Polynom", 1.1,
                Objects.requireNonNull(lanes.floor(p)).getLinearReference().getS(), 0.0);
    }

    @Test
    public void comparableTestArc() {
        Arc a = new Arc();
        a.getLinearReference().setS(11.2222);
        Arc a1 = new Arc();
        a1.getLinearReference().setS(8.9);
        Arc a2 = new Arc();
        a2.getLinearReference().setS(13.54353);
        TreeSet<Arc> arcs = new TreeSet<>();
        arcs.add(a1);
        arcs.add(a2);
        Assert.assertEquals("Test based on linearReference - Arc", 8.9,
                Objects.requireNonNull(arcs.floor(a)).getLinearReference().getS(), 0.0);
    }

    @Test
    public void compareTSTransform() {
        Arc a = new Arc();
        a.getStTransform().setsOffset(10.0);
        a.setId("Tester");
        Arc a1 = new Arc();
        a1.getStTransform().setsOffset(8.0);
        a1.setId("Pre");
        Arc a2 = new Arc();
        a2.getStTransform().setsOffset(12.0);
        a2.setId("Post");
        TreeSet<Arc> arcs = new TreeSet<>();
        arcs.add(a1);
        arcs.add(a2);
        Assert.assertEquals("Test based on STTransform - Arc", "Pre", Objects.requireNonNull(arcs.floor(a)).getId());
    }
}
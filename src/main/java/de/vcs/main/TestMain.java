package de.vcs.main;

import de.vcs.model.odr.core.OpenDRIVE;
import de.vcs.model.odr.geometry.AbstractODRGeometry;
import de.vcs.model.odr.geometry.AbstractSTGeometry;
import de.vcs.model.odr.geometry.Line;
import de.vcs.model.odr.road.Road;
import org.xmlobjects.XMLObjects;
import org.xmlobjects.stream.XMLReader;
import org.xmlobjects.stream.XMLReaderFactory;

import java.io.File;

public class TestMain{
    public static void main(String[] args) throws Exception {
        XMLObjects xmlObjects = XMLObjects.newInstance();
        XMLReaderFactory factory = XMLReaderFactory.newInstance(xmlObjects);
        OpenDRIVE odr;
        try (XMLReader reader = factory.createReader(new File(
                "src/main/resources/Crossing8Course.xodr"))) {
            odr = xmlObjects.fromXML(reader, OpenDRIVE.class);
        }
        System.out.println("header name: " + odr.getHeader().getName());
        for (Road r : odr.getRoads()) {
            System.out.println("===== road id: " + r.getId() + " =====");
            System.out.println(" + preID: " + r.getPredecessorId());
            System.out.println(" + sucID: " + r.getSuccessorId());
            System.out.println(" + type: " + r.getType().get(0).getType());
            System.out.println(" -- planview -- ");
            for (AbstractODRGeometry geom : r.getPlanView().getOdrGeometries()) {
                AbstractSTGeometry g = (AbstractSTGeometry) geom;
                System.out.println(g.getClass().getName() + " s: " + g.getLinearReference().getS() +
                        " xy: " + g.getInertialReference().getPos().getValue());
            }
            r.getLanes().getLaneSections()
                    .forEach(ls -> {
                        System.out.println("-- laneSection: " + ls.getLinearReference().getS() + " --");
                        ls.getLeftLanes()
                                .forEach(l -> System.out.println("left id: " + l.getId() + " type: " + l.getType() +
                                        " width: " + l.getWidths().get(0).getStTransform().getsOffset() +
                                        " preID: " + l.getPredecessorId() + " sucID: " + l.getSuccessorId()));
                        ls.getCenterLanes()
                                .forEach(l -> System.out.println("center id: " + l.getId() + " type: " + l.getType()));
                        ls.getRightLanes()
                                .forEach(l -> System.out.println("right id: " + l.getId() + " type: " + l.getType() +
                                        " width: " + l.getWidths().get(0).getStTransform().getsOffset() +
                                        " preID: " + l.getPredecessorId() + " sucID: " + l.getSuccessorId()));
                    });
            System.out.println("-- signals --");
            r.getSignals().getSignals()
                    .forEach(sig -> System.out.println("signalId: " + sig.getId() + " type: " + sig.getType()));
        }
    }
}

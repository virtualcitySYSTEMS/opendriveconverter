package de.vcs.main;

import de.vcs.area.LaneAreaGenerator;
import de.vcs.area.RoadAreaGenerator;
import de.vcs.area.worker.AreaWorker;
import de.vcs.area.worker.AreaWorkerFactory;
import de.vcs.area.worker.AreaWorkerPool;
import de.vcs.model.odr.core.OpenDRIVE;
import de.vcs.utils.log.ODRLogger;
import org.xmlobjects.XMLObjects;
import org.xmlobjects.XMLObjectsException;
import org.xmlobjects.builder.ObjectBuildException;
import org.xmlobjects.stream.XMLReadException;
import org.xmlobjects.stream.XMLReader;
import org.xmlobjects.stream.XMLReaderFactory;

import java.awt.geom.Area;
import java.io.File;

public class MainCLI {

    OpenDRIVE odr;
    XMLObjects xmlObjects;
    XMLReaderFactory xmlReaderFactory;
    File odrFile;
    AreaWorkerFactory areaWorkerFactory;
    AreaWorkerPool areaWorkerPool;
    int poolsizeMax = Runtime.getRuntime().availableProcessors();
    int poolsizeMin = poolsizeMax;
    int queueSize = 1000;
    ODRLogger log;

    public MainCLI(String odrFileName) {
        this.odrFile = new File(odrFileName);
    }

    public static void main(String[] args) {
        try {
            MainCLI mainCLI = new MainCLI("src/main/resources/realRoadExample.xodr");
            mainCLI.doMain();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doMain() throws XMLReadException, XMLObjectsException, ObjectBuildException {
        odr = new OpenDRIVE();
        initializeODRFactory();
        parseODRFile(odrFile);
        initializeAreaWorkerFactory();
        buildAreaParallel();
        printGML();
    }

    private void initializeODRFactory() throws XMLObjectsException, XMLReadException {
        xmlObjects = XMLObjects.newInstance();
        xmlReaderFactory = XMLReaderFactory.newInstance(xmlObjects);
    }

    private void initializeAreaWorkerFactory() {
        areaWorkerFactory = new AreaWorkerFactory();
        areaWorkerPool = new AreaWorkerPool(poolsizeMin, poolsizeMax,
                areaWorkerFactory, queueSize);
    }

    private void parseODRFile(File file) throws XMLReadException, ObjectBuildException {
        if (xmlReaderFactory != null && odr != null) {
            XMLReader reader = xmlReaderFactory.createReader(file);
            odr = xmlObjects.fromXML(reader, OpenDRIVE.class);
        }
    }

    private void buildAreaParallel() {
        odr.getRoads().forEach(o -> areaWorkerPool.addWork(new RoadAreaGenerator(o)));
    }

    private void printGML() {
        odr.getRoads().get(0).getGmlGeometries().forEach(g -> {
            System.out.println(g.getCoordinate().getX());
        });
    }
}

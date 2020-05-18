package de.vcs.main;

import de.vcs.area.RoadAreaGenerator;
import de.vcs.area.worker.AreaWorkerFactory;
import de.vcs.area.worker.AreaWorkerPool;
import de.vcs.converter.GeoJsonConverter;
import de.vcs.model.odr.core.OpenDRIVE;
import de.vcs.utils.log.ODRLogger;
import org.xmlobjects.XMLObjects;
import org.xmlobjects.XMLObjectsException;
import org.xmlobjects.builder.ObjectBuildException;
import org.xmlobjects.stream.XMLReadException;
import org.xmlobjects.stream.XMLReader;
import org.xmlobjects.stream.XMLReaderFactory;

import java.io.File;
import java.io.IOException;

public class MainCLI {

    OpenDRIVE odr;
    XMLObjects xmlObjects;
    XMLReaderFactory xmlReaderFactory;
    File odrFile;
    File outputFile;
    AreaWorkerFactory areaWorkerFactory;
    AreaWorkerPool areaWorkerPool;
    int poolsizeMax = Runtime.getRuntime().availableProcessors();
    int poolsizeMin = poolsizeMax;
    int queueSize = 1000;
    ODRLogger log;

    public MainCLI(String odrFileName, String outFileName) {
        this.odrFile = new File(odrFileName);
        this.outputFile = new File(outFileName);
    }

    public static void main(String[] args) {
        try {
            MainCLI mainCLI = new MainCLI("src/main/resources/realRoadExample.xodr", "src/main/resources/realRoadExample.json");
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
        writeODRFile(odr);
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

    private void writeODRFile(OpenDRIVE odr) {
        GeoJsonConverter converter = new GeoJsonConverter();
        try {
            converter.write(odr, outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void buildAreaParallel() {
        areaWorkerPool.prestartCoreWorkers();
        odr.getRoads().forEach(o -> areaWorkerPool.addWork(new RoadAreaGenerator(o)));
        try {
            areaWorkerPool.shutdownAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printGML() {
        odr.getRoads().get(0).getGmlGeometries().forEach(g -> {
            System.out.println(g.getCoordinate().getX());
        });
    }
}

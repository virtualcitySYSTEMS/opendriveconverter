package de.vcs.main;

import de.vcs.area.generator.ObjectAreaGenerator;
import de.vcs.area.generator.RoadAreaGenerator;
import de.vcs.area.generator.SignalAreaGenerator;
import de.vcs.area.worker.AreaWorkerFactory;
import de.vcs.area.worker.AreaWorkerPool;
import de.vcs.converter.FormatConverter;
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
import java.util.*;

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
        System.setProperty("hsqldb.reconfig_logging", "false");
        this.odrFile = new File(odrFileName);
        this.outputFile = new File(outFileName);
    }

    public static void main(String[] args) {
        try {
            MainCLI mainCLI = new MainCLI("src/main/resources/2021-10-26_1500_PLIMOS_Grafing_Prio1.xodr",
                    "src/main/resources/2021-10-26_1500_PLIMOS_Grafing_Prio1");
//            MainCLI mainCLI = new MainCLI("src/main/resources/2020-06-19_SAVe_Ingolstadt_Prio4.xodr",
//                    "src/main/resources/2020-06-19_SAVe_Ingolstadt_Prio4");
//            MainCLI mainCLI = new MainCLI("src/main/resources/2020-09-21_SAVe_Ingolstadt_Update2_Prio1-6.xodr",
//                    "src/main/resources/2020-09-21_SAVe_Ingolstadt_Update2_Prio1-6");
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
        generateAreasParallel();
        processAreasParallel();
        writeODRFile(odr);
        printGML();
    }

    private void initializeODRFactory() throws XMLObjectsException, XMLReadException {
        xmlObjects = XMLObjects.newInstance();
        xmlReaderFactory = XMLReaderFactory.newInstance(xmlObjects);
    }

    private void parseODRFile(File file) throws XMLReadException, ObjectBuildException {
        if (xmlReaderFactory != null && odr != null) {
            XMLReader reader = xmlReaderFactory.createReader(file);
            odr = xmlObjects.fromXML(reader, OpenDRIVE.class);
            odr.getHeader().getGeoReference().setEpsg("EPSG:32632"); //TODO input parameter
        }
    }

    private void initializeAreaWorkerFactory() {
        areaWorkerFactory = new AreaWorkerFactory();
        areaWorkerPool = new AreaWorkerPool(poolsizeMin, poolsizeMax, areaWorkerFactory, queueSize);
    }

    private void generateAreasParallel() {
        areaWorkerPool.prestartCoreWorkers();
        odr.getRoads().forEach(o -> {
            areaWorkerPool.addWork(new RoadAreaGenerator(o));
            areaWorkerPool.addWork(new ObjectAreaGenerator(o));
            areaWorkerPool.addWork(new SignalAreaGenerator(o));
        });
        try {
            areaWorkerPool.awaitQueueEmpty();
            areaWorkerPool.shutdownAndWait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void processAreasParallel() {
        // TODO worker, can we do this parallel? or other concept instead?
        // new JunctionAreaProcessor(odr.getRoads(), odr.getJunctions());
    }

    private void writeODRFile(OpenDRIVE odr) {
        List<FormatConverter> converters = new ArrayList<>();
        if (outputFile.exists() || outputFile.mkdir()) {
            System.out.println("Writing Output in: " + outputFile.getAbsolutePath());
            converters.add(new GeoJsonConverter(GeoJsonConverter::convertReferenceLine,
                    new File(outputFile, "refLine.json")));
            converters.add(new GeoJsonConverter(GeoJsonConverter::convertLaneBreakLines,
                    new File(outputFile, "breakLines.json")));
            converters.add(new GeoJsonConverter(GeoJsonConverter::convertRoads, new File(outputFile, "roads.json")));
            converters.add(new GeoJsonConverter(GeoJsonConverter::convertLanes, new File(outputFile, "lanes.json")));
            converters
                    .add(new GeoJsonConverter(GeoJsonConverter::convertObjects, new File(outputFile, "objects.json")));
            converters
                    .add(new GeoJsonConverter(GeoJsonConverter::convertSignals, new File(outputFile, "signals.json")));
            converters.add(new GeoJsonConverter(GeoJsonConverter::convertLaneSections,
                    new File(outputFile, "laneSections.json")));
            converters.add(new GeoJsonConverter(GeoJsonConverter::convertJunctions,
                    new File(outputFile, "junctions.json")));
            converters.add(new GeoJsonConverter(GeoJsonConverter::convertRoadMarks,
                    new File(outputFile, "roadMarks.json")));
            // TODO: converters.add(new CityGMLConverter(CityGMLConverter::convertRoads));
            converters.forEach(c -> {
                try {
                    c.write(c.convertFromODR(odr));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Couldn't create output directory: " + outputFile.getAbsolutePath());
        }
    }

    private void printGML() {
        System.out.println("finish");
        System.out.flush();
    }
}

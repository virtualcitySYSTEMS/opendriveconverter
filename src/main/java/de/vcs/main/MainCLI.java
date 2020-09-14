package de.vcs.main;

import de.vcs.area.ObjectAreaGenerator;
import de.vcs.area.RoadAreaGenerator;
import de.vcs.area.worker.AreaWorkerFactory;
import de.vcs.area.worker.AreaWorkerPool;
import de.vcs.converter.AbstractFormat;
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
import java.util.ArrayList;
import java.util.List;

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
            MainCLI mainCLI = new MainCLI("src/main/resources/2019-11-29_SAVe_Ingolstadt_Prio1-4.xodr", "src/main/resources/2019-11-29_SAVe_Ingolstadt_Prio1-4");
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
            odr.getHeader().getGeoReference().setEpsg("EPSG:32632"); //TODO input parameter
        }
    }

    private void writeODRFile(OpenDRIVE odr) {
        List<FormatConverter> converters = new ArrayList<>();
//        converters.add(new GeoJsonConverter(GeoJsonConverter::convertRoads, outputFile));
        if (outputFile.exists() || outputFile.mkdir()) {
            System.out.println("Writing Output in: " + outputFile.getAbsolutePath());
            converters.add(new GeoJsonConverter(GeoJsonConverter::convertReferenceLine, new File(outputFile, "refLine.json")));
//            converters.add(new GeoJsonConverter(GeoJsonConverter::convertObjects, new File(outputFile, "objects.json")));
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

    private void buildAreaParallel() {
        areaWorkerPool.prestartCoreWorkers();
        odr.getRoads().forEach(o -> {
            areaWorkerPool.addWork(new RoadAreaGenerator(o));
            //areaWorkerPool.addWork(new ObjectAreaGenerator(o));
        });
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

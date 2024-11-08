package de.vcs.area.worker;

import de.vcs.area.generator.AreaGenerator;
import de.vcs.utils.log.ODRLogger;
import org.citydb.util.concurrent.DefaultWorker;


public class AreaWorker extends DefaultWorker<AreaGenerator> {

    private ODRLogger log = ODRLogger.getInstance();

    @Override
    public void doWork(AreaGenerator work) {
        work.generateArea();
    }

    @Override
    public void shutdown() {
        System.out.println("Worker shut down");
    }
}

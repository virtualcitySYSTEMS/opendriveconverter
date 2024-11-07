package de.vcs.area.worker;

import de.vcs.area.generator.AreaGenerator;
import org.citydb.util.concurrent.Worker;
import org.citydb.util.concurrent.WorkerFactory;

public class AreaWorkerFactory implements WorkerFactory<AreaGenerator> {

    @Override
    public Worker<AreaGenerator> createWorker() {
        return new AreaWorker();
    }
}

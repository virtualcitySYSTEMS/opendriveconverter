package de.vcs.area.worker;

import de.vcs.area.AreaGenerator;
import org.citydb.concurrent.Worker;
import org.citydb.concurrent.WorkerFactory;

public class AreaWorkerFactory implements WorkerFactory<AreaGenerator> {

    @Override
    public Worker<AreaGenerator> createWorker() {
        return new AreaWorker();
    }
}

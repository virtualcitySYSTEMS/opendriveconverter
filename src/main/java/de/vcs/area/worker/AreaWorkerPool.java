package de.vcs.area.worker;

import de.vcs.area.generator.AreaGenerator;
import org.citydb.concurrent.PoolSizeAdaptationStrategy;
import org.citydb.concurrent.WorkerFactory;
import org.citydb.concurrent.WorkerPool;

public class AreaWorkerPool extends WorkerPool<AreaGenerator> {

    public AreaWorkerPool(int corePoolSize, int maximumPoolSize,
            WorkerFactory<AreaGenerator> workerFactory, int queueSize) {
        super("Texture Pool", corePoolSize, maximumPoolSize,
                PoolSizeAdaptationStrategy.AGGRESSIVE, workerFactory, queueSize);
    }
}

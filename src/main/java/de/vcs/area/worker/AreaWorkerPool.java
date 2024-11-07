package de.vcs.area.worker;

import de.vcs.area.generator.AreaGenerator;
import org.citydb.util.concurrent.PoolSizeAdaptationStrategy;
import org.citydb.util.concurrent.WorkerFactory;
import org.citydb.util.concurrent.WorkerPool;

public class AreaWorkerPool extends WorkerPool<AreaGenerator> {

    public AreaWorkerPool(int corePoolSize, int maximumPoolSize,
                          WorkerFactory<AreaGenerator> workerFactory, int queueSize) {
        super("Texture Pool", corePoolSize, maximumPoolSize,
                PoolSizeAdaptationStrategy.AGGRESSIVE, workerFactory, queueSize);
    }
}

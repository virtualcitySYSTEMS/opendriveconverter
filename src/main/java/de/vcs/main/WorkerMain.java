package de.vcs.main;

import de.vcs.area.worker.AreaWorkerFactory;
import de.vcs.area.worker.AreaWorkerPool;
import de.vcs.datatypes.TestContainer;

public class WorkerMain {

    public static void main(String[] args) {
        int poolsizeMax = Runtime.getRuntime().availableProcessors();
        int poolsizeMin = poolsizeMax;
        int queueSize = 1000;
        TestContainer container = new TestContainer();
        AreaWorkerFactory factory = new AreaWorkerFactory();
        AreaWorkerPool pool = new AreaWorkerPool(poolsizeMin, poolsizeMax,
                factory, queueSize);
        container.getRoad().forEach(o -> System.out.println(o));
        container.getLane().forEach(o -> System.out.println(o));
    }
}

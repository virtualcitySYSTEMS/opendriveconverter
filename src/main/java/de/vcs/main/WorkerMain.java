package de.vcs.main;

import de.vcs.area.LaneAreaGenerator;
import de.vcs.area.RoadAreaGenerator;
import de.vcs.area.worker.AreaWorkerFactory;
import de.vcs.area.worker.AreaWorkerPool;

public class WorkerMain {

    public static void main(String[] args) {
        int poolsizeMax = Runtime.getRuntime().availableProcessors();
        int poolsizeMin = poolsizeMax;
        int queueSize = 1000;
        AreaWorkerFactory factory = new AreaWorkerFactory();
        AreaWorkerPool pool = new AreaWorkerPool(poolsizeMin, poolsizeMax,
                factory, queueSize);
        for (int i = 0; i < 100; i++) {
            pool.addWork(new LaneAreaGenerator());
            pool.addWork(new RoadAreaGenerator());
        }
    }
}

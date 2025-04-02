package multithreading.controller;

import multithreading.model.Barrier;
import multithreading.model.Flag;
import multithreading.model.Boid;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class BoidThread extends Thread {

    private final BoidSimulationManager boidSimulationManager;
    private final Flag flag;
    private final Barrier barrier;
    private final List<Boid> boids;
    private final Lock lock;
    private final int startIndex;
    private final int endIndex;

    public BoidThread(List<Boid> boids, BoidSimulationManager boidSimulationManager, Barrier barrier, int startIndex, int endIndex, Flag flag, Lock lock) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.boids = boids;
        this.boidSimulationManager = boidSimulationManager;
        this.barrier = barrier;
        this.flag= flag;
        this.lock= lock;
    }

    public void run() {
        try {
            boidSimulationManager.runSimulation(this);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Boid> getBoids() {
        return boids;
    }

    public BoidSimulationManager getBoidSimulationManager() {
        return boidSimulationManager;
    }

    public Barrier getBarrier() {
        return barrier;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public Flag getFlag() {
        return flag;
    }

    public Lock getLock() {
        return lock;
    }
}

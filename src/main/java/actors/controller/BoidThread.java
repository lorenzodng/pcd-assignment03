package actors.controller;

import actors.model.Barrier;
import actors.model.Boid;
import actors.model.Flag;

import java.util.List;

public class BoidThread extends Thread {

    private final BoidSimulationManager boidSimulationManager;
    private final Flag flag;
    private final Barrier barrier;
    private final List<Boid> boids;
    private final int startIndex;
    private final int endIndex;

    public BoidThread(List<Boid> boids, BoidSimulationManager boidSimulationManager, Barrier barrier, int startIndex, int endIndex, Flag flag) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.boids = boids;
        this.boidSimulationManager = boidSimulationManager;
        this.barrier = barrier;
        this.flag= flag;
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
}

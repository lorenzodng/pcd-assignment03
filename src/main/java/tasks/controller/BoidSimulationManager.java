package tasks.controller;

import tasks.model.Boid;
import tasks.model.BoidManager;
import tasks.view.BoidView;
import virtual_threads.main.Simulation;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoidSimulationManager {

    private final BoidManager boidManager;
    private Optional<BoidView> view;
    private Lock lock;
    private int framerate;

    public BoidSimulationManager(BoidManager boidManager) {
        this.boidManager = boidManager;
        view = Optional.empty();
        lock= new ReentrantLock();
    }

    public void attachView(BoidView view) {
        this.view = Optional.of(view);
    }

    public void changeVelocity(Boid boid) {
        boid.updateVelocity(boidManager);
    }

    public void changePosition(Boid boid, long dtVelocity) {
        long dtBefore= System.currentTimeMillis();
        boid.updatePosition(boidManager);
        long dtAfter= System.currentTimeMillis();
        long dtPosition = dtAfter - dtBefore;
        long dtElapsed= dtVelocity + dtPosition;
        if (view.isPresent()) {
            long frameratePeriod = 1000 / (virtual_threads.main.Simulation.FRAMERATE);
            lock.lock();
            if (dtElapsed < frameratePeriod) {
                framerate = Simulation.FRAMERATE;
            } else {
                framerate = (int) (1000 / (dtElapsed));
            }
            lock.unlock();
            if (dtElapsed < frameratePeriod) {
                try {
                    Thread.sleep(frameratePeriod - dtElapsed);
                } catch (Exception ex) {
                }
            }
        }
        view.get().update(framerate);
    }
}

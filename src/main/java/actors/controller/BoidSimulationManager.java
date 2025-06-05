package actors.controller;

import actors.main.Simulation;
import actors.model.BoidManager;
import actors.view.BoidView;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoidSimulationManager {

    private BoidManager boidManager;
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
      
    public void runSimulation(BoidThread thread) throws InterruptedException {
        thread.getFlag().reset();
        while(!thread.getFlag().isSet()) {
            long t0 = System.currentTimeMillis();
            for (int i = thread.getStartIndex(); i < thread.getEndIndex(); i++) {
                thread.getBoids().get(i).updateVelocity(boidManager);
            }
            thread.getBarrier().hitAndWaitAll();
            for (int i = thread.getStartIndex(); i < thread.getEndIndex(); i++) {
                thread.getBoids().get(i).updatePosition(boidManager);
            }
            long t1 = System.currentTimeMillis();
            long dtElapsed = t1 - t0;
            if (view.isPresent()) {
                long frameratePeriod = 1000 / Simulation.FRAMERATE;
                lock.lock();
                view.get().update(framerate);
                if (dtElapsed < frameratePeriod) {
                    framerate = Simulation.FRAMERATE;
                } else {
                    framerate = (int) (1000 / dtElapsed);
                }
                lock.unlock();
                if (dtElapsed < frameratePeriod) {
                    try {
                        Thread.sleep(frameratePeriod - dtElapsed);
                    } catch (Exception ex) {
                    }
                }
            }
        }
    }
}
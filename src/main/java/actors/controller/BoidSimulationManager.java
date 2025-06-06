package actors.controller;

import actors.main.Simulation;
import actors.model.Boid;
import actors.model.BoidManager;
import actors.view.BoidView;

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
        view= Optional.empty();
        lock= new ReentrantLock();
    }

    public void attachView(BoidView view) {
        this.view = Optional.of(view);
    }

    public void changeVelocity(BoidActor boidActor) {
        for(int i = boidActor.getStartIndex(); i < boidActor.getEndIndex(); i++){
            boidActor.getBoids().get(i).updateVelocity(boidManager);
        }
    }

    public void changePosition(BoidActor boidActor, long dtVelocity) {
        long dtBefore= System.currentTimeMillis();

        for(int i = boidActor.getStartIndex(); i < boidActor.getEndIndex(); i++){
            boidActor.getBoids().get(i).updatePosition(boidManager);
        }

        long dtAfter= System.currentTimeMillis();
        long dtPosition = dtAfter - dtBefore;
        long dtElapsed= dtVelocity + dtPosition;
        if (view.isPresent()) {
            long frameratePeriod = 1000 / (tasks.main.Simulation.FRAMERATE);
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
package threads.controller;

import threads.main.Simulation;
import threads.model.BoidManager;
import threads.view.BoidView;
import java.util.Optional;

public class BoidSimulationManager {

    private BoidManager boidManager;
    private Optional<BoidView> view;
    private int framerate;

    public BoidSimulationManager(BoidManager boidManager) {
        this.boidManager = boidManager;
        view = Optional.empty();
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
                view.get().update(framerate);
                long frameratePeriod = 1000 / Simulation.FRAMERATE;
                if (dtElapsed < frameratePeriod) {
                    try {
                        Thread.sleep(frameratePeriod - dtElapsed);
                    } catch (Exception ex) {
                    }
                    framerate = Simulation.FRAMERATE;
                } else {
                    framerate = (int) (1000 / dtElapsed);
                }
            }
        }
    }
}
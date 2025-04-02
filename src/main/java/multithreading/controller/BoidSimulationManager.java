package multithreading.controller;

import multithreading.model.BoidManager;
import multithreading.view.BoidView;

import java.util.Optional;

public class BoidSimulationManager {

    private BoidManager boidManager;
    private Optional<BoidView> view;
    private static final int FRAMERATE = 60;
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
            thread.getBarrier().hitAndWaitAll();

            long t1 = System.currentTimeMillis();

            thread.getLock().lockInterruptibly();
            long dtElapsed = t1 - t0;

            if (view.isPresent()) {
                view.get().update(framerate);
                long frameratePeriod = 1000 / FRAMERATE;
                thread.getLock().unlock();

                if (dtElapsed < frameratePeriod) {
                    try {
                        Thread.sleep(frameratePeriod - dtElapsed);
                    } catch (Exception ex) {
                    }
                    framerate = FRAMERATE;
                } else {
                    thread.getLock().lockInterruptibly();
                    framerate = (int) (1000 / dtElapsed);
                    thread.getLock().unlock();
                }
            }

        }
    }



}

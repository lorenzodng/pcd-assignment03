package virtual_threads.controller;

import virtual_threads.main.Simulation;
import virtual_threads.model.Boid;
import virtual_threads.model.BoidManager;
import virtual_threads.view.BoidView;
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

    public void changeVelocity(Boid boid) {
        boid.updateVelocity(boidManager);
    }

    public void changePosition(Boid boid, long dtVelocity) {
        long dtBefore= System.currentTimeMillis();
        boid.updatePosition(boidManager);
        long dtAfter= System.currentTimeMillis();
        long dtPosition = dtAfter - dtBefore;
        long dtElapsed= dtVelocity + dtPosition;
        //System.out.println("dtElapsed: " + dtElapsed);
        if (view.isPresent()) {
            long frameratePeriod = 1000 / (Simulation.FRAMERATE);
            if (dtElapsed < frameratePeriod) {
                try {
                    Thread.sleep(frameratePeriod - dtElapsed);
                } catch (Exception ex) {
                }
                framerate = Simulation.FRAMERATE;
            } else {
                framerate = (int) (1000 / (dtElapsed));
            }
        }
        view.get().update(framerate);
    }
}

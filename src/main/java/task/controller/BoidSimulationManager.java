package task.controller;

import task.main.Simulation;
import task.model.Boid;
import task.model.BoidManager;
import task.view.BoidView;

import java.util.Optional;

public class BoidSimulationManager {

    private final BoidManager boidManager;
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
                    Thread.sleep(frameratePeriod - dtElapsed); //if dtElapsed is significantly lower than frameratePeriod (4 ms vs 16), Thread.sleep(...)) will be called for a  long time. This helps to maintain the desired framerate.
                } catch (Exception ex) {
                }
                framerate = Simulation.FRAMERATE;
            } else {
                framerate = (int) (1000 / (dtElapsed)); //if bad performance, go fast (bad behaviour) but only if high framerate. If low framerate, time spent to compute all is already high, so it doesn't go fast
            }
        }
        view.get().update(framerate);
    }
}

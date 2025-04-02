package multithreading.controller;

import multithreading.model.BoidManager;
import multithreading.model.Flag;
import multithreading.view.BoidView;

public class BoidController {

    private Flag flag;

    public BoidController(Flag flag) {
        this.flag = flag;
    }

    public void startSimulation(BoidView view) {
        view.getBoidManager().getThreads().clear();
        view.getBoidManager().createThreads(view.getBoidManager().getBoids().size(), flag);
        for(BoidThread thread: view.getBoidManager().getThreads()){
            thread.getBoidSimulationManager().attachView(view);
            thread.start();
        }
    }

    public void stopSimulation() {
        flag.set();
    }

    public void resetSimulation(BoidManager boidManager) {
        boidManager.resetBoids();
        flag.reset();
    }

}

package actors.controller;

import actors.model.BoidManager;
import actors.model.Flag;
import actors.view.BoidView;

public class BoidController {

    private Flag flag;

    public BoidController(Flag flag) {
        this.flag = flag;
    }

    public void start(BoidView view, int nBoids) {
        if(view.getBoidManager().getBoids().isEmpty()){
            view.getBoidManager().createBoids(nBoids);
        }
        view.getBoidManager().getThreads().clear();
        view.getBoidManager().createThreads(flag);
        for(BoidThread thread: view.getBoidManager().getThreads()){
            thread.getBoidSimulationManager().attachView(view);
            thread.start();
        }
        System.out.println(view.getBoidManager().getThreads().size() + " threads running...");
    }

    public void stop() {
        flag.set();
        System.out.println("Threads terminated, boids active");
    }

    public void reset(BoidManager boidManager) {
        boidManager.deleteBoids();
        flag.reset();
        System.out.println("Threads terminated, boids deleted");
    }

}

package tasks.controller;

import tasks.model.BoidManager;
import tasks.model.Flag;
import tasks.view.BoidView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BoidController {

    private final Flag flag;
    private ExecutorService executor;

    public BoidController(Flag flag) {
        this.flag = flag;
    }

    public void start(BoidView view, int nBoids) {
        if(view.getBoidManager().getBoids().isEmpty()){
            view.getBoidManager().createBoids(nBoids);
        }
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
        new ThreadMaster(executor, flag, view).start();
    }

    public void stop() {
        executor.shutdownNow();
        flag.set();
        System.out.println("Tasks terminated, boids active");
    }

    public void reset(BoidManager boidManager) {
        executor.shutdownNow();
        boidManager.deleteBoids();
        flag.reset();
        System.out.println("Tasks terminated, boids deleted");
    }
}

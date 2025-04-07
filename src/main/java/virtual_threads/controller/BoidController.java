package virtual_threads.controller;

import virtual_threads.model.BoidManager;
import virtual_threads.model.Flag;
import virtual_threads.view.BoidView;

public class BoidController {

    private Flag flag;

    public BoidController(Flag flag) {
        this.flag = flag;
    }

    public void start(BoidView view, int nBoids) {
        if(view.getBoidManager().getBoids().isEmpty()){
            view.getBoidManager().createBoids(nBoids);
        }
        new ThreadMaster(flag, view).start();
    }

    public void stop() {
        flag.set();
        System.out.println("Virtual threads terminated, boids active");
    }

    public void reset(BoidManager boidManager) {
        boidManager.deleteBoids();
        flag.reset();
        System.out.println("Virtual threads terminated, boids deleted");
    }

}

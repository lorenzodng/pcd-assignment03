package task.controller;

import task.model.Boid;
import task.model.Flag;
import task.view.BoidView;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class ThreadMaster extends Thread {

    private final ExecutorService executor;
    private final Flag flag;
    private final BoidView view;
    private final int nTasks;
    private final List<Future<Void>> results = new LinkedList<>();
    private final BoidSimulationManager boidSimulationManager;

    public ThreadMaster(ExecutorService executor, Flag flag, BoidView view) {
        this.executor = executor;
        this.flag = flag;
        this.view = view;
        this.nTasks = view.getBoidManager().getBoids().size();
        this.boidSimulationManager = new BoidSimulationManager(view.getBoidManager());
        this.boidSimulationManager.attachView(view);
    }

    public void run() {
        System.out.println(nTasks + " tasks running...");
        flag.reset();
        while (!flag.isSet()) {
            long beforeVelocity= System.currentTimeMillis();
            results.clear();
            computeVelocity();
            waitResults();
            long afterVelocity= System.currentTimeMillis();
            long dtVelocity= afterVelocity - beforeVelocity;
            computePosition(dtVelocity);
        }
    }

    public void computeVelocity(){
        IntStream.range(0, nTasks).forEach(i -> {
            Future<Void> result= executor.submit(() -> {
                Boid boid = view.getBoidManager().getBoids().get(i);
                boidSimulationManager.changeVelocity(boid);
                return null;
            });
            results.add(result);
        });
    }

    public void computePosition(long dtVelocity){
        IntStream.range(0, nTasks).forEach(i -> {
            executor.submit(() -> {
                Boid boid = view.getBoidManager().getBoids().get(i);
                boidSimulationManager.changePosition(boid, dtVelocity);
                return null;
            });
        });
    }

    public void waitResults(){
        for (Future<Void> res : results) {
            try {
                res.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

package virtual_threads.controller;

import virtual_threads.model.Boid;
import virtual_threads.model.Flag;
import virtual_threads.view.BoidView;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class ThreadMaster extends Thread {

    private final Flag flag;
    private final BoidView view;
    private final List<Future<Void>> results = new LinkedList<>();
    private final int nVirtualThreads;
    private final BoidSimulationManager boidSimulationManager;

    public ThreadMaster(Flag flag, BoidView view) {
        this.flag = flag;
        this.view = view;
        this.nVirtualThreads= view.getBoidManager().getBoids().size();
        boidSimulationManager = new BoidSimulationManager(view.getBoidManager());
        boidSimulationManager.attachView(view);
    }

    public void run() {
        System.out.println(nVirtualThreads + " virtual threads running...");
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
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, nVirtualThreads).forEach(i -> {
                Future<Void> result=  executor.submit(() -> {
                    Boid boid = view.getBoidManager().getBoids().get(i);
                    boidSimulationManager.changeVelocity(boid);
                    return null;
                });
                results.add(result);
            });
        }
    }

    public void computePosition(long dtVelocity){
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, nVirtualThreads).forEach(i -> {
                executor.submit(() -> {
                    Boid boid = view.getBoidManager().getBoids().get(i);
                    boidSimulationManager.changePosition(boid, dtVelocity);
                    return null;
                });
            });
        }
    }

    public void waitResults() {
        for (Future<Void> res : results) {
            try {
                res.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

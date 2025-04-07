package threads.model;

import threads.controller.BoidSimulationManager;
import threads.controller.BoidThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoidManager {
    
    private final List<Boid> boids;
    private final List<BoidThread> threads;
    private double separationWeight; 
    private double alignmentWeight; 
    private double cohesionWeight; 
    private final double width;
    private final double height;
    private final double maxSpeed;
    private final double perceptionRadius;
    private final double avoidRadius;

    public BoidManager(double initialSeparationWeight, double initialAlignmentWeight, double initialCohesionWeight, double width, double height, double maxSpeed, double perceptionRadius, double avoidRadius){
        separationWeight = initialSeparationWeight;
        alignmentWeight = initialAlignmentWeight;
        cohesionWeight = initialCohesionWeight;
        this.width = width;
        this.height = height;
        this.maxSpeed = maxSpeed;
        this.perceptionRadius = perceptionRadius;
        this.avoidRadius = avoidRadius;
    	boids = new ArrayList<>();
        threads = new ArrayList<>();
    }

    public void createBoids(int nboids) {
        for (int i = 0; i < nboids; i++) {
            P2d pos = new P2d(-width / 2 + Math.random() * width, -height / 2 + Math.random() * height);
            V2d vel = new V2d(Math.random() * maxSpeed / 2 - maxSpeed / 4, Math.random() * maxSpeed / 2 - maxSpeed / 4);
            boids.add(new Boid(pos, vel));
        }
    }

    public void createThreads(Flag flag){
        int nThreads = Runtime.getRuntime().availableProcessors() + 1;
        Barrier barrier = new Barrier(nThreads);
        int chunkSize = boids.size() / nThreads;
        for (int i = 0; i < nThreads; i++) {
            int startIndex = i * chunkSize;
            int endIndex;
            if(i == nThreads - 1){
                endIndex= boids.size();
            }else {
                endIndex = (i + 1) * chunkSize;
            }
            threads.add(new BoidThread(boids, new BoidSimulationManager(this), barrier, startIndex, endIndex, flag));
        }
    }

    public void deleteBoids() {
        boids.clear();
    }

    public List<Boid> getBoids(){
        return boids;
    }

    public List<BoidThread> getThreads() {
        return threads;
    }

    public double getMinX() {
        return -width/2;
    }

    public double getMaxX() {
        return width/2;
    }

    public double getMinY() {
        return -height/2;
    }

    public double getMaxY() {
        return height/2;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setSeparationWeight(double value) {
        this.separationWeight = value;
    }

    public void setAlignmentWeight(double value) {
        this.alignmentWeight = value;
    }

    public void setCohesionWeight(double value) {
        this.cohesionWeight = value;
    }

    public double getSeparationWeight() {
        return separationWeight;
    }

    public double getCohesionWeight() {
        return cohesionWeight;
    }

    public double getAlignmentWeight() {
        return alignmentWeight;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAvoidRadius() {
        return avoidRadius;
    }

    public double getPerceptionRadius() {
        return perceptionRadius;
    }
}

package actors.model;

import java.util.ArrayList;
import java.util.List;

public class Boid {

    private P2d pos;
    private V2d vel;

    public Boid(P2d pos, V2d vel) {
        this.pos = pos;
        this.vel = vel;
    }

    public P2d getPos() {
        return pos;
    }

    public V2d getVel() {
        return vel;
    }

    public void updateVelocity(BoidManager boidManager) {
        List<Boid> nearbyBoids = getNearbyBoids(boidManager);
        V2d separation = calculateSeparation(nearbyBoids, boidManager);
        V2d alignment = calculateAlignment(nearbyBoids);
        V2d cohesion = calculateCohesion(nearbyBoids);
        vel = vel.sum(alignment.mul(boidManager.getAlignmentWeight()))
                .sum(separation.mul(boidManager.getSeparationWeight()))
                .sum(cohesion.mul(boidManager.getCohesionWeight()));
        double speed = vel.abs();
        if (speed > boidManager.getMaxSpeed()) {
            vel = vel.getNormalized().mul(boidManager.getMaxSpeed());
        }
    }

    public void updatePosition(BoidManager boidManager) {
        pos = pos.sum(vel);
        if (pos.x() < boidManager.getMinX())
            pos = pos.sum(new V2d(boidManager.getWidth(), 0));
        if (pos.x() >= boidManager.getMaxX())
            pos = pos.sum(new V2d(-boidManager.getWidth(), 0));
        if (pos.y() < boidManager.getMinY())
            pos = pos.sum(new V2d(0, boidManager.getHeight()));
        if (pos.y() >= boidManager.getMaxY())
            pos = pos.sum(new V2d(0, -boidManager.getHeight()));
    }

    private List<Boid> getNearbyBoids(BoidManager boidManager) {
        var list = new ArrayList<Boid>();
        for (Boid other : boidManager.getBoids()) {
            if (other != this) {
                P2d otherPos = other.getPos();
                double distance = pos.distance(otherPos);
                if (distance < boidManager.getPerceptionRadius()) {
                    list.add(other);
                }
            }
        }
        return list;
    }

    private V2d calculateAlignment(List<Boid> nearbyBoids) {
        double avgVx = 0;
        double avgVy = 0;
        if (nearbyBoids.size() > 0) {
            for (Boid other : nearbyBoids) {
                V2d otherVel = other.getVel();
                avgVx += otherVel.x();
                avgVy += otherVel.y();
            }
            avgVx /= nearbyBoids.size();
            avgVy /= nearbyBoids.size();
            return new V2d(avgVx - vel.x(), avgVy - vel.y()).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }

    private V2d calculateCohesion(List<Boid> nearbyBoids) {
        double centerX = 0;
        double centerY = 0;
        if (nearbyBoids.size() > 0) {
            for (Boid other : nearbyBoids) {
                P2d otherPos = other.getPos();
                centerX += otherPos.x();
                centerY += otherPos.y();
            }
            centerX /= nearbyBoids.size();
            centerY /= nearbyBoids.size();
            return new V2d(centerX - pos.x(), centerY - pos.y()).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }

    private V2d calculateSeparation(List<Boid> nearbyBoids, BoidManager boidManager) {
        double dx = 0;
        double dy = 0;
        int count = 0;
        for (Boid other : nearbyBoids) {
            P2d otherPos = other.getPos();
            double distance = pos.distance(otherPos);
            if (distance < boidManager.getAvoidRadius()) {
                dx += pos.x() - otherPos.x();
                dy += pos.y() - otherPos.y();
                count++;
            }
        }
        if (count > 0) {
            dx /= count;
            dy /= count;
            return new V2d(dx, dy).getNormalized();
        } else {
            return new V2d(0, 0);
        }
    }
}


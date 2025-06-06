package actors.controller;

import actors.model.Boid;
import actors.model.MsgProtocol;
import akka.actor.AbstractActor;
import java.util.List;

public class BoidActor extends AbstractActor {

    private final List<Boid> boids;
    private final int startIndex;
    private final int endIndex;
    private final BoidSimulationManager boidSimulationManager;

    public BoidActor(List<Boid> boids, int startIndex, int endIndex, BoidSimulationManager boidSimulationManager) {
        this.boids= boids;
        this.startIndex= startIndex;
        this.endIndex= endIndex;
        this.boidSimulationManager= boidSimulationManager;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MsgProtocol.VelocityMsg.class, this::computeVelocity)
                .match(MsgProtocol.PositionMsg.class, this::computePosition)
                .build();
    }

    private void computeVelocity(MsgProtocol.VelocityMsg msg) {
        boidSimulationManager.changeVelocity(this);
        getSender().tell(new MsgProtocol.VelocityDoneMsg(), this.getSelf());
    }

    private void computePosition(MsgProtocol.PositionMsg msg) {
        boidSimulationManager.changePosition(this, msg.dtVelocity());
        getSender().tell(new MsgProtocol.PositionDoneMsg(), this.getSelf());
    }

    public List<Boid> getBoids() {
        return boids;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
}

package actors.controller;

import actors.model.Boid;
import actors.model.MsgProtocol;
import akka.actor.AbstractActor;

public class BoidActor extends AbstractActor {

    private Boid boid;

    public BoidActor(Boid boid) {
        this.boid= boid;
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(MsgProtocol.VelocityMsg.class, this::computeVelocity)
                .match(MsgProtocol.VelocityMsg.class, this::computePosition)
                .build();
    }

    private void computePosition(MsgProtocol.VelocityMsg velocityMsg) {

    }

    private void computeVelocity(MsgProtocol.VelocityMsg velocityMsg) {

    }
}

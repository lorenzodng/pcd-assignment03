package actors.controller;

import actors.model.Boid;
import actors.model.MsgProtocol;
import actors.view.BoidView;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.List;


public class ActorMaster extends AbstractActor {

    private final BoidView view;
    private List<Boid> boids;
    private final BoidSimulationManager boidSimulationManager;
    private List<ActorRef> workerActors;
    private int velocityAcks;


    public ActorMaster(BoidView view) {
        this.view = view;
        boids = view.getBoidManager().getBoids();
        this.boidSimulationManager = new BoidSimulationManager(view.getBoidManager());
        this.boidSimulationManager.attachView(view);
        workerActors= new ArrayList<>();
    }

    @Override
    public void preStart() {
        for (int i = 0; i < boids.size(); i++) {
            final int index = i;
            Props props = Props.create(() -> new BoidActor(boids.get(index))).withDispatcher("my-blocking-dispatcher");
            ActorRef actor = getContext().actorOf(props, "workerActor-" + index);
            workerActors.add(actor);
        }
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(BootMsg.class, this::broadcastVelocity)
                .match(MsgProtocol.VelocityDoneMsg.class, this::checkVelocityUpdates)
                .match(MsgProtocol.PositionDoneMsg.class, this::broadcastVelocity)
                .build();
    }

    private void checkVelocityUpdates(MsgProtocol.VelocityDoneMsg msg){
        velocityAcks++;
        if (velocityAcks == workerActors.size()) {
            broadcastPosition();
            velocityAcks = 0;
        }
    }

    private void broadcastVelocity(Object msg) {
        for (ActorRef actor : workerActors) {
            actor.tell(new MsgProtocol.VelocityMsg(), this.getSelf());
        }
    }

    private void broadcastPosition() {
        for (ActorRef actor : workerActors) {
            actor.tell(new MsgProtocol.PositionMsg(), this.getSelf());
        }
    }

    static public class BootMsg {}


}

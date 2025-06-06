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

    private final List<Boid> boids;
    private final BoidSimulationManager boidSimulationManager;
    private final List<ActorRef> workerActors;
    private static final int CHUNK_SIZE= 50;
    private int velocityAcks;
    private long beforeVelocity;

    public ActorMaster(BoidView view) {
        boids = view.getBoidManager().getBoids();
        this.boidSimulationManager = new BoidSimulationManager(view.getBoidManager());
        this.boidSimulationManager.attachView(view);
        workerActors= new ArrayList<>();
    }

    @Override
    public void preStart() {
        createWorkerActors();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BootMsg.class, this::broadcastVelocity)
                .match(MsgProtocol.VelocityDoneMsg.class, this::checkVelocityUpdates)
                .match(MsgProtocol.PositionDoneMsg.class, this::broadcastVelocity)
                .build();
    }

    private void createWorkerActors() {
        int nActors= boids.size() / CHUNK_SIZE;
        for (int i = 0; i < nActors; i++) {
            int startIndex = i * CHUNK_SIZE;
            int endIndex;
            if(i == nActors - 1){
                endIndex= boids.size();
            }else {
                endIndex = (i + 1) * CHUNK_SIZE;
            }
            Props props = Props.create(BoidActor.class, () -> new BoidActor(boids, startIndex, endIndex, boidSimulationManager)).withDispatcher("my-dispatcher");
            ActorRef actor = getContext().actorOf(props, "workerActor-" + i);
            workerActors.add(actor);
        }
    }

    private void checkVelocityUpdates(MsgProtocol.VelocityDoneMsg msg){
        velocityAcks++;
        if (velocityAcks == workerActors.size()) {
            long afterVelocity= System.currentTimeMillis();
            long dtVelocity= afterVelocity - beforeVelocity;
            broadcastPosition(dtVelocity);
            velocityAcks = 0;
        }
    }

    private void broadcastVelocity(Object msg) {
        beforeVelocity= System.currentTimeMillis();
        for (ActorRef actor : workerActors) {
            actor.tell(new MsgProtocol.VelocityMsg(), this.getSelf());
        }
    }

    private void broadcastPosition(long dtVelocity) {
        for (ActorRef actor : workerActors) {
            actor.tell(new MsgProtocol.PositionMsg(dtVelocity), this.getSelf());
        }
    }

    public static class BootMsg {}

}

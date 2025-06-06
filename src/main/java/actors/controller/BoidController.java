package actors.controller;

import actors.model.MsgProtocol;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class BoidController extends AbstractActor {

    private ActorRef actorMaster;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(MsgProtocol.StartMsg.class, this::start)
                .match(MsgProtocol.StopMsg.class, this::stop)
                .match(MsgProtocol.ResetMsg.class, this::reset)
                .build();
    }

    private void start(MsgProtocol.StartMsg msg) {
        if(msg.view().getBoidManager().getBoids().isEmpty()){
            msg.view().getBoidManager().createBoids(msg.nBoids());
        }
        if(actorMaster == null) {
            actorMaster = this.getContext().actorOf(Props.create(ActorMaster.class, () -> new ActorMaster(msg.view())), "actorMaster");
        }
        actorMaster.tell(new ActorMaster.BootMsg(), null);
    }

    private void stop(MsgProtocol.StopMsg msg) {
        this.getContext().stop(actorMaster);
        System.out.println("Actors stopped, boids active");
    }

    private void reset(MsgProtocol.ResetMsg msg) {
        this.getContext().stop(actorMaster);
        actorMaster= null;
        msg.boidManager().deleteBoids();
        System.out.println("Actors terminated, boids deleted");
    }

}

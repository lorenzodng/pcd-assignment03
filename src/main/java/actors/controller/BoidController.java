package actors.controller;

import actors.model.Flag;
import actors.model.MsgProtocol;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class BoidController extends AbstractActor {


    private Flag flag;
    private ActorRef actorMaster= null;

    public BoidController(Flag flag) {
        this.flag = flag;
    }

    public Receive createReceive() {
        return receiveBuilder()
                .match(MsgProtocol.StartMsg.class, this::start)
                .match(MsgProtocol.StopMsg.class, this::stop)
                .match(MsgProtocol.ResetMsg.class, this::reset)
                .build();
    }

    public void start(MsgProtocol.StartMsg msg) {
        if(msg.view().getBoidManager().getBoids().isEmpty()){
            msg.view().getBoidManager().createBoids(msg.nBoids());
        }
        if(actorMaster == null) {
            actorMaster = this.getContext().actorOf(Props.create(() -> new ActorMaster(msg.view())), "actorMaster");
        }
        actorMaster.tell(new ActorMaster.BootMsg(), null);
    }

    public void stop(MsgProtocol.StopMsg msg) {
        flag.set();
        System.out.println("Threads terminated, boids active");
    }

    public void reset(MsgProtocol.ResetMsg msg) {
        msg.boidManager().deleteBoids();
        flag.reset();
        System.out.println("Threads terminated, boids deleted");
    }



}

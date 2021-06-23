package state.akka;

import akka.actor.ActorRef;

/**
 * @class public class AkkaHandler
 * @brief AkkaHandler class
 */
public class AkkaHandler {

    private final ActorRef actorRef;

    public AkkaHandler(ActorRef actorRef) {
        this.actorRef = actorRef;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }

}

package state.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

/**
 * @class public class AkkaContainer
 * @brief AkkaContainer class
 */
public class AkkaContainer {

    private final ActorSystem actorSystem;
    private final Map<String, ActorRef> actorRefMap = new HashMap<>();

    private final String name;

    public AkkaContainer(String name) {
        this.name = name;
        this.actorSystem = ActorSystem.create(name);
    }

    public synchronized void addActor (String name, Props props) {
        if (getActor(name) != null) { return; }
        actorRefMap.putIfAbsent(
                name,
                actorSystem.actorOf(props, name)
        );
    }

    public synchronized void removeActor (String name) {
        if (getActor(name) == null) { return; }
        actorRefMap.remove(name);
    }

    public ActorRef getActor (String name) {
        return actorRefMap.get(name);
    }

    public boolean tell (String name, Object msg) {
        ActorRef actorRef = getActor(name);
        if (actorRef == null) { return false; }

        actorRef.tell(msg, ActorRef.noSender());
        return true;
    }

    public String getName() {
        return name;
    }

}

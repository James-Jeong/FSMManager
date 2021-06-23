package state.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @class public class AkkaContainer
 * @brief AkkaContainer class
 */
public class AkkaContainer {

    private static final Logger logger = LoggerFactory.getLogger(AkkaContainer.class);

    private final ActorSystem actorSystem;
    private final Map<String, ActorRef> actorRefMap = new ConcurrentHashMap<>();

    private final String name;

    public AkkaContainer(String name) {
        this.name = name;
        this.actorSystem = ActorSystem.create(name);
    }

    public void addActorRef(String name, Props props) {
        if (getActorRef(name) != null) { return; }
        actorRefMap.putIfAbsent(
                name,
                actorSystem.actorOf(props, name)
        );
    }

    public void removeActorRef(String name) {
        actorRefMap.remove(name);
    }

    public void removeAllActorRefs() {
        actorRefMap.clear();
    }

    public ActorRef getActorRef(String name) {
        return actorRefMap.get(name);
    }

    public boolean tell (String name, Object msg) {
        ActorRef actorRef = getActorRef(name);
        if (actorRef == null) { return false; }

        actorRef.tell(msg, actorRef);
        return true;
    }

    public Object ask (String name, Object msg) {
        ActorRef actorRef = getActorRef(name);
        if (actorRef == null) { return null; }

        Timeout timeout = new Timeout(Duration.create(1, TimeUnit.SECONDS));
        Future<Object> ask = Patterns.ask(actorRef, msg, timeout);
        ask.onComplete(new OnComplete<Object>() {
            @Override
            public void onComplete(Throwable throwable, Object o) {
                if (throwable != null) {
                    logger.warn("Fail to complete the ask. (actorName={}, msg={})", name, o, throwable);
                } else {
                    logger.debug("Success to complete the ask. (actorName={}, msg={})", name, o);
                }
            }
        }, actorSystem.dispatcher());

        try {
            return Await.result(ask, timeout.duration());
        } catch (Exception e) {
            logger.warn("Fail to get the result. (actorName={}, msg={})", name, msg, e);
        }

        return null;
    }

    public String getName() {
        return name;
    }

}

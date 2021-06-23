package base.akka.media.base;

import akka.actor.AbstractActor;
import akka.actor.Props;
import base.squirrel.media.base.MediaState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @class public class MediaActor extends AbstractActor
 * @brief MediaActor class
 */
public class MediaActor extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(MediaActor.class);

    public static final String MEDIA_ACTOR_NAME = "MediaActor";

    private final AtomicReference<String> mediaState = new AtomicReference<>(MediaState.IDLE_STATE);

    public MediaActor() {
        // Nothing
    }

    static public Props props() {
        return Props.create(MediaActor.class, MediaActor::new);
    }

    static public class Transition {
        public final String nextMediaState;

        public Transition(String nextMediaState) {
            this.nextMediaState = nextMediaState;
        }
    }

    static public class GetState {
        public GetState () {
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // match 함수를 이용해서 여러 상태 Actor 로 필요한 상태 천이 메시지를 보낼 수 있다.
                .match(GetState.class, getState -> {
                    String mediaState = getMediaState();
                    logger.info("Cur media state: {}", mediaState);

                    sender().tell(mediaState, this.self());
                })
                .match(Transition.class, transition -> { // Action Message
                    logger.info("Transition: {} > {}", this.mediaState, transition.nextMediaState);
                    setMediaState(transition.nextMediaState);

                    sender().tell(true, this.self());

                    /*StateManager stateManager = StateManager.getInstance();
                    AkkaContainer akkaContainer = stateManager.getAkkaContainer(AkkaMediaStateTest.MEDIA_STATE_NAME);
                    if (akkaContainer != null) {
                        akkaContainer.tell(PrinterActor.PRINTER_ACTOR_NAME, new PrinterActor.Printing(this.mediaState));
                    }*/
                })
                .build();
    }

    public void setMediaState(String mediaState) {
        this.mediaState.set(mediaState);
    }

    public String getMediaState() {
        return mediaState.get();
    }
}

package base.akka.media.base;

import akka.actor.AbstractActor;
import akka.actor.Props;
import base.akka.media.AkkaMediaStateTest;
import base.squirrel.media.base.MediaState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.akka.AkkaContainer;

/**
 * @class public class MediaActor extends AbstractActor
 * @brief MediaActor class
 */
public class MediaActor extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(MediaActor.class);

    public static final String MEDIA_ACTOR_NAME = "MediaActor";

    private String mediaState = MediaState.IDLE_STATE;

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

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // match 함수를 이용해서 여러 상태 Actor 로 필요한 상태 천이 메시지를 보낼 수 있다.
                .match(Transition.class, transition -> { // Action Message
                    logger.info("Transition: {} > {}", this.mediaState, transition.nextMediaState);
                    this.mediaState = transition.nextMediaState;

                    StateManager stateManager = StateManager.getInstance();
                    AkkaContainer akkaContainer = stateManager.getAkkaContainer(AkkaMediaStateTest.MEDIA_STATE_NAME);
                    akkaContainer.tell(PrinterActor.PRINTER_ACTOR_NAME, new PrinterActor.Printing(this.mediaState));
                })
                .build();
    }
}

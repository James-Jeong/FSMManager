package base.akka.media;

import base.akka.media.base.MediaActor;
import base.akka.media.base.PrinterActor;
import base.squirrel.media.base.MediaState;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.akka.AkkaContainer;

/**
 * @class public class AkkaMediaStateTest
 * @brief AkkaMediaStateTest class
 */
public class AkkaMediaStateTest {

    private static final Logger logger = LoggerFactory.getLogger(AkkaMediaStateTest.class);

    private final StopWatch stopWatch = new StopWatch();

    ////////////////////////////////////////////////////////////////////////////////

    public static final String MEDIA_STATE_NAME = "media_state";

    private final StateManager stateManager = StateManager.getInstance();

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        stateManager.addAkkaContainer(MEDIA_STATE_NAME);

        stateManager.getAkkaContainer(MEDIA_STATE_NAME).addActorRef(
                PrinterActor.PRINTER_ACTOR_NAME, PrinterActor.props()
        );

        stateManager.getAkkaContainer(MEDIA_STATE_NAME).addActorRef(
                MediaActor.MEDIA_ACTOR_NAME, MediaActor.props()
        );

        normalTest();

        stateManager.removeAkkaContainer(MEDIA_STATE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void mediaStart () {
        AkkaContainer akkaContainer = stateManager.getAkkaContainer(MEDIA_STATE_NAME);

        String curMediaState = (String) akkaContainer.ask(
                MediaActor.MEDIA_ACTOR_NAME,
                new MediaActor.GetState()
        );

        if (curMediaState.equals(MediaState.IDLE_STATE)) {
            Assert.assertTrue((boolean) akkaContainer.ask(
                    MediaActor.MEDIA_ACTOR_NAME,
                    new MediaActor.Transition(MediaState.ACTIVE_REQUEST))
            );

            logger.info("@ Media is started!");
        } else {
            logger.warn("# Fail to start Media!");
        }
    }

    public void mediaStop () {
        AkkaContainer akkaContainer = stateManager.getAkkaContainer(MEDIA_STATE_NAME);

        String curMediaState = (String) akkaContainer.ask(
                MediaActor.MEDIA_ACTOR_NAME,
                new MediaActor.GetState()
        );

        if (curMediaState.equals(MediaState.ACTIVE_STATE)) {
            Assert.assertTrue((boolean) akkaContainer.ask(
                    MediaActor.MEDIA_ACTOR_NAME,
                    new MediaActor.Transition(MediaState.IDLE_REQUEST))
            );

            logger.info("@ Media is stopped!");
        } else {
            logger.warn("# Fail to stop Media!");
        }
    }

    public void mediaCreateSuccess () {
        AkkaContainer akkaContainer = stateManager.getAkkaContainer(MEDIA_STATE_NAME);

        String curMediaState = (String) akkaContainer.ask(
                MediaActor.MEDIA_ACTOR_NAME,
                new MediaActor.GetState()
        );

        if (curMediaState.equals(MediaState.ACTIVE_REQUEST)) {
            Assert.assertTrue((boolean) akkaContainer.ask(
                    MediaActor.MEDIA_ACTOR_NAME,
                    new MediaActor.Transition(MediaState.ACTIVE_STATE))
            );

            logger.info("@ Success to create media!");
        } else {
            logger.warn("# Fail to create Media!");
        }
    }

    public void mediaCreateFail () {
        logger.info("@ Fail to create media!");
    }

    public void mediaDeleteSuccess () {
        AkkaContainer akkaContainer = stateManager.getAkkaContainer(MEDIA_STATE_NAME);

        String curMediaState = (String) akkaContainer.ask(
                MediaActor.MEDIA_ACTOR_NAME,
                new MediaActor.GetState()
        );

        if (curMediaState.equals(MediaState.IDLE_REQUEST)) {
            Assert.assertTrue((boolean) akkaContainer.ask(
                    MediaActor.MEDIA_ACTOR_NAME,
                    new MediaActor.Transition(MediaState.IDLE_STATE))
            );

            logger.info("@ Success to delete media!");
        } else {
            logger.warn("# Fail to delete Media!");
        }
    }

    public void mediaDeleteFail () {
        logger.info("@ Fail to delete media!");
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest () {
        ////////////////////////////////////////////////////////////////////////////////
        // 1. 상태 천이
        this.stopWatch.start();

        mediaStart();
        mediaCreateSuccess();
        mediaStop();
        mediaDeleteSuccess();

        this.stopWatch.stop();
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ////////////////////////////////////////////////////////////////////////////////
    }

}

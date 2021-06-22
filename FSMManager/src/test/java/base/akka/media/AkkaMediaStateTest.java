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

    private static final String MEDIA_START_EVENT = "media_start_success";
    private static final String MEDIA_STOP_EVENT = "media_stop_success";
    private static final String MEDIA_CREATE_SUCCESS_EVENT = "media_create_success";
    private static final String MEDIA_CREATE_FAIL_EVENT = "media_create_fail";
    private static final String MEDIA_DELETE_SUCCESS_EVENT = "media_delete_success";
    private static final String MEDIA_DELETE_FAIL_EVENT = "media_delete_fail";

    ////////////////////////////////////////////////////////////////////////////////

    public static final String MEDIA_STATE_NAME = "media_state";

    private final StateManager stateManager = StateManager.getInstance();

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        stateManager.addAkkaContainer(MEDIA_STATE_NAME);

        stateManager.getAkkaContainer(MEDIA_STATE_NAME).addActor(
                PrinterActor.PRINTER_ACTOR_NAME, PrinterActor.props()
        );

        stateManager.getAkkaContainer(MEDIA_STATE_NAME).addActor(
                MediaActor.MEDIA_ACTOR_NAME, MediaActor.props()
        );

        normalTest();

        stateManager.removeAkkaContainer(MEDIA_STATE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void mediaStart () {
        AkkaContainer akkaContainer = stateManager.getAkkaContainer(MEDIA_STATE_NAME);
        Assert.assertTrue(akkaContainer.tell(MediaActor.MEDIA_ACTOR_NAME,
                new MediaActor.Transition(MediaState.ACTIVE_REQUEST))
        );
        logger.info("@ Media is started!");
    }

    public void mediaStop () {
        AkkaContainer akkaContainer = stateManager.getAkkaContainer(MEDIA_STATE_NAME);
        Assert.assertTrue(akkaContainer.tell(MediaActor.MEDIA_ACTOR_NAME,
                new MediaActor.Transition(MediaState.IDLE_REQUEST))
        );
        logger.info("@ Media is stopped!");
    }

    public void mediaCreateSuccess () {
        AkkaContainer akkaContainer = stateManager.getAkkaContainer(MEDIA_STATE_NAME);
        Assert.assertTrue(akkaContainer.tell(MediaActor.MEDIA_ACTOR_NAME,
                new MediaActor.Transition(MediaState.ACTIVE_STATE))
        );
        logger.info("@ Success to create media!");
    }

    public void mediaCreateFail () {
        logger.info("@ Fail to create media!");
    }

    public void mediaDeleteSuccess () {
        AkkaContainer akkaContainer = stateManager.getAkkaContainer(MEDIA_STATE_NAME);
        Assert.assertTrue(akkaContainer.tell(MediaActor.MEDIA_ACTOR_NAME,
                new MediaActor.Transition(MediaState.IDLE_STATE))
        );
        logger.info("@ Success to delete media!");
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

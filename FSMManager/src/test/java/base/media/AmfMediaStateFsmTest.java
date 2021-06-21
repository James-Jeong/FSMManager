package base.media;

import base.media.base.MediaEvent;
import base.media.base.MediaFsm;
import base.media.base.MediaState;
import com.google.common.util.concurrent.FutureCallback;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.base.CallBack;
import state.base.TransitionContext;

import javax.print.attribute.standard.Media;

/**
 * @class public class AmfMediaStateTest
 * @brief AmfMediaStateTest class
 */
public class AmfMediaStateFsmTest {

    private static final Logger logger = LoggerFactory.getLogger(AmfMediaStateFsmTest.class);

    ////////////////////////////////////////////////////////////////////////////////

    private static final String MEDIA_STATE_NAME = "media_state";

    ////////////////////////////////////////////////////////////////////////////////
    private final StateManager stateManager = StateManager.getInstance();


    @Test
    public void testStart () {
        stateManager.addFsmContainer(MEDIA_STATE_NAME,
                new MediaFsm(),
                new MediaState(),
                new MediaEvent(),
                new TransitionContext()
        );

        normalTest();
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest () {
        ////////////////////////////////////////////////////////////////////////////////
        // 1. 상태 정의
        stateManager.setFsmCondition(MEDIA_STATE_NAME, MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST, MediaEvent.MEDIA_START_EVENT);

        stateManager.setFsmOnEntry(MEDIA_STATE_NAME, MediaState.ACTIVE_REQUEST, "mediaStart");
        stateManager.setFsmCondition(MEDIA_STATE_NAME, MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE, MediaEvent.MEDIA_CREATE_SUCCESS_EVENT);
        stateManager.setFsmCondition(MEDIA_STATE_NAME, MediaState.ACTIVE_REQUEST, MediaState.IDLE_STATE, MediaEvent.MEDIA_CREATE_FAIL_EVENT);
        stateManager.setFsmOnEntry(MEDIA_STATE_NAME, MediaState.ACTIVE_STATE, "mediaCreateSuccess");

        stateManager.setFsmCondition(MEDIA_STATE_NAME, MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST, MediaEvent.MEDIA_STOP_EVENT);

        stateManager.setFsmOnEntry(MEDIA_STATE_NAME, MediaState.IDLE_REQUEST, "mediaStop");
        stateManager.setFsmCondition(MEDIA_STATE_NAME, MediaState.IDLE_REQUEST, MediaState.IDLE_STATE, MediaEvent.MEDIA_DELETE_SUCCESS_EVENT);
        stateManager.setFsmCondition(MEDIA_STATE_NAME, MediaState.IDLE_REQUEST, MediaState.ACTIVE_STATE, MediaEvent.MEDIA_DELETE_FAIL_EVENT);
        stateManager.setFsmOnEntry(MEDIA_STATE_NAME, MediaState.ACTIVE_STATE, "mediaDeleteSuccess");

        //stateManager.setFsmFinalState(MEDIA_STATE_NAME, MediaState.IDLE_STATE);
        stateManager.buildFsm(MEDIA_STATE_NAME, MediaState.IDLE_STATE, true, null);
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 2. 상태 천이

/*        FutureCallback<Void> futureCallback = new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                logger.info("SUCCESS");
            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.warn("FAIL");
            }
        };*/

        logger.info("Current State: {}", stateManager.getFsmCurState(MEDIA_STATE_NAME));

        stateManager.fireFsm(MEDIA_STATE_NAME, MediaEvent.MEDIA_START_EVENT, null);
        logger.info("Current State: {}", stateManager.getFsmCurState(MEDIA_STATE_NAME));

        stateManager.fireFsm(MEDIA_STATE_NAME, MediaEvent.MEDIA_CREATE_SUCCESS_EVENT, null);
        logger.info("Current State: {}", stateManager.getFsmCurState(MEDIA_STATE_NAME));

        stateManager.fireFsm(MEDIA_STATE_NAME, MediaEvent.MEDIA_STOP_EVENT, null);
        logger.info("Current State: {}", stateManager.getFsmCurState(MEDIA_STATE_NAME));

        stateManager.fireFsm(MEDIA_STATE_NAME, MediaEvent.MEDIA_DELETE_SUCCESS_EVENT, null);
        logger.info("Current State: {}", stateManager.getFsmCurState(MEDIA_STATE_NAME));

        ////////////////////////////////////////////////////////////////////////////////
    }

}

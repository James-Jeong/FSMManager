package base.media;

import base.media.base.MediaEvent;
import base.media.base.MediaFsm;
import base.media.base.MediaState;
import com.google.common.util.concurrent.FutureCallback;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;

/**
 * @class public class AmfMediaStateTest
 * @brief AmfMediaStateTest class
 */
public class AmfMediaStateFsmTest {

    private static final Logger logger = LoggerFactory.getLogger(AmfMediaStateFsmTest.class);

    private final StateManager stateManager = StateManager.getInstance();

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        stateManager.addFsmContainer(MediaFsm.MEDIA_STATE_NAME,
                new MediaFsm(),
                new MediaState(),
                new MediaEvent()
        );

        normalTest();

        stateManager.removeFsmContainer(MediaFsm.MEDIA_STATE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest () {
        ////////////////////////////////////////////////////////////////////////////////
        // 1. CallBack 함수 정의
        FutureCallback<Object> futureCallback = new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object param) {
                logger.info("SUCCESS: Current State = {}", param);
            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.warn("FAIL: {}", (throwable.getCause() == null ? null : throwable.getCause().toString()));
            }
        };
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 2. 상태 정의
        stateManager.setFsmCondition(MediaFsm.MEDIA_STATE_NAME, MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST, MediaEvent.MEDIA_START_EVENT);

        stateManager.setFsmOnEntry(MediaFsm.MEDIA_STATE_NAME, MediaState.ACTIVE_REQUEST, "mediaStart");
        stateManager.setFsmCondition(MediaFsm.MEDIA_STATE_NAME, MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE, MediaEvent.MEDIA_CREATE_SUCCESS_EVENT);
        stateManager.setFsmCondition(MediaFsm.MEDIA_STATE_NAME, MediaState.ACTIVE_REQUEST, MediaState.IDLE_STATE, MediaEvent.MEDIA_CREATE_FAIL_EVENT);
        stateManager.setFsmOnEntry(MediaFsm.MEDIA_STATE_NAME, MediaState.ACTIVE_STATE, "mediaCreateSuccess");

        stateManager.setFsmCondition(MediaFsm.MEDIA_STATE_NAME, MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST, MediaEvent.MEDIA_STOP_EVENT);

        stateManager.setFsmOnEntry(MediaFsm.MEDIA_STATE_NAME, MediaState.IDLE_REQUEST, "mediaStop");
        stateManager.setFsmCondition(MediaFsm.MEDIA_STATE_NAME, MediaState.IDLE_REQUEST, MediaState.IDLE_STATE, MediaEvent.MEDIA_DELETE_SUCCESS_EVENT);
        stateManager.setFsmCondition(MediaFsm.MEDIA_STATE_NAME, MediaState.IDLE_REQUEST, MediaState.ACTIVE_STATE, MediaEvent.MEDIA_DELETE_FAIL_EVENT);
        stateManager.setFsmOnEntry(MediaFsm.MEDIA_STATE_NAME, MediaState.ACTIVE_STATE, "mediaDeleteSuccess");

        //stateManager.setFsmFinalState(MediaFsm.MEDIA_STATE_NAME, MediaState.IDLE_STATE);
        stateManager.buildFsm(MediaFsm.MEDIA_STATE_NAME, MediaState.IDLE_STATE, true);
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        Assert.assertEquals(MediaState.IDLE_STATE, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(MediaFsm.MEDIA_STATE_NAME, MediaEvent.MEDIA_START_EVENT, futureCallback));
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(MediaFsm.MEDIA_STATE_NAME, MediaEvent.MEDIA_CREATE_SUCCESS_EVENT, futureCallback));
        Assert.assertEquals(MediaState.ACTIVE_STATE, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(MediaFsm.MEDIA_STATE_NAME, MediaEvent.MEDIA_STOP_EVENT, futureCallback));
        Assert.assertEquals(MediaState.IDLE_REQUEST, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(MediaFsm.MEDIA_STATE_NAME, MediaEvent.MEDIA_DELETE_SUCCESS_EVENT, futureCallback));
        Assert.assertEquals(MediaState.IDLE_STATE, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));
        ////////////////////////////////////////////////////////////////////////////////
    }

}

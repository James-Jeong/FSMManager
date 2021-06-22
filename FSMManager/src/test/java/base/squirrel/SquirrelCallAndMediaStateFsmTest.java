package base.squirrel;

import base.squirrel.call.base.CallEvent;
import base.squirrel.call.base.CallFsm;
import base.squirrel.call.base.CallState;
import base.squirrel.media.base.MediaEvent;
import base.squirrel.media.base.MediaFsm;
import base.squirrel.media.base.MediaState;
import com.google.common.util.concurrent.FutureCallback;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;

/**
 * @class public class AmfCallStateTest
 * @brief Amf Call State Test class
 */
public class SquirrelCallAndMediaStateFsmTest {

    private static final Logger logger = LoggerFactory.getLogger(SquirrelCallAndMediaStateFsmTest.class);

    private final StateManager stateManager = StateManager.getInstance();

    private final StopWatch stopWatch = new StopWatch();

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        stateManager.addFsmContainer(CallFsm.CALL_STATE_NAME,
                new CallFsm(),
                new CallState(),
                new CallEvent()
        );

        stateManager.addFsmContainer(MediaFsm.MEDIA_STATE_NAME,
                new MediaFsm(),
                new MediaState(),
                new MediaEvent()
        );

        normalTest();

        stateManager.removeFsmContainer(CallFsm.CALL_STATE_NAME);
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
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.IDLE, CallState.INIT, CallEvent.CALL_INIT_EVENT);

        stateManager.setFsmOnEntry(CallFsm.CALL_STATE_NAME, CallState.OFFER, "callStart");
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.INIT, CallState.OFFER, CallEvent.CALL_START_EVENT);
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.OFFER, CallState.INIT, CallEvent.CALL_FAIL_EVENT);

        stateManager.setFsmOnEntry(CallFsm.CALL_STATE_NAME, CallState.EARLY_NEGO_REQ, "earlyNegoStart");
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.OFFER, CallState.EARLY_NEGO_REQ, CallEvent.OFFER_EARLY_NEGO_START_EVENT);

        stateManager.setFsmOnEntry(CallFsm.CALL_STATE_NAME, CallState.NEGO_REQ, "negoStart");
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.OFFER, CallState.NEGO_REQ, CallEvent.OFFER_NEGO_START_EVENT);

        stateManager.setFsmOnEntry(CallFsm.CALL_STATE_NAME, CallState.HANGUP_REQ, "hangupStart");
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.OFFER, CallState.HANGUP_REQ, CallEvent.OFFER_STOP_EVENT);

        stateManager.setFsmOnEntry(CallFsm.CALL_STATE_NAME, CallState.EARLY_MEDIA, "earlyMediaStart");
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.EARLY_NEGO_REQ, CallState.EARLY_MEDIA, CallEvent.EARLY_MEDIA_START_EVENT);

        stateManager.setFsmOnEntry(CallFsm.CALL_STATE_NAME, CallState.INACTIVE, "inActiveStart");
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.EARLY_NEGO_REQ, CallState.INACTIVE, CallEvent.EARLY_NEGO_INACTIVE_START_EVENT);

        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.EARLY_MEDIA, CallState.NEGO_REQ, CallEvent.EARLY_NEGO_NEGO_START_EVENT);
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.EARLY_MEDIA, CallState.HANGUP_REQ, CallEvent.EARLY_NEGO_STOP_EVENT);

        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.NEGO_REQ, CallState.ACTIVE, CallEvent.ACTIVE_START_EVENT);
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.NEGO_REQ, CallState.INACTIVE, CallEvent.NEGO_INACTIVE_START_EVENT);
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.NEGO_REQ, CallState.HANGUP_REQ, CallEvent.NEGO_STOP_EVENT);

        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.ACTIVE, CallState.HANGUP_REQ, CallEvent.ACTIVE_STOP_EVENT);

        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.INACTIVE, CallState.HANGUP_REQ, CallEvent.INACTIVE_STOP_EVENT);

        stateManager.setFsmOnEntry(CallFsm.CALL_STATE_NAME, CallState.INIT, "callInitSuccess");
        stateManager.setFsmCondition(CallFsm.CALL_STATE_NAME, CallState.HANGUP_REQ, CallState.INIT, CallEvent.CALL_STOP_DONE_SUCCESS_EVENT);

        stateManager.buildFsm(CallFsm.CALL_STATE_NAME, CallState.IDLE, true);

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
        this.stopWatch.start();
        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.CALL_INIT_EVENT, futureCallback));

        Assert.assertEquals(CallState.INIT, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.CALL_START_EVENT, futureCallback));
        Assert.assertEquals(CallState.OFFER, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.OFFER_EARLY_NEGO_START_EVENT, futureCallback));
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(MediaFsm.MEDIA_STATE_NAME, MediaEvent.MEDIA_START_EVENT, futureCallback));
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.EARLY_MEDIA_START_EVENT, futureCallback));
        Assert.assertEquals(CallState.EARLY_MEDIA, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(MediaFsm.MEDIA_STATE_NAME, MediaEvent.MEDIA_CREATE_SUCCESS_EVENT, futureCallback));
        Assert.assertEquals(MediaState.ACTIVE_STATE, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.EARLY_NEGO_NEGO_START_EVENT, futureCallback));
        Assert.assertEquals(CallState.NEGO_REQ, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.ACTIVE_START_EVENT, futureCallback));
        Assert.assertEquals(CallState.ACTIVE, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.ACTIVE_STOP_EVENT, futureCallback));
        Assert.assertEquals(CallState.HANGUP_REQ, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(MediaFsm.MEDIA_STATE_NAME, MediaEvent.MEDIA_STOP_EVENT, futureCallback));
        Assert.assertEquals(MediaState.IDLE_REQUEST, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.CALL_STOP_DONE_SUCCESS_EVENT, futureCallback));
        Assert.assertEquals(CallState.INIT, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(MediaFsm.MEDIA_STATE_NAME, MediaEvent.MEDIA_DELETE_SUCCESS_EVENT, futureCallback));
        Assert.assertEquals(MediaState.IDLE_STATE, stateManager.getFsmCurState(MediaFsm.MEDIA_STATE_NAME));

        this.stopWatch.stop();
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }

}

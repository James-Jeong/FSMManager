package base.call;

import base.call.base.CallEvent;
import base.call.base.CallFsm;
import base.call.base.CallGlobalContext;
import base.call.base.CallState;
import base.media.base.MediaEvent;
import base.media.base.MediaFsm;
import base.media.base.MediaGlobalContext;
import base.media.base.MediaState;
import com.google.common.util.concurrent.FutureCallback;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.base.CallBack;
import state.base.TransitionContext;
import state.module.StateHandler;

/**
 * @class public class AmfCallStateTest
 * @brief Amf Call State Test class
 */
public class AmfCallStateFsmTest {

    private static final Logger logger = LoggerFactory.getLogger(AmfCallStateFsmTest.class);

    private final StateManager stateManager = StateManager.getInstance();

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        stateManager.addFsmContainer(CallFsm.CALL_STATE_NAME,
                new CallFsm(),
                new CallState(),
                new CallEvent(),
                new TransitionContext(),
                CallGlobalContext.class
        );


        normalTest();
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

        CallGlobalContext callGlobalContext = new CallGlobalContext("010-1234-5678");
        stateManager.buildFsm(CallFsm.CALL_STATE_NAME, CallState.IDLE, false, callGlobalContext);
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.CALL_INIT_EVENT, futureCallback);

        Assert.assertEquals(CallState.INIT, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.CALL_START_EVENT, futureCallback);
        Assert.assertEquals(CallState.OFFER, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.OFFER_EARLY_NEGO_START_EVENT, futureCallback);
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.EARLY_MEDIA_START_EVENT, futureCallback);
        Assert.assertEquals(CallState.EARLY_MEDIA, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.EARLY_NEGO_NEGO_START_EVENT, futureCallback);
        Assert.assertEquals(CallState.NEGO_REQ, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.ACTIVE_START_EVENT, futureCallback);
        Assert.assertEquals(CallState.ACTIVE, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.ACTIVE_STOP_EVENT, futureCallback);
        Assert.assertEquals(CallState.HANGUP_REQ, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.CALL_STOP_DONE_SUCCESS_EVENT, futureCallback);
        Assert.assertEquals(CallState.INIT, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        ////////////////////////////////////////////////////////////////////////////////
    }

}

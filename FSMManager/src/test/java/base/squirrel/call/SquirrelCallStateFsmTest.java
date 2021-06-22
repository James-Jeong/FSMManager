package base.squirrel.call;

import base.squirrel.call.base.CallEvent;
import base.squirrel.call.base.CallFsm;
import base.squirrel.call.base.CallState;
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
public class SquirrelCallStateFsmTest {

    private static final Logger logger = LoggerFactory.getLogger(SquirrelCallStateFsmTest.class);

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

        normalTest();

        stateManager.removeFsmContainer(CallFsm.CALL_STATE_NAME);
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

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.EARLY_MEDIA_START_EVENT, futureCallback));
        Assert.assertEquals(CallState.EARLY_MEDIA, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.EARLY_NEGO_NEGO_START_EVENT, futureCallback));
        Assert.assertEquals(CallState.NEGO_REQ, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.ACTIVE_START_EVENT, futureCallback));
        Assert.assertEquals(CallState.ACTIVE, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.ACTIVE_STOP_EVENT, futureCallback));
        Assert.assertEquals(CallState.HANGUP_REQ, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        Assert.assertTrue(stateManager.fireFsm(CallFsm.CALL_STATE_NAME, CallEvent.CALL_STOP_DONE_SUCCESS_EVENT, futureCallback));
        Assert.assertEquals(CallState.INIT, stateManager.getFsmCurState(CallFsm.CALL_STATE_NAME));

        this.stopWatch.stop();
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));

        ////////////////////////////////////////////////////////////////////////////////
    }

}

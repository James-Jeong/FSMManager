package base.call;

import base.call.base.CallState;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.base.CallBack;
import state.module.StateHandler;

/**
 * @class public class AmfCallStateTest
 * @brief Amf Call State Test class
 */
public class AmfCallStateTest {

    private static final Logger logger = LoggerFactory.getLogger(AmfCallStateTest.class);

    private static final String CALL_START_EVENT = "call_start";
    private static final String CALL_FAIL_EVENT = "call_fail";

    private static final String OFFER_STOP_EVENT = "offer_stop";
    private static final String NEGO_STOP_EVENT = "nego_stop";
    private static final String EARLY_NEGO_STOP_EVENT = "early_nego_stop";
    private static final String ACTIVE_STOP_EVENT = "active_stop";
    private static final String INACTIVE_STOP_EVENT = "inactive_stop";

    private static final String CALL_STOP_DONE_SUCCESS_EVENT = "call_stop_done_success";
    private static final String CALL_STOP_DONE_FAIL_EVENT = "call_stop_done_fail";
    private static final String OFFER_EARLY_NEGO_START_EVENT = "early_nego_start";
    private static final String EARLY_MEDIA_START_EVENT = "early_media_start";
    private static final String OFFER_NEGO_START_EVENT = "nego_start";
    private static final String EARLY_NEGO_NEGO_START_EVENT = "nego_start";
    private static final String ACTIVE_START_EVENT = "active_start";
    private static final String EARLY_NEGO_INACTIVE_START_EVENT = "early_nego_inactive_start";
    private static final String NEGO_INACTIVE_START_EVENT = "nego_inactive_start";

    ////////////////////////////////////////////////////////////////////////////////

    private static final String CALL_STATE_NAME = "call_state";
    private final StateManager stateManager = StateManager.getInstance();
    private StateHandler callStateHandler = null;

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        stateManager.addStateHandler(CALL_STATE_NAME);
        callStateHandler = stateManager.getStateHandler(CALL_STATE_NAME);

        normalTest();

        stateManager.removeStateHandler(CALL_STATE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void callStart () {
        logger.info("@ Call is started!");
        callStateHandler.fire(CALL_START_EVENT);

        if (callStateHandler.getCallBackResult() == null) {
            logger.info("@ Call is failed!");
            callStateHandler.fire(CALL_FAIL_EVENT);
        }
    }

    public void earlyNegoStart () {
        logger.info("@ Early Nego is started by offer!");
        callStateHandler.fire(OFFER_EARLY_NEGO_START_EVENT);
    }

    public void earlyMediaStart () {
        logger.info("@ Early Media is started!");
        callStateHandler.fire(EARLY_MEDIA_START_EVENT);
    }

    public void activeStart () {
        logger.info("@ Active is started!");
        callStateHandler.fire(ACTIVE_START_EVENT);
    }

    public void earlyNegoInActiveStart () {
        logger.info("@ InActive is started by early_nego!");
        callStateHandler.fire(EARLY_NEGO_INACTIVE_START_EVENT);
    }

    public void negoInActiveStart () {
        logger.info("@ InActive is started by nego!");
        callStateHandler.fire(NEGO_INACTIVE_START_EVENT);
    }

    public void offerNegoStart () {
        logger.info("@ Nego is started by offer!");
        callStateHandler.fire(OFFER_NEGO_START_EVENT);
    }

    public void earlyNegoNegoStart () {
        logger.info("@ Nego is started by early_nego!");
        callStateHandler.fire(EARLY_NEGO_NEGO_START_EVENT);
    }

    public void offerHangupStart () {
        logger.info("@ Hangup is started by offer!");
        callStateHandler.fire(OFFER_STOP_EVENT);
    }

    public void earlyNegoHangupStart () {
        logger.info("@ Hangup is started by early_nego!");
        callStateHandler.fire(EARLY_NEGO_STOP_EVENT);
    }

    public void activeHangupStart() {
        logger.info("@ Hangup is started by nego!");
        callStateHandler.fire(ACTIVE_STOP_EVENT);
    }

    public void callStopSuccess () {
        logger.info("@ Success to stop the call!");
        callStateHandler.fire(CALL_STOP_DONE_SUCCESS_EVENT);
    }

    public void callStopFail () {
        logger.info("@ Fail to stop the call!");
        callStateHandler.fire(CALL_STOP_DONE_FAIL_EVENT);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest () {
        ////////////////////////////////////////////////////////////////////////////////
        // 1. CallBack 함수 정의
        CallBack callBack = object -> {
            if (object.length == 0) { return null; }

            String stateName = (String) object[0];
            System.out.println("CallBack is called. (curState=" + stateName + ")");

            return stateName;
        };
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 2. 상태 정의
        Assert.assertTrue(callStateHandler.addState(CALL_START_EVENT, CallState.INIT, CallState.OFFER, callBack));

        Assert.assertTrue(callStateHandler.addState(CALL_FAIL_EVENT, CallState.OFFER, CallState.INIT, callBack));
        Assert.assertTrue(callStateHandler.addState(OFFER_EARLY_NEGO_START_EVENT, CallState.OFFER, CallState.EARLY_NEGO_REQ, callBack));
        Assert.assertTrue(callStateHandler.addState(OFFER_NEGO_START_EVENT, CallState.OFFER, CallState.NEGO_REQ, callBack));
        Assert.assertTrue(callStateHandler.addState(OFFER_STOP_EVENT, CallState.OFFER, CallState.HANGUP_REQ, callBack));

        Assert.assertTrue(callStateHandler.addState(EARLY_MEDIA_START_EVENT, CallState.EARLY_NEGO_REQ, CallState.EARLY_MEDIA, callBack));
        Assert.assertTrue(callStateHandler.addState(EARLY_NEGO_INACTIVE_START_EVENT, CallState.EARLY_NEGO_REQ, CallState.INACTIVE, callBack));

        Assert.assertTrue(callStateHandler.addState(EARLY_NEGO_NEGO_START_EVENT, CallState.EARLY_MEDIA, CallState.NEGO_REQ, callBack));
        Assert.assertTrue(callStateHandler.addState(EARLY_NEGO_STOP_EVENT, CallState.EARLY_MEDIA, CallState.HANGUP_REQ, callBack));

        Assert.assertTrue(callStateHandler.addState(ACTIVE_START_EVENT, CallState.NEGO_REQ, CallState.ACTIVE, callBack));
        Assert.assertTrue(callStateHandler.addState(NEGO_INACTIVE_START_EVENT, CallState.NEGO_REQ, CallState.INACTIVE, callBack));
        Assert.assertTrue(callStateHandler.addState(NEGO_STOP_EVENT, CallState.NEGO_REQ, CallState.HANGUP_REQ, callBack));

        Assert.assertTrue(callStateHandler.addState(ACTIVE_STOP_EVENT, CallState.ACTIVE, CallState.HANGUP_REQ, callBack));

        Assert.assertTrue(callStateHandler.addState(INACTIVE_STOP_EVENT, CallState.INACTIVE, CallState.HANGUP_REQ, callBack));

        Assert.assertTrue(callStateHandler.addState(CALL_STOP_DONE_SUCCESS_EVENT, CallState.HANGUP_REQ, CallState.INIT, callBack));
        Assert.assertTrue(callStateHandler.addState(CALL_STOP_DONE_FAIL_EVENT, CallState.HANGUP_REQ, CallState.IDLE, callBack));

        Assert.assertNotNull(callStateHandler.getStateList());
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        callStart();
        /*Assert.assertNull(stateManager.nextState(CallState.INACTIVE.name())); // 비정상 천이 > 아직 처음 상태가 정의되지 않음
        Assert.assertNull(stateManager.getCallBackResult());*/

        callStateHandler.setCurState(CallState.INIT);

        callStart();
        Assert.assertEquals(CallState.OFFER, callStateHandler.getCallBackResult());

        earlyNegoStart();
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, callStateHandler.getCallBackResult());

        earlyMediaStart();
        Assert.assertEquals(CallState.EARLY_MEDIA, callStateHandler.getCallBackResult());

        earlyNegoNegoStart();
        Assert.assertEquals(CallState.NEGO_REQ, callStateHandler.getCallBackResult());

        activeStart();
        Assert.assertEquals(CallState.ACTIVE, callStateHandler.getCallBackResult());

        activeHangupStart();
        Assert.assertEquals(CallState.HANGUP_REQ, callStateHandler.getCallBackResult());

        callStopSuccess();
        Assert.assertEquals(CallState.INIT, callStateHandler.getCallBackResult());


/*        Assert.assertNotNull(callStateHandler.nextState(CallState.OFFER));
        Assert.assertEquals(CallState.OFFER, callStateHandler.getCallBackResult());

        Assert.assertNull(callStateHandler.nextState(CallState.INACTIVE)); // 비정상 천이 > 정의되지 않은 상태로 천이
        Assert.assertNull(callStateHandler.getCallBackResult());

        Assert.assertNotNull(callStateHandler.nextState(CallState.EARLY_NEGO_REQ));
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, callStateHandler.getCallBackResult());

        Assert.assertNotNull(callStateHandler.nextState(CallState.EARLY_MEDIA));
        Assert.assertEquals(CallState.EARLY_MEDIA, callStateHandler.getCallBackResult());

        Assert.assertNotNull(callStateHandler.nextState(CallState.NEGO_REQ));
        Assert.assertEquals(CallState.NEGO_REQ, callStateHandler.getCallBackResult());

        Assert.assertNotNull(callStateHandler.nextState(CallState.ACTIVE));
        Assert.assertEquals(CallState.ACTIVE, callStateHandler.getCallBackResult());

        Assert.assertNull(callStateHandler.nextState(CallState.ACTIVE)); // 비정상 천이 > 동일 상태로 천이
        Assert.assertNull(callStateHandler.getCallBackResult());

        Assert.assertNotNull(callStateHandler.nextState(CallState.HANGUP_REQ));
        Assert.assertEquals(CallState.HANGUP_REQ, callStateHandler.getCallBackResult());

        Assert.assertNotNull(callStateHandler.nextState(CallState.INIT));
        Assert.assertEquals(CallState.INIT, callStateHandler.getCallBackResult());*/
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 4. 상태 삭제

/*        Assert.assertTrue(callStateHandler.removeFromState(CallState.HANGUP_REQ));
        Assert.assertFalse(callStateHandler.removeFromState(CallState.HANGUP_REQ));

        Assert.assertTrue(CallState.NEGO_REQ, callStateHandler.removeToStateByFromState(CallState.EARLY_MEDIA));
        Assert.assertFalse(CallState.NEGO_REQ, callStateHandler.removeToStateByFromState(CallState.EARLY_MEDIA));

        Assert.assertNotNull(callStateHandler.nextState(CallState.OFFER));
        Assert.assertNotNull(callStateHandler.nextState(CallState.EARLY_NEGO_REQ));
        Assert.assertNotNull(callStateHandler.nextState(CallState.EARLY_MEDIA));
        Assert.assertNull(callStateHandler.nextState(CallState.NEGO_REQ));

        Assert.assertNull(callStateHandler.nextState(CallState.HANGUP_REQ));*/

        ////////////////////////////////////////////////////////////////////////////////
    }

}
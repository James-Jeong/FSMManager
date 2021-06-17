package base;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.CallBack;
import state.StateManager;

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

    private final StateManager stateManager = StateManager.getInstance();

    @Test
    public void testStart () {
        normalTest();
    }

    public void callStart () {
        logger.debug("@ Call is started!");
        stateManager.fire(CALL_START_EVENT);

        if (stateManager.getCallBackResult() == null) {
            logger.debug("@ Call is failed!");
            stateManager.fire(CALL_FAIL_EVENT);
        }
    }

    public void earlyNegoStart () {
        logger.debug("@ Early Nego is started by offer!");
        stateManager.fire(OFFER_EARLY_NEGO_START_EVENT);
    }

    public void earlyMediaStart () {
        logger.debug("@ Early Media is started!");
        stateManager.fire(EARLY_MEDIA_START_EVENT);
    }

    public void activeStart () {
        logger.debug("@ Active is started!");
        stateManager.fire(ACTIVE_START_EVENT);
    }

    public void earlyNegoInActiveStart () {
        logger.debug("@ InActive is started by early_nego!");
        stateManager.fire(EARLY_NEGO_INACTIVE_START_EVENT);
    }

    public void negoInActiveStart () {
        logger.debug("@ InActive is started by nego!");
        stateManager.fire(NEGO_INACTIVE_START_EVENT);
    }

    public void offerNegoStart () {
        logger.debug("@ Nego is started by offer!");
        stateManager.fire(OFFER_NEGO_START_EVENT);
    }

    public void earlyNegoNegoStart () {
        logger.debug("@ Nego is started by early_nego!");
        stateManager.fire(EARLY_NEGO_NEGO_START_EVENT);
    }

    public void offerHangupStart () {
        logger.debug("@ Hangup is started by offer!");
        stateManager.fire(OFFER_STOP_EVENT);
    }

    public void earlyNegoHangupStart () {
        logger.debug("@ Hangup is started by early_nego!");
        stateManager.fire(EARLY_NEGO_STOP_EVENT);
    }

    public void activeHangupStart() {
        logger.debug("@ Hangup is started by nego!");
        stateManager.fire(ACTIVE_STOP_EVENT);
    }

    public void callStopSuccess () {
        logger.debug("@ Success to stop the call!");
        stateManager.fire(CALL_STOP_DONE_SUCCESS_EVENT);
    }

    public void callStopFail () {
        logger.debug("@ Fail to stop the call!");
        stateManager.fire(CALL_STOP_DONE_FAIL_EVENT);
    }

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
        Assert.assertTrue(stateManager.addState(CALL_START_EVENT, CallState.INIT.name(), CallState.OFFER.name(), callBack));

        Assert.assertTrue(stateManager.addState(CALL_FAIL_EVENT, CallState.OFFER.name(), CallState.INIT.name(), callBack));
        Assert.assertTrue(stateManager.addState(OFFER_EARLY_NEGO_START_EVENT, CallState.OFFER.name(), CallState.EARLY_NEGO_REQ.name(), callBack));
        Assert.assertTrue(stateManager.addState(OFFER_NEGO_START_EVENT, CallState.OFFER.name(), CallState.NEGO_REQ.name(), callBack));
        Assert.assertTrue(stateManager.addState(OFFER_STOP_EVENT, CallState.OFFER.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(EARLY_MEDIA_START_EVENT, CallState.EARLY_NEGO_REQ.name(), CallState.EARLY_MEDIA.name(), callBack));
        Assert.assertTrue(stateManager.addState(EARLY_NEGO_INACTIVE_START_EVENT, CallState.EARLY_NEGO_REQ.name(), CallState.INACTIVE.name(), callBack));

        Assert.assertTrue(stateManager.addState(EARLY_NEGO_NEGO_START_EVENT, CallState.EARLY_MEDIA.name(), CallState.NEGO_REQ.name(), callBack));
        Assert.assertTrue(stateManager.addState(EARLY_NEGO_STOP_EVENT, CallState.EARLY_MEDIA.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(ACTIVE_START_EVENT, CallState.NEGO_REQ.name(), CallState.ACTIVE.name(), callBack));
        Assert.assertTrue(stateManager.addState(NEGO_INACTIVE_START_EVENT, CallState.NEGO_REQ.name(), CallState.INACTIVE.name(), callBack));
        Assert.assertTrue(stateManager.addState(NEGO_STOP_EVENT, CallState.NEGO_REQ.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(ACTIVE_STOP_EVENT, CallState.ACTIVE.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(INACTIVE_STOP_EVENT, CallState.INACTIVE.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(CALL_STOP_DONE_SUCCESS_EVENT, CallState.HANGUP_REQ.name(), CallState.INIT.name(), callBack));
        Assert.assertTrue(stateManager.addState(CALL_STOP_DONE_FAIL_EVENT, CallState.HANGUP_REQ.name(), CallState.IDLE.name(), callBack));

        Assert.assertNotNull(stateManager.getStateList());
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        callStart();
        /*Assert.assertNull(stateManager.nextState(CallState.INACTIVE.name())); // 비정상 천이 > 아직 처음 상태가 정의되지 않음
        Assert.assertNull(stateManager.getCallBackResult());*/

        stateManager.setCurState(CallState.INIT.name());

        callStart();
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.OFFER.name());

        earlyNegoStart();
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.EARLY_NEGO_REQ.name());

        earlyMediaStart();
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.EARLY_MEDIA.name());

        earlyNegoNegoStart();
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.NEGO_REQ.name());

        activeStart();
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.ACTIVE.name());

        activeHangupStart();
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.HANGUP_REQ.name());

        callStopSuccess();
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.INIT.name());


/*        Assert.assertNotNull(stateManager.nextState(CallState.OFFER.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.OFFER.name());

        Assert.assertNull(stateManager.nextState(CallState.INACTIVE.name())); // 비정상 천이 > 정의되지 않은 상태로 천이
        Assert.assertNull(stateManager.getCallBackResult());

        Assert.assertNotNull(stateManager.nextState(CallState.EARLY_NEGO_REQ.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.EARLY_NEGO_REQ.name());

        Assert.assertNotNull(stateManager.nextState(CallState.EARLY_MEDIA.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.EARLY_MEDIA.name());

        Assert.assertNotNull(stateManager.nextState(CallState.NEGO_REQ.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.NEGO_REQ.name());

        Assert.assertNotNull(stateManager.nextState(CallState.ACTIVE.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.ACTIVE.name());

        Assert.assertNull(stateManager.nextState(CallState.ACTIVE.name())); // 비정상 천이 > 동일 상태로 천이
        Assert.assertNull(stateManager.getCallBackResult());

        Assert.assertNotNull(stateManager.nextState(CallState.HANGUP_REQ.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.HANGUP_REQ.name());

        Assert.assertNotNull(stateManager.nextState(CallState.INIT.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.INIT.name());*/
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 4. 상태 삭제

/*        Assert.assertTrue(stateManager.removeFromState(CallState.HANGUP_REQ.name()));
        Assert.assertFalse(stateManager.removeFromState(CallState.HANGUP_REQ.name()));

        Assert.assertTrue(stateManager.removeToStateByFromState(CallState.EARLY_MEDIA.name(), CallState.NEGO_REQ.name()));
        Assert.assertFalse(stateManager.removeToStateByFromState(CallState.EARLY_MEDIA.name(), CallState.NEGO_REQ.name()));

        Assert.assertNotNull(stateManager.nextState(CallState.OFFER.name()));
        Assert.assertNotNull(stateManager.nextState(CallState.EARLY_NEGO_REQ.name()));
        Assert.assertNotNull(stateManager.nextState(CallState.EARLY_MEDIA.name()));
        Assert.assertNull(stateManager.nextState(CallState.NEGO_REQ.name()));

        Assert.assertNull(stateManager.nextState(CallState.HANGUP_REQ.name()));*/

        ////////////////////////////////////////////////////////////////////////////////
    }

}

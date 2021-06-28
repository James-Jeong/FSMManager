package base.basic.call;

import base.basic.call.base.CallCallBack;
import base.basic.call.base.CallInfo;
import base.basic.call.base.CallState;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.state.StateHandler;

/**
 * @class public class AmfCallStateTest
 * @brief Amf Call State Test class
 */
public class BasicCallStateTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicCallStateTest.class);

    private final StopWatch stopWatch = new StopWatch();

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
        String callId = "Call";
        CallInfo callInfo = new CallInfo(
                callId,
                "01012345678",
                "01056781234"
        );

        stateManager.addStateUnit(callInfo.getSipStateUnitName(), CallState.INIT);
        stateManager.addStateHandler(CALL_STATE_NAME);
        callStateHandler = stateManager.getStateHandler(CALL_STATE_NAME);

        ////////////////////////////////////////////////////////////////////////////////
        // 1. CallBack 함수 정의
        CallCallBack callStartCallBack = new CallCallBack(CALL_START_EVENT);
        CallCallBack callFailCallBack = new CallCallBack(CALL_FAIL_EVENT);
        CallCallBack callOfferEarlyNegoStartCallBack = new CallCallBack(OFFER_EARLY_NEGO_START_EVENT);
        CallCallBack callOfferNegoStartCallBack = new CallCallBack(OFFER_NEGO_START_EVENT);
        CallCallBack callOfferStopCallBack = new CallCallBack(OFFER_STOP_EVENT);
        CallCallBack callEarlyMediaStartCallBack = new CallCallBack(EARLY_MEDIA_START_EVENT);
        CallCallBack callEarlyNegoInactiveStartCallBack = new CallCallBack(EARLY_NEGO_INACTIVE_START_EVENT);
        CallCallBack callEarlyNegoNegoStartCallBack = new CallCallBack(EARLY_NEGO_NEGO_START_EVENT);
        CallCallBack callEarlyNegoStopCallBack = new CallCallBack(EARLY_NEGO_STOP_EVENT);
        CallCallBack callActiveStartCallBack = new CallCallBack(ACTIVE_START_EVENT);
        CallCallBack callNegoInactiveStartCallBack = new CallCallBack(NEGO_INACTIVE_START_EVENT);
        CallCallBack callNegoStopCallBack = new CallCallBack(NEGO_STOP_EVENT);
        CallCallBack callActiveStopCallBack = new CallCallBack(ACTIVE_STOP_EVENT);
        CallCallBack callInactiveStopCallBack = new CallCallBack(INACTIVE_STOP_EVENT);
        CallCallBack callStopDoneSuccessCallBack = new CallCallBack(CALL_STOP_DONE_SUCCESS_EVENT);
        CallCallBack callStopDoneFailCallBack = new CallCallBack(CALL_STOP_DONE_FAIL_EVENT);
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 2. 상태 정의
        Assert.assertTrue(callStateHandler.addState(CALL_START_EVENT, CallState.INIT, CallState.OFFER, callStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CALL_FAIL_EVENT, CallState.OFFER, CallState.INIT, callFailCallBack));
        Assert.assertTrue(callStateHandler.addState(OFFER_EARLY_NEGO_START_EVENT, CallState.OFFER, CallState.EARLY_NEGO_REQ, callOfferEarlyNegoStartCallBack));
        Assert.assertTrue(callStateHandler.addState(OFFER_NEGO_START_EVENT, CallState.OFFER, CallState.NEGO_REQ, callOfferNegoStartCallBack));
        Assert.assertTrue(callStateHandler.addState(OFFER_STOP_EVENT, CallState.OFFER, CallState.HANGUP_REQ, callOfferStopCallBack));
        Assert.assertTrue(callStateHandler.addState(EARLY_MEDIA_START_EVENT, CallState.EARLY_NEGO_REQ, CallState.EARLY_MEDIA, callEarlyMediaStartCallBack));
        Assert.assertTrue(callStateHandler.addState(EARLY_NEGO_INACTIVE_START_EVENT, CallState.EARLY_NEGO_REQ, CallState.INACTIVE, callEarlyNegoInactiveStartCallBack));
        Assert.assertTrue(callStateHandler.addState(EARLY_NEGO_NEGO_START_EVENT, CallState.EARLY_MEDIA, CallState.NEGO_REQ, callEarlyNegoNegoStartCallBack));
        Assert.assertTrue(callStateHandler.addState(EARLY_NEGO_STOP_EVENT, CallState.EARLY_MEDIA, CallState.HANGUP_REQ, callEarlyNegoStopCallBack));
        Assert.assertTrue(callStateHandler.addState(ACTIVE_START_EVENT, CallState.NEGO_REQ, CallState.ACTIVE, callActiveStartCallBack));
        Assert.assertTrue(callStateHandler.addState(NEGO_INACTIVE_START_EVENT, CallState.NEGO_REQ, CallState.INACTIVE, callNegoInactiveStartCallBack));
        Assert.assertTrue(callStateHandler.addState(NEGO_STOP_EVENT, CallState.NEGO_REQ, CallState.HANGUP_REQ, callNegoStopCallBack));
        Assert.assertTrue(callStateHandler.addState(ACTIVE_STOP_EVENT, CallState.ACTIVE, CallState.HANGUP_REQ, callActiveStopCallBack));
        Assert.assertTrue(callStateHandler.addState(INACTIVE_STOP_EVENT, CallState.INACTIVE, CallState.HANGUP_REQ, callInactiveStopCallBack));
        Assert.assertTrue(callStateHandler.addState(CALL_STOP_DONE_SUCCESS_EVENT, CallState.HANGUP_REQ, CallState.INIT, callStopDoneSuccessCallBack));
        Assert.assertTrue(callStateHandler.addState(CALL_STOP_DONE_FAIL_EVENT, CallState.HANGUP_REQ, CallState.IDLE, callStopDoneFailCallBack));

        Assert.assertFalse(callStateHandler.getEventList().isEmpty());
        Assert.assertFalse(callStateHandler.getStateList().isEmpty());
        ////////////////////////////////////////////////////////////////////////////////

        normalTest(callInfo);

        stateManager.removeStateUnit(callId);
        stateManager.removeStateHandler(CALL_STATE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String callStart (CallInfo callInfo) {
        logger.info("@ Call is started!");
        return callStateHandler.fire(
                CALL_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.INIT
        );
    }

    public String offerEarlyNegoStart(CallInfo callInfo) {
        logger.info("@ Early Nego is started by offer!");
        return callStateHandler.fire(
                OFFER_EARLY_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER
        );
    }

    public String earlyMediaStart (CallInfo callInfo) {
        logger.info("@ Early Media is started!");
        return callStateHandler.fire(
                EARLY_MEDIA_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ
        );
    }

    public String activeStart (CallInfo callInfo) {
        logger.info("@ Active is started!");
        return callStateHandler.fire(
                ACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.NEGO_REQ
        );
    }

    public String earlyNegoInActiveStart (CallInfo callInfo) {
        logger.info("@ InActive is started by early_nego!");
        return callStateHandler.fire(
                EARLY_NEGO_INACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ
        );
    }

    public String negoInActiveStart (CallInfo callInfo) {
        logger.info("@ InActive is started by nego!");
        return callStateHandler.fire(
                NEGO_INACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.NEGO_REQ
        );
    }

    public String offerNegoStart (CallInfo callInfo) {
        logger.info("@ Nego is started by offer!");
        return callStateHandler.fire(
                OFFER_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER
        );
    }

    public String earlyNegoNegoStart (CallInfo callInfo) {
        logger.info("@ Nego is started by early_nego!");
        return callStateHandler.fire(
                EARLY_NEGO_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ
        );
    }

    public String offerHangupStart (CallInfo callInfo) {
        logger.info("@ Hangup is started by offer!");
        return callStateHandler.fire(
                OFFER_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER
        );
    }

    public String earlyNegoHangupStart (CallInfo callInfo) {
        logger.info("@ Hangup is started by early_nego!");
        return callStateHandler.fire(
                EARLY_NEGO_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ
        );
    }

    public String activeHangupStart(CallInfo callInfo) {
        logger.info("@ Hangup is started by nego!");
        return callStateHandler.fire(
                ACTIVE_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.ACTIVE
        );
    }

    public String callStopSuccess (CallInfo callInfo) {
        logger.info("@ Success to stop the call!");
        return callStateHandler.fire(
                CALL_STOP_DONE_SUCCESS_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.HANGUP_REQ
        );
    }

    public String callStopFail (CallInfo callInfo) {
        logger.info("@ Fail to stop the call!");
        return callStateHandler.fire(
                CALL_STOP_DONE_FAIL_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.HANGUP_REQ
        );
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest (CallInfo callInfo) {
        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        this.stopWatch.start();

        // 1) INIT > OFFER
        Assert.assertEquals(CallState.OFFER, callStart(callInfo));
        Assert.assertEquals(CallState.INIT, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.OFFER, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.OFFER, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        // 2) OFFER > EARLY_NEGO_REQ
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, offerEarlyNegoStart(callInfo));
        Assert.assertEquals(CallState.OFFER, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        // 3) EARLY_NEGO_REQ > EARLY_MEDIA
        Assert.assertEquals(CallState.EARLY_MEDIA, earlyMediaStart(callInfo));
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.EARLY_MEDIA, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.EARLY_MEDIA, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        // 4) EARLY_MEDIA > NEGO_REQ
        Assert.assertEquals(CallState.NEGO_REQ, earlyNegoNegoStart(callInfo));
        Assert.assertEquals(CallState.EARLY_MEDIA, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        // 5) NEGO_REQ > ACTIVE
        Assert.assertEquals(CallState.ACTIVE, activeStart(callInfo));
        Assert.assertEquals(CallState.NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.ACTIVE, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.ACTIVE, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        // 6) ACTIVE > HANGUP_REQ
        Assert.assertEquals(CallState.HANGUP_REQ, activeHangupStart(callInfo));
        Assert.assertEquals(CallState.ACTIVE, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.HANGUP_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.HANGUP_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        // 7) HANGUP_REQ > INIT
        Assert.assertEquals(CallState.INIT, callStopSuccess(callInfo));
        Assert.assertEquals(CallState.HANGUP_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.INIT, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.INIT, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        this.stopWatch.stop();
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }

}

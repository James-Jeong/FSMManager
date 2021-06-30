package base.basic.call;

import base.basic.call.base.CallCallBack;
import base.basic.call.base.CallEvent;
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

    ////////////////////////////////////////////////////////////////////////////////

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
        stateManager.addStateHandler(CallState.CALL_STATE_NAME);
        callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        ////////////////////////////////////////////////////////////////////////////////
        // 1. CallBack 함수 정의
        CallCallBack callStartCallBack = new CallCallBack(CallEvent.CALL_START_EVENT);
        CallCallBack callFailCallBack = new CallCallBack(CallEvent.CALL_FAIL_EVENT);
        CallCallBack callOfferEarlyNegoStartCallBack = new CallCallBack(CallEvent.OFFER_EARLY_NEGO_START_EVENT);
        CallCallBack callOfferNegoStartCallBack = new CallCallBack(CallEvent.OFFER_NEGO_START_EVENT);
        CallCallBack callOfferStopCallBack = new CallCallBack(CallEvent.OFFER_STOP_EVENT);
        CallCallBack callEarlyMediaStartCallBack = new CallCallBack(CallEvent.EARLY_MEDIA_START_EVENT);
        CallCallBack callEarlyNegoInactiveStartCallBack = new CallCallBack(CallEvent.EARLY_NEGO_INACTIVE_START_EVENT);
        CallCallBack callEarlyNegoNegoStartCallBack = new CallCallBack(CallEvent.EARLY_NEGO_NEGO_START_EVENT);
        CallCallBack callEarlyNegoStopCallBack = new CallCallBack(CallEvent.EARLY_NEGO_STOP_EVENT);
        CallCallBack callActiveStartCallBack = new CallCallBack(CallEvent.ACTIVE_START_EVENT);
        CallCallBack callNegoInactiveStartCallBack = new CallCallBack(CallEvent.NEGO_INACTIVE_START_EVENT);
        CallCallBack callNegoStopCallBack = new CallCallBack(CallEvent.NEGO_STOP_EVENT);
        CallCallBack callActiveStopCallBack = new CallCallBack(CallEvent.ACTIVE_STOP_EVENT);
        CallCallBack callInactiveStopCallBack = new CallCallBack(CallEvent.INACTIVE_STOP_EVENT);
        CallCallBack callStopDoneSuccessCallBack = new CallCallBack(CallEvent.CALL_STOP_DONE_SUCCESS_EVENT);
        CallCallBack callStopDoneFailCallBack = new CallCallBack(CallEvent.CALL_STOP_DONE_FAIL_EVENT);
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 2. 상태 정의
        Assert.assertTrue(callStateHandler.addState(CallEvent.CALL_START_EVENT, CallState.INIT, CallState.OFFER, callStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.CALL_FAIL_EVENT, CallState.OFFER, CallState.INIT, callFailCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.OFFER_EARLY_NEGO_START_EVENT, CallState.OFFER, CallState.EARLY_NEGO_REQ, callOfferEarlyNegoStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.OFFER_NEGO_START_EVENT, CallState.OFFER, CallState.NEGO_REQ, callOfferNegoStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.OFFER_STOP_EVENT, CallState.OFFER, CallState.HANGUP_REQ, callOfferStopCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.EARLY_MEDIA_START_EVENT, CallState.EARLY_NEGO_REQ, CallState.EARLY_MEDIA, callEarlyMediaStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.EARLY_NEGO_INACTIVE_START_EVENT, CallState.EARLY_NEGO_REQ, CallState.INACTIVE, callEarlyNegoInactiveStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.EARLY_NEGO_NEGO_START_EVENT, CallState.EARLY_MEDIA, CallState.NEGO_REQ, callEarlyNegoNegoStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.EARLY_NEGO_STOP_EVENT, CallState.EARLY_MEDIA, CallState.HANGUP_REQ, callEarlyNegoStopCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.ACTIVE_START_EVENT, CallState.NEGO_REQ, CallState.ACTIVE, callActiveStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.NEGO_INACTIVE_START_EVENT, CallState.NEGO_REQ, CallState.INACTIVE, callNegoInactiveStartCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.NEGO_STOP_EVENT, CallState.NEGO_REQ, CallState.HANGUP_REQ, callNegoStopCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.ACTIVE_STOP_EVENT, CallState.ACTIVE, CallState.HANGUP_REQ, callActiveStopCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.INACTIVE_STOP_EVENT, CallState.INACTIVE, CallState.HANGUP_REQ, callInactiveStopCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.CALL_STOP_DONE_SUCCESS_EVENT, CallState.HANGUP_REQ, CallState.INIT, callStopDoneSuccessCallBack));
        Assert.assertTrue(callStateHandler.addState(CallEvent.CALL_STOP_DONE_FAIL_EVENT, CallState.HANGUP_REQ, CallState.IDLE, callStopDoneFailCallBack));

        Assert.assertFalse(callStateHandler.getEventList().isEmpty());
        Assert.assertFalse(callStateHandler.getStateList().isEmpty());
        ////////////////////////////////////////////////////////////////////////////////

        normalTest(callInfo);

        stateManager.removeStateUnit(callId);
        stateManager.removeStateHandler(CallState.CALL_STATE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String callStart (CallInfo callInfo) {
        logger.debug("@ Call is started!");
        return callStateHandler.fire(
                CallEvent.CALL_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.INIT,
                CallState.OFFER
        );
    }

    public String offerEarlyNegoStart(CallInfo callInfo) {
        logger.debug("@ Early Nego is started by offer!");
        return callStateHandler.fire(
                CallEvent.OFFER_EARLY_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER,
                CallState.EARLY_NEGO_REQ
        );
    }

    public String earlyMediaStart (CallInfo callInfo) {
        logger.debug("@ Early Media is started!");
        return callStateHandler.fire(
                CallEvent.EARLY_MEDIA_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ,
                CallState.EARLY_MEDIA
        );
    }

    public String activeStart (CallInfo callInfo) {
        logger.debug("@ Active is started!");
        return callStateHandler.fire(
                CallEvent.ACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.NEGO_REQ,
                CallState.ACTIVE
        );
    }

    public String earlyNegoInActiveStart (CallInfo callInfo) {
        logger.debug("@ InActive is started by early_nego!");
        return callStateHandler.fire(
                CallEvent.EARLY_NEGO_INACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ,
                CallState.INACTIVE
        );
    }

    public String negoInActiveStart (CallInfo callInfo) {
        logger.debug("@ InActive is started by nego!");
        return callStateHandler.fire(
                CallEvent.NEGO_INACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.NEGO_REQ,
                CallState.INACTIVE
        );
    }

    public String offerNegoStart (CallInfo callInfo) {
        logger.debug("@ Nego is started by offer!");
        return callStateHandler.fire(
                CallEvent.OFFER_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER,
                CallState.NEGO_REQ
        );
    }

    public String earlyNegoNegoStart (CallInfo callInfo) {
        logger.debug("@ Nego is started by early_nego!");
        return callStateHandler.fire(
                CallEvent.EARLY_NEGO_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ,
                CallState.NEGO_REQ
        );
    }

    public String offerHangupStart (CallInfo callInfo) {
        logger.debug("@ Hangup is started by offer!");
        return callStateHandler.fire(
                CallEvent.OFFER_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER,
                CallState.HANGUP_REQ
        );
    }

    public String earlyNegoHangupStart (CallInfo callInfo) {
        logger.debug("@ Hangup is started by early_nego!");
        return callStateHandler.fire(
                CallEvent.EARLY_NEGO_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ,
                CallState.HANGUP_REQ
        );
    }

    public String activeHangupStart(CallInfo callInfo) {
        logger.debug("@ Hangup is started by nego!");
        return callStateHandler.fire(
                CallEvent.ACTIVE_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.ACTIVE,
                CallState.HANGUP_REQ
        );
    }

    public String callStopSuccess (CallInfo callInfo) {
        logger.debug("@ Success to stop the call!");
        return callStateHandler.fire(
                CallEvent.CALL_STOP_DONE_SUCCESS_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.HANGUP_REQ,
                CallState.INIT
        );
    }

    public String callStopFail (CallInfo callInfo) {
        logger.debug("@ Fail to stop the call!");
        return callStateHandler.fire(
                CallEvent.CALL_STOP_DONE_FAIL_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.HANGUP_REQ,
                CallState.ACTIVE
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
        logger.debug("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }

}

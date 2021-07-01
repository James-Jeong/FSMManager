package base.basic.call;

import base.basic.base.SessionManager;
import base.basic.call.base.CallEvent;
import base.basic.call.base.CallInfo;
import base.basic.call.base.CallState;
import base.basic.media.base.MediaState;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.module.StateHandler;

/**
 * @class public class AmfCallStateTest
 * @brief Amf Call State Test class
 */
public class BasicCallStateTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicCallStateTest.class);

    private final StopWatch stopWatch = new StopWatch();

    private final StateManager stateManager = StateManager.getInstance();

    ////////////////////////////////////////////////////////////////////////////////

    public void testStart () {
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.createCall("Call", "01012345678", "01056781234");
        CallInfo callInfo = sessionManager.getCall("Call");

        normalTest(callInfo);

        sessionManager.removeCall("Call");
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String callStart (CallInfo callInfo) {
        logger.debug("@ Call is started!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.CALL_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.INIT,
                CallState.OFFER
        );
    }

    public String offerEarlyNegoStart(CallInfo callInfo) {
        logger.debug("@ Early Nego is started by offer!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.OFFER_EARLY_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER,
                callInfo
        );
    }

    public String earlyMediaStart (CallInfo callInfo) {
        logger.debug("@ Early Media is started!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.EARLY_MEDIA_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ,
                callInfo
        );
    }

    public String activeStart (CallInfo callInfo) {
        logger.debug("@ Active is started!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.ACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.NEGO_REQ,
                callInfo
        );
    }

    public String earlyNegoInActiveStart (CallInfo callInfo) {
        logger.debug("@ InActive is started by early_nego!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.EARLY_NEGO_INACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ,
                callInfo
        );
    }

    public String negoInActiveStart (CallInfo callInfo) {
        logger.debug("@ InActive is started by nego!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.NEGO_INACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.NEGO_REQ,
                callInfo
        );
    }

    public String offerNegoStart (CallInfo callInfo) {
        logger.debug("@ Nego is started by offer!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.OFFER_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER,
                callInfo
        );
    }

    public String earlyMediaNegoStart(CallInfo callInfo) {
        logger.debug("@ Nego is started by early_media!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.EARLY_MEDIA_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_MEDIA,
                CallState.NEGO_REQ
        );
    }

    public String offerHangupStart (CallInfo callInfo) {
        logger.debug("@ Hangup is started by offer!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.OFFER_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER,
                CallState.HANGUP_REQ
        );
    }

    public String earlyNegoHangupStart (CallInfo callInfo) {
        logger.debug("@ Hangup is started by early_nego!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.EARLY_MEDIA_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.EARLY_NEGO_REQ,
                callInfo
        );
    }

    public String activeHangupStart(CallInfo callInfo) {
        logger.debug("@ Hangup is started by nego!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.ACTIVE_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.ACTIVE,
                callInfo
        );
    }

    public String callStopSuccess (CallInfo callInfo) {
        logger.debug("@ Success to stop the call!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.CALL_STOP_DONE_SUCCESS_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.HANGUP_REQ,
                callInfo
        );
    }

    public String callStopFail (CallInfo callInfo) {
        logger.debug("@ Fail to stop the call!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.CALL_STOP_DONE_FAIL_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.HANGUP_REQ,
                callInfo
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
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

        // 3) EARLY_NEGO_REQ > EARLY_MEDIA
        Assert.assertEquals(CallState.EARLY_MEDIA, earlyMediaStart(callInfo));
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.EARLY_MEDIA, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

        // 4) EARLY_MEDIA > NEGO_REQ
        Assert.assertEquals(CallState.NEGO_REQ, earlyMediaNegoStart(callInfo));
        Assert.assertEquals(CallState.EARLY_MEDIA, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        // 5) NEGO_REQ > ACTIVE
        Assert.assertEquals(CallState.ACTIVE, activeStart(callInfo));
        Assert.assertEquals(CallState.NEGO_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.ACTIVE, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        // 6) ACTIVE > HANGUP_REQ
        Assert.assertEquals(CallState.HANGUP_REQ, activeHangupStart(callInfo));
        Assert.assertEquals(CallState.ACTIVE, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.HANGUP_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

        // 7) HANGUP_REQ > INIT
        Assert.assertEquals(CallState.INIT, callStopSuccess(callInfo));
        Assert.assertEquals(CallState.HANGUP_REQ, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.INIT, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

        this.stopWatch.stop();
        logger.debug("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }
}

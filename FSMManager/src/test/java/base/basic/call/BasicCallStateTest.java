package base.basic.call;

import base.basic.base.SessionManager;
import base.basic.call.base.CallEvent;
import base.basic.call.base.CallInfo;
import base.basic.call.base.CallState;
import base.basic.media.base.MediaState;
import ch.qos.logback.core.util.TimeUtil;
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

        sessionManager.createCall("Call1", "01012345678", "01056781234");
        CallInfo callInfo1 = sessionManager.getCall("Call1");
        normalTest(callInfo1);

        sessionManager.createCall("Call2", "01056781234", "01012345678");
        CallInfo callInfo2 = sessionManager.getCall("Call2");
        failTest(callInfo2);

        sessionManager.removeCall("Call1");
        sessionManager.removeCall("Call2");
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String callStart (CallInfo callInfo) {
        logger.info("@ Call is started!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.CALL_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.OFFER
        );
    }

    public String offerEarlyNegoStart(CallInfo callInfo) {
        logger.info("@ Early Nego is started by offer!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.OFFER_EARLY_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String earlyMediaStart (CallInfo callInfo) {
        logger.info("@ Early Media is started!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.EARLY_MEDIA_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String activeStart (CallInfo callInfo) {
        logger.info("@ Active is started!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.ACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String earlyNegoInActiveStart (CallInfo callInfo) {
        logger.info("@ InActive is started by early_nego!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.EARLY_NEGO_INACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String negoInActiveStart (CallInfo callInfo) {
        logger.info("@ InActive is started by nego!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.NEGO_INACTIVE_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String offerNegoStart (CallInfo callInfo) {
        logger.info("@ Nego is started by offer!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.OFFER_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String earlyMediaNegoStart(CallInfo callInfo) {
        logger.info("@ Nego is started by early_media!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.EARLY_MEDIA_NEGO_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.NEGO_REQ
        );
    }

    public String offerHangupStart (CallInfo callInfo) {
        logger.info("@ Hangup is started by offer!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.OFFER_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                CallState.HANGUP_REQ
        );
    }

    public String earlyNegoHangupStart (CallInfo callInfo) {
        logger.info("@ Hangup is started by early_nego!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.EARLY_MEDIA_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String activeHangupStart(CallInfo callInfo) {
        logger.info("@ Hangup is started by nego!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.ACTIVE_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String callStopSuccess (CallInfo callInfo) {
        logger.info("@ Success to stop the call!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.CALL_STOP_DONE_SUCCESS_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
                callInfo
        );
    }

    public String callStopFail (CallInfo callInfo) {
        logger.info("@ Fail to stop the call!");
        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);

        return callStateHandler.fire(
                CallEvent.CALL_STOP_DONE_FAIL_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()),
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
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }

    public void failTest (CallInfo callInfo) {
        Assert.assertEquals(CallState.OFFER, callStart(callInfo));
        Assert.assertEquals(CallState.INIT, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.OFFER, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
        Assert.assertEquals(CallState.OFFER, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCallBackResult());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            logger.warn("() () () Thread.sleep.Exception", e);
        }

        Assert.assertEquals(CallState.OFFER, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getPrevState());
        Assert.assertEquals(CallState.INIT, StateManager.getInstance().getStateUnit(callInfo.getSipStateUnitName()).getCurState());
    }

}

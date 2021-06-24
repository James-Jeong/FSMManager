package base.basic;

import base.basic.call.base.CallCallBack;
import base.basic.media.base.MediaCallBack;
import base.squirrel.call.base.CallState;
import base.squirrel.media.base.MediaState;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.StateHandler;

/**
 * @class
 * @brief
 */
public class BasicCallAndMediaStateTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicCallAndMediaStateTest.class);

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

    private static final String MEDIA_START_EVENT = "media_start_success";
    private static final String MEDIA_STOP_EVENT = "media_stop_success";
    private static final String MEDIA_CREATE_SUCCESS_EVENT = "media_create_success";
    private static final String MEDIA_CREATE_FAIL_EVENT = "media_create_fail";
    private static final String MEDIA_DELETE_SUCCESS_EVENT = "media_delete_success";
    private static final String MEDIA_DELETE_FAIL_EVENT = "media_delete_fail";

    ////////////////////////////////////////////////////////////////////////////////

    private static final String CALL_STATE_NAME = "call_state";
    private final StateManager stateManager = StateManager.getInstance();
    private StateHandler callStateHandler = null;

    private static final String MEDIA_STATE_NAME = "media_state";
    private StateHandler mediaStateHandler = null;

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        stateManager.addStateHandler(CALL_STATE_NAME, CallState.INIT);
        callStateHandler = stateManager.getStateHandler(CALL_STATE_NAME);

        stateManager.addStateHandler(MEDIA_STATE_NAME, MediaState.IDLE_STATE);
        mediaStateHandler = stateManager.getStateHandler(MEDIA_STATE_NAME);

        normalTest();

        stateManager.removeStateHandler(CALL_STATE_NAME);
        stateManager.removeStateHandler(MEDIA_STATE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String callStart () {
        logger.info("@ Call is started!");
        return callStateHandler.fire(CALL_START_EVENT, CallState.INIT);
    }

    public String offerEarlyNegoStart() {
        logger.info("@ Early Nego is started by offer!");

        Assert.assertEquals(MediaState.ACTIVE_REQUEST, mediaStart());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, mediaStateHandler.getCurState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, mediaStateHandler.getCallBackResultByState(MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST));

        return callStateHandler.fire(OFFER_EARLY_NEGO_START_EVENT, CallState.OFFER);
    }

    public String earlyMediaStart () {
        logger.info("@ Early Media is started!");

        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaCreateSuccess());
        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaStateHandler.getCurState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaStateHandler.getCallBackResultByState(MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE));

        return callStateHandler.fire(EARLY_MEDIA_START_EVENT, CallState.EARLY_NEGO_REQ);
    }

    public String activeStart () {
        logger.info("@ Active is started!");
        return callStateHandler.fire(ACTIVE_START_EVENT, CallState.NEGO_REQ);
    }

    public String earlyNegoInActiveStart () {
        logger.info("@ InActive is started by early_nego!");
        return callStateHandler.fire(EARLY_NEGO_INACTIVE_START_EVENT, CallState.EARLY_NEGO_REQ);
    }

    public String negoInActiveStart () {
        logger.info("@ InActive is started by nego!");
        return callStateHandler.fire(NEGO_INACTIVE_START_EVENT, CallState.NEGO_REQ);
    }

    public String offerNegoStart () {
        logger.info("@ Nego is started by offer!");
        return callStateHandler.fire(OFFER_NEGO_START_EVENT, CallState.OFFER);
    }

    public String earlyNegoNegoStart () {
        logger.info("@ Nego is started by early_nego!");
        return callStateHandler.fire(EARLY_NEGO_NEGO_START_EVENT, CallState.EARLY_NEGO_REQ);
    }

    public String offerHangupStart () {
        logger.info("@ Hangup is started by offer!");
        return callStateHandler.fire(OFFER_STOP_EVENT, CallState.OFFER);
    }

    public String earlyNegoHangupStart () {
        logger.info("@ Hangup is started by early_nego!");
        return callStateHandler.fire(EARLY_NEGO_STOP_EVENT, CallState.EARLY_NEGO_REQ);
    }

    public String activeHangupStart() {
        logger.info("@ Hangup is started by nego!");

        Assert.assertEquals(MediaState.IDLE_REQUEST, mediaStop());
        Assert.assertEquals(MediaState.IDLE_REQUEST, mediaStateHandler.getCurState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, mediaStateHandler.getCallBackResultByState(MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST));

        return callStateHandler.fire(ACTIVE_STOP_EVENT, CallState.ACTIVE);
    }

    public String callStopSuccess () {
        logger.info("@ Success to stop the call!");

        Assert.assertEquals(MediaState.IDLE_STATE, mediaDeleteSuccess());
        Assert.assertEquals(MediaState.IDLE_STATE, mediaStateHandler.getCurState());
        Assert.assertEquals(MediaState.IDLE_STATE, mediaStateHandler.getCallBackResultByState(MediaState.IDLE_REQUEST, MediaState.IDLE_STATE));

        return callStateHandler.fire(CALL_STOP_DONE_SUCCESS_EVENT, CallState.HANGUP_REQ);
    }

    public String callStopFail () {
        logger.info("@ Fail to stop the call!");
        return callStateHandler.fire(CALL_STOP_DONE_FAIL_EVENT, CallState.HANGUP_REQ);
    }

    public String mediaStart () {
        //logger.info("@ Media is started!");
        return mediaStateHandler.fire(MEDIA_START_EVENT, MediaState.IDLE_STATE);
    }

    public String mediaStop () {
        //logger.info("@ Media is stopped!");
        return mediaStateHandler.fire(MEDIA_STOP_EVENT, MediaState.ACTIVE_STATE);
    }

    public String mediaCreateSuccess () {
        //logger.info("@ Success to create media!");
        return mediaStateHandler.fire(MEDIA_CREATE_SUCCESS_EVENT, MediaState.ACTIVE_REQUEST);
    }

    public String mediaCreateFail () {
        //logger.info("@ Fail to create media!");
        return mediaStateHandler.fire(MEDIA_CREATE_FAIL_EVENT, MediaState.ACTIVE_REQUEST);
    }

    public String mediaDeleteSuccess () {
        //logger.info("@ Success to delete media!");
        return mediaStateHandler.fire(MEDIA_DELETE_SUCCESS_EVENT, MediaState.IDLE_REQUEST);
    }

    public String mediaDeleteFail () {
        //logger.info("@ Fail to delete media!");
        return mediaStateHandler.fire(MEDIA_DELETE_FAIL_EVENT, MediaState.IDLE_REQUEST);
    }


    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest () {
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

        MediaCallBack mediaStartCallBack = new MediaCallBack(MEDIA_START_EVENT);
        MediaCallBack mediaCreateSuccessCallBack = new MediaCallBack(MEDIA_CREATE_SUCCESS_EVENT);
        MediaCallBack mediaCreateFailCallBack = new MediaCallBack(MEDIA_CREATE_FAIL_EVENT);
        MediaCallBack mediaStopCallBack = new MediaCallBack(MEDIA_STOP_EVENT);
        MediaCallBack mediaDeleteSuccessCallBack = new MediaCallBack(MEDIA_DELETE_SUCCESS_EVENT);
        MediaCallBack mediaDeleteFailCallBack = new MediaCallBack(MEDIA_DELETE_FAIL_EVENT);
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

        Assert.assertTrue(mediaStateHandler.addState(MEDIA_START_EVENT, MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST, mediaStartCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_CREATE_SUCCESS_EVENT, MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE, mediaCreateSuccessCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_CREATE_FAIL_EVENT, MediaState.ACTIVE_REQUEST, MediaState.IDLE_STATE, mediaCreateFailCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_STOP_EVENT, MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST, mediaStopCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_DELETE_SUCCESS_EVENT, MediaState.IDLE_REQUEST, MediaState.IDLE_STATE, mediaDeleteSuccessCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_DELETE_FAIL_EVENT, MediaState.IDLE_REQUEST, MediaState.ACTIVE_STATE, mediaDeleteFailCallBack));

        Assert.assertFalse(mediaStateHandler.getStateList().isEmpty());
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        this.stopWatch.start();

        Assert.assertEquals(CallState.OFFER, callStart());
        Assert.assertEquals(CallState.OFFER, callStateHandler.getCurState());
        Assert.assertEquals(CallState.OFFER, callStateHandler.getCallBackResultByState(CallState.INIT, CallState.OFFER));

        Assert.assertEquals(CallState.EARLY_NEGO_REQ, offerEarlyNegoStart());
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, callStateHandler.getCurState());
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, callStateHandler.getCallBackResultByState(CallState.OFFER, CallState.EARLY_NEGO_REQ));
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, mediaStateHandler.getCurState());

        Assert.assertEquals(CallState.EARLY_MEDIA, earlyMediaStart());
        Assert.assertEquals(CallState.EARLY_MEDIA, callStateHandler.getCurState());
        Assert.assertEquals(CallState.EARLY_MEDIA, callStateHandler.getCallBackResultByState(CallState.EARLY_NEGO_REQ, CallState.EARLY_MEDIA));
        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaStateHandler.getCurState());

        Assert.assertEquals(CallState.NEGO_REQ, earlyNegoNegoStart());
        Assert.assertEquals(CallState.NEGO_REQ, callStateHandler.getCurState());
        Assert.assertEquals(CallState.NEGO_REQ, callStateHandler.getCallBackResultByState(CallState.EARLY_MEDIA, CallState.NEGO_REQ));

        Assert.assertEquals(CallState.ACTIVE, activeStart());
        Assert.assertEquals(CallState.ACTIVE, callStateHandler.getCurState());
        Assert.assertEquals(CallState.ACTIVE, callStateHandler.getCallBackResultByState(CallState.NEGO_REQ, CallState.ACTIVE));

        Assert.assertEquals(CallState.HANGUP_REQ, activeHangupStart());
        Assert.assertEquals(CallState.HANGUP_REQ, callStateHandler.getCurState());
        Assert.assertEquals(CallState.HANGUP_REQ, callStateHandler.getCallBackResultByState(CallState.ACTIVE, CallState.HANGUP_REQ));
        Assert.assertEquals(MediaState.IDLE_REQUEST, mediaStateHandler.getCurState());

        Assert.assertEquals(CallState.INIT, callStopSuccess());
        Assert.assertEquals(CallState.INIT, callStateHandler.getCurState());
        Assert.assertEquals(CallState.INIT, callStateHandler.getCallBackResultByState(CallState.HANGUP_REQ, CallState.INIT));
        Assert.assertEquals(MediaState.IDLE_STATE, mediaStateHandler.getCurState());

        this.stopWatch.stop();
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }
}

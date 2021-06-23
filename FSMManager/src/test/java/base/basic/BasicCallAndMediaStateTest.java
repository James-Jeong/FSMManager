package base.basic;

import base.squirrel.call.base.CallState;
import base.squirrel.media.base.MediaState;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.squirrel.CallBack;
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

    public void callStart () {
        logger.info("@ Call is started!");
        callStateHandler.fire(CALL_START_EVENT);

        if (callStateHandler.getCurState() == null) {
            logger.info("@ Call is failed!");
            callStateHandler.fire(CALL_FAIL_EVENT);
        }
    }

    public void earlyNegoStart () {
        logger.info("@ Early Nego is started by offer!");
        callStateHandler.fire(OFFER_EARLY_NEGO_START_EVENT);
        mediaStart();
    }

    public void earlyMediaStart () {
        logger.info("@ Early Media is started!");
        callStateHandler.fire(EARLY_MEDIA_START_EVENT);
        mediaCreateSuccess();
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
        mediaStop();
    }

    public void earlyNegoHangupStart () {
        logger.info("@ Hangup is started by early_nego!");
        callStateHandler.fire(EARLY_NEGO_STOP_EVENT);
        mediaStop();
    }

    public void activeHangupStart() {
        logger.info("@ Hangup is started by nego!");
        callStateHandler.fire(ACTIVE_STOP_EVENT);
        mediaStop();
    }

    public void callStopSuccess () {
        logger.info("@ Success to stop the call!");
        callStateHandler.fire(CALL_STOP_DONE_SUCCESS_EVENT);
        mediaDeleteSuccess();
    }

    public void callStopFail () {
        logger.info("@ Fail to stop the call!");
        callStateHandler.fire(CALL_STOP_DONE_FAIL_EVENT);
    }

    public void mediaStart () {
        logger.info("@ Media is started!");
        mediaStateHandler.fire(MEDIA_START_EVENT);
    }

    public void mediaStop () {
        logger.info("@ Media is stopped!");
        mediaStateHandler.fire(MEDIA_STOP_EVENT);
    }

    public void mediaCreateSuccess () {
        logger.info("@ Success to create media!");
        mediaStateHandler.fire(MEDIA_CREATE_SUCCESS_EVENT);
    }

    public void mediaCreateFail () {
        logger.info("@ Fail to create media!");
        mediaStateHandler.fire(MEDIA_CREATE_FAIL_EVENT);
    }

    public void mediaDeleteSuccess () {
        logger.info("@ Success to delete media!");
        mediaStateHandler.fire(MEDIA_DELETE_SUCCESS_EVENT);
    }

    public void mediaDeleteFail () {
        logger.info("@ Fail to delete media!");
        mediaStateHandler.fire(MEDIA_DELETE_FAIL_EVENT);
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

        Assert.assertTrue(mediaStateHandler.addState(MEDIA_START_EVENT, MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST, callBack));

        Assert.assertTrue(mediaStateHandler.addState(MEDIA_CREATE_SUCCESS_EVENT, MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE, callBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_CREATE_FAIL_EVENT, MediaState.ACTIVE_REQUEST, MediaState.IDLE_STATE, callBack));

        Assert.assertTrue(mediaStateHandler.addState(MEDIA_STOP_EVENT, MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST, callBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_DELETE_SUCCESS_EVENT, MediaState.IDLE_REQUEST, MediaState.IDLE_STATE, callBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_DELETE_FAIL_EVENT, MediaState.IDLE_REQUEST, MediaState.ACTIVE_STATE, callBack));

        Assert.assertNotNull(mediaStateHandler.getStateList());
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        this.stopWatch.start();

        callStart();
        Assert.assertEquals(CallState.OFFER, callStateHandler.getCurState());

        earlyNegoStart();
        Assert.assertEquals(CallState.EARLY_NEGO_REQ, callStateHandler.getCurState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, mediaStateHandler.getCurState());

        earlyMediaStart();
        Assert.assertEquals(CallState.EARLY_MEDIA, callStateHandler.getCurState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaStateHandler.getCurState());

        earlyNegoNegoStart();
        Assert.assertEquals(CallState.NEGO_REQ, callStateHandler.getCurState());

        activeStart();
        Assert.assertEquals(CallState.ACTIVE, callStateHandler.getCurState());

        activeHangupStart();
        Assert.assertEquals(CallState.HANGUP_REQ, callStateHandler.getCurState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, mediaStateHandler.getCurState());

        callStopSuccess();
        Assert.assertEquals(CallState.INIT, callStateHandler.getCurState());
        Assert.assertEquals(MediaState.IDLE_STATE, mediaStateHandler.getCurState());

        this.stopWatch.stop();
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }
}

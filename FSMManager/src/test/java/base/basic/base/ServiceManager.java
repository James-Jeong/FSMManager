package base.basic.base;

import base.basic.call.base.CallEvent;
import base.basic.call.base.CallState;
import base.basic.call.base.callback.*;
import base.basic.media.base.MediaCallBack;
import base.basic.media.base.MediaEvent;
import base.basic.media.base.MediaState;
import org.junit.Assert;
import state.StateManager;
import state.basic.module.StateHandler;

/**
 * @class public class ServiceManager
 * @brief ServiceManager class
 */
public class ServiceManager {

    private static ServiceManager serviceManager;

    public ServiceManager() {
        // Nothing
    }

    public static ServiceManager getInstance() {
        if (serviceManager == null) {
            serviceManager = new ServiceManager();
        }

        return  serviceManager;
    }

    public void start () {
        StateManager stateManager = StateManager.getInstance();

        stateManager.addStateHandler(CallState.CALL_STATE_NAME);
        stateManager.addStateHandler(MediaState.MEDIA_STATE_NAME);

        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);
        ////////////////////////////////////////////////////////////////////////////////
        // 1. CallBack 함수 정의
        CommonCallBack callStartCallBack = new CommonCallBack(CallEvent.CALL_START_EVENT);
        CommonCallBack callFailCallBack = new CommonCallBack(CallEvent.CALL_FAIL_EVENT);

        OfferEarlyNegoCallBack offerEarlyNegoCallBack = new OfferEarlyNegoCallBack(CallEvent.OFFER_EARLY_NEGO_START_EVENT);
        OfferNegoCallBack offerNegoCallBack = new OfferNegoCallBack(CallEvent.OFFER_NEGO_START_EVENT);
        CommonCallBack callOfferStopCallBack = new CommonCallBack(CallEvent.OFFER_STOP_EVENT);

        EarlyMediaStartCallBack earlyMediaStartCallBack = new EarlyMediaStartCallBack(CallEvent.EARLY_MEDIA_START_EVENT);
        EarlyNegoInactiveStartCallBack earlyNegoInactiveStartCallBack = new EarlyNegoInactiveStartCallBack(CallEvent.EARLY_NEGO_INACTIVE_START_EVENT);

        CommonCallBack callEarlyNegoNegoStartCallBack = new CommonCallBack(CallEvent.EARLY_MEDIA_NEGO_START_EVENT);
        EarlyMediaStopCallBack earlyMediaStopCallBack = new EarlyMediaStopCallBack(CallEvent.EARLY_MEDIA_STOP_EVENT);

        ActiveStartCallBack activeStartCallBack = new ActiveStartCallBack(CallEvent.ACTIVE_START_EVENT);
        NegoInactiveStartCallBack negoInactiveStartCallBack = new NegoInactiveStartCallBack(CallEvent.NEGO_INACTIVE_START_EVENT);
        ActiveStopCallBack activeStopCallBack = new ActiveStopCallBack(CallEvent.ACTIVE_STOP_EVENT);

        CommonCallBack callInactiveStopCallBack = new CommonCallBack(CallEvent.INACTIVE_STOP_EVENT);
        CallStopDoneSuccessCallBack callStopDoneSuccessCallBack = new CallStopDoneSuccessCallBack(CallEvent.CALL_STOP_DONE_SUCCESS_EVENT);
        CallStopDoneFailCallBack callStopDoneFailCallBack = new CallStopDoneFailCallBack(CallEvent.CALL_STOP_DONE_FAIL_EVENT);

        MediaCallBack mediaStartCallBack = new MediaCallBack(MediaEvent.MEDIA_START_EVENT);
        MediaCallBack mediaCreateSuccessCallBack = new MediaCallBack(MediaEvent.MEDIA_CREATE_SUCCESS_EVENT);
        MediaCallBack mediaCreateFailCallBack = new MediaCallBack(MediaEvent.MEDIA_CREATE_FAIL_EVENT);
        MediaCallBack mediaStopCallBack = new MediaCallBack(MediaEvent.MEDIA_STOP_EVENT);
        MediaCallBack mediaDeleteSuccessCallBack = new MediaCallBack(MediaEvent.MEDIA_DELETE_SUCCESS_EVENT);
        MediaCallBack mediaDeleteFailCallBack = new MediaCallBack(MediaEvent.MEDIA_DELETE_FAIL_EVENT);
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 2. 상태 정의
        Assert.assertTrue(callStateHandler.addState(CallEvent.CALL_START_EVENT, CallState.INIT, CallState.OFFER, callStartCallBack, CallEvent.CALL_FAIL_EVENT,  2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.CALL_FAIL_EVENT, CallState.OFFER, CallState.INIT, callFailCallBack, null, 0));
        Assert.assertTrue(callStateHandler.addState(CallEvent.OFFER_EARLY_NEGO_START_EVENT, CallState.OFFER, CallState.EARLY_NEGO_REQ, offerEarlyNegoCallBack, CallEvent.EARLY_NEGO_INACTIVE_START_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.OFFER_NEGO_START_EVENT, CallState.OFFER, CallState.NEGO_REQ, offerNegoCallBack, CallEvent.NEGO_INACTIVE_START_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.OFFER_STOP_EVENT, CallState.OFFER, CallState.HANGUP_REQ, callOfferStopCallBack, null, 0));
        Assert.assertTrue(callStateHandler.addState(CallEvent.EARLY_MEDIA_START_EVENT, CallState.EARLY_NEGO_REQ, CallState.EARLY_MEDIA, earlyMediaStartCallBack, CallEvent.EARLY_MEDIA_STOP_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.EARLY_NEGO_INACTIVE_START_EVENT, CallState.EARLY_NEGO_REQ, CallState.INACTIVE, earlyNegoInactiveStartCallBack, null, 0));
        Assert.assertTrue(callStateHandler.addState(CallEvent.EARLY_MEDIA_NEGO_START_EVENT, CallState.EARLY_MEDIA, CallState.NEGO_REQ, callEarlyNegoNegoStartCallBack, CallEvent.NEGO_INACTIVE_START_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.EARLY_MEDIA_STOP_EVENT, CallState.EARLY_MEDIA, CallState.HANGUP_REQ, earlyMediaStopCallBack, CallEvent.CALL_STOP_DONE_FAIL_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.ACTIVE_START_EVENT, CallState.NEGO_REQ, CallState.ACTIVE, activeStartCallBack, CallEvent.ACTIVE_STOP_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.NEGO_INACTIVE_START_EVENT, CallState.NEGO_REQ, CallState.INACTIVE, negoInactiveStartCallBack, CallEvent.INACTIVE_STOP_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.ACTIVE_STOP_EVENT, CallState.ACTIVE, CallState.HANGUP_REQ, activeStopCallBack, CallEvent.CALL_STOP_DONE_FAIL_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.INACTIVE_STOP_EVENT, CallState.INACTIVE, CallState.HANGUP_REQ, callInactiveStopCallBack, CallEvent.CALL_STOP_DONE_FAIL_EVENT, 2000));
        Assert.assertTrue(callStateHandler.addState(CallEvent.CALL_STOP_DONE_SUCCESS_EVENT, CallState.HANGUP_REQ, CallState.INIT, callStopDoneSuccessCallBack, null, 0));
        Assert.assertTrue(callStateHandler.addState(CallEvent.CALL_STOP_DONE_FAIL_EVENT, CallState.HANGUP_REQ, CallState.IDLE, callStopDoneFailCallBack, null, 0));

        Assert.assertTrue(mediaStateHandler.addState(MediaEvent.MEDIA_START_EVENT, MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST, mediaStartCallBack, MediaEvent.MEDIA_CREATE_FAIL_EVENT, 2000));
        Assert.assertTrue(mediaStateHandler.addState(MediaEvent.MEDIA_CREATE_SUCCESS_EVENT, MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE, mediaCreateSuccessCallBack, MediaEvent.MEDIA_STOP_EVENT, 2000));
        Assert.assertTrue(mediaStateHandler.addState(MediaEvent.MEDIA_CREATE_FAIL_EVENT, MediaState.ACTIVE_REQUEST, MediaState.IDLE_STATE, mediaCreateFailCallBack, null, 0));
        Assert.assertTrue(mediaStateHandler.addState(MediaEvent.MEDIA_STOP_EVENT, MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST, mediaStopCallBack, MediaEvent.MEDIA_DELETE_FAIL_EVENT, 2000));
        Assert.assertTrue(mediaStateHandler.addState(MediaEvent.MEDIA_DELETE_SUCCESS_EVENT, MediaState.IDLE_REQUEST, MediaState.IDLE_STATE, mediaDeleteSuccessCallBack, null, 0));
        Assert.assertTrue(mediaStateHandler.addState(MediaEvent.MEDIA_DELETE_FAIL_EVENT, MediaState.IDLE_REQUEST, MediaState.ACTIVE_STATE, mediaDeleteFailCallBack, null, 0));

        Assert.assertFalse(mediaStateHandler.getStateList().isEmpty());
    }

    public void stop () {
        StateManager stateManager = StateManager.getInstance();
        stateManager.removeStateHandler(CallState.CALL_STATE_NAME);
        stateManager.removeStateHandler(MediaState.MEDIA_STATE_NAME);
    }

}

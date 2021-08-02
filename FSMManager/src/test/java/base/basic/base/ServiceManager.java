package base.basic.base;

import base.basic.call.base.CallEvent;
import base.basic.call.base.CallState;
import base.basic.call.base.callback.*;
import base.basic.call.base.condition.CallStopDoneSuccessEventCondition;
import base.basic.call.base.condition.InactiveStopEventCondition;
import base.basic.call.base.condition.MediaDeleteSuccessEventCondition;
import base.basic.media.base.MediaCallBack;
import base.basic.media.base.MediaEvent;
import base.basic.media.base.MediaState;
import org.junit.Assert;
import state.StateManager;
import state.basic.module.StateHandler;
import state.basic.module.StateTaskManager;

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
        stateManager.start(1000);

        stateManager.addStateHandler(CallState.CALL_STATE_NAME);
        stateManager.addStateHandler(MediaState.MEDIA_STATE_NAME);

        StateHandler callStateHandler = stateManager.getStateHandler(CallState.CALL_STATE_NAME);
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);
        ////////////////////////////////////////////////////////////////////////////////
        // 1. CallBack 함수 정의
        FailCallBack failCallBack = new FailCallBack("FailCallBack");

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

        // 2-1) CallState
        // 2-1-1) CallStartEvent : CallState.INIT > CallState.OFFER
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.CALL_START_EVENT,
                        CallState.INIT, CallState.OFFER,
                        callStartCallBack,
                        failCallBack,
                        CallEvent.CALL_FAIL_EVENT,  1000, 3, CallState.INIT
                )
        );

        // 2-1-2) CallFailEvent : CallState.OFFER > CallState.INIT
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.CALL_FAIL_EVENT,
                        CallState.OFFER, CallState.INIT,
                        callFailCallBack,
                        null,
                        null, 0, 0
                )
        );

        // 2-1-3) OfferEarlyNegoStartEvent : CallState.OFFER > CallState.EARLY_NEGO_REQ
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.OFFER_EARLY_NEGO_START_EVENT,
                        CallState.OFFER, CallState.EARLY_NEGO_REQ,
                        offerEarlyNegoCallBack,
                        null,
                        CallEvent.EARLY_NEGO_INACTIVE_START_EVENT, 1000, 0, CallState.OFFER
                )
        );

        // 2-1-4) OfferNegoStartEvent : CallState.OFFER > CallState.NEGO_REQ
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.OFFER_NEGO_START_EVENT,
                        CallState.OFFER, CallState.NEGO_REQ,
                        offerNegoCallBack,
                        null,
                        CallEvent.NEGO_INACTIVE_START_EVENT,1000, 0, CallState.OFFER
                )
        );

        // 2-1-5) OfferStopEvent : CallState.OFFER > CallState.HANGUP_REQ
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.OFFER_STOP_EVENT,
                        CallState.OFFER, CallState.HANGUP_REQ,
                        callOfferStopCallBack,
                        null,
                        null, 0, 0
                )
        );

        // 2-1-6) EarlyMediaStartEvent : CallState.EARLY_NEGO_REQ > CallState.EARLY_MEDIA
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.EARLY_MEDIA_START_EVENT,
                        CallState.EARLY_NEGO_REQ, CallState.EARLY_MEDIA,
                        earlyMediaStartCallBack,
                        null,
                        CallEvent.EARLY_MEDIA_STOP_EVENT, 1000, 0, CallState.EARLY_NEGO_REQ
                )
        );

        // 2-1-7) EarlyNegoInactiveStartEvent : CallState.EARLY_NEGO_REQ > CallState.INACTIVE
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.EARLY_NEGO_INACTIVE_START_EVENT,
                        CallState.EARLY_NEGO_REQ, CallState.INACTIVE,
                        earlyNegoInactiveStartCallBack,
                        null,
                        null, 0, 0
                )
        );

        // 2-1-8) EarlyMediaNegoStartEvent : CallState.EARLY_MEDIA > CallState.NEGO_REQ
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.EARLY_MEDIA_NEGO_START_EVENT,
                        CallState.EARLY_MEDIA, CallState.NEGO_REQ,
                        callEarlyNegoNegoStartCallBack,
                        null,
                        CallEvent.NEGO_INACTIVE_START_EVENT, 1000, 0, CallState.EARLY_MEDIA
                )
        );

        // 2-1-9) EarlyMediaStopEvent : CallState.EARLY_MEDIA > CallState.HANGUP_REQ
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.EARLY_MEDIA_STOP_EVENT,
                        CallState.EARLY_MEDIA, CallState.HANGUP_REQ,
                        earlyMediaStopCallBack,
                        null,
                        CallEvent.CALL_STOP_DONE_FAIL_EVENT, 1000, 0, CallState.EARLY_MEDIA
                )
        );

        // 2-1-10) ActiveStartEvent : CallState.NEGO_REQ > CallState.ACTIVE
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.ACTIVE_START_EVENT,
                        CallState.NEGO_REQ, CallState.ACTIVE,
                        activeStartCallBack,
                        null,
                        CallEvent.ACTIVE_STOP_EVENT, 1000, 0, CallState.NEGO_REQ
                )
        );

        // 2-1-11) NegoInactiveStartEvent : CallState.NEGO_REQ > CallState.INACTIVE
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.NEGO_INACTIVE_START_EVENT,
                        CallState.NEGO_REQ, CallState.INACTIVE,
                        negoInactiveStartCallBack,
                        null,
                        null, 0, 0
                )
        );

        // 2-1-12) ActiveStopEvent : CallState.ACTIVE > CallState.HANGUP_REQ
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.ACTIVE_STOP_EVENT,
                        CallState.ACTIVE, CallState.HANGUP_REQ,
                        activeStopCallBack,
                        null,
                        CallEvent.CALL_STOP_DONE_FAIL_EVENT, 1000, 0, CallState.ACTIVE
                )
        );

        // 2-1-13) InactiveStopEvent : CallState.INACTIVE > CallState.HANGUP_REQ
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.INACTIVE_STOP_EVENT,
                        CallState.INACTIVE, CallState.HANGUP_REQ,
                        callInactiveStopCallBack,
                        null,
                        CallEvent.CALL_STOP_DONE_FAIL_EVENT, 1000, 0, CallState.INACTIVE
                )
        );

        // 2-1-14) CallStopDoneSuccessEvent : CallState.HANGUP_REQ > CallState.INIT
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.CALL_STOP_DONE_SUCCESS_EVENT,
                        CallState.HANGUP_REQ, CallState.INIT,
                        callStopDoneSuccessCallBack,
                        null,
                        null, 0, 0
                )
        );

        // 2-1-15) CallStopDoneFailEvent : CallState.HANGUP_REQ > CallState.IDLE
        Assert.assertTrue(
                callStateHandler.addState(
                        CallEvent.CALL_STOP_DONE_FAIL_EVENT,
                        CallState.HANGUP_REQ, CallState.IDLE,
                        callStopDoneFailCallBack,
                        null,
                        null, 0, 0
                )
        );

        Assert.assertFalse(callStateHandler.getEventList().isEmpty());

        // 2-2) MediaState
        // 2-2-1) MediaStartEvent : MediaState.IDLE_STATE > MediaState.ACTIVE_REQUEST
        Assert.assertTrue(
                mediaStateHandler.addState(
                        MediaEvent.MEDIA_START_EVENT,
                        MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST,
                        mediaStartCallBack,
                        null,
                        MediaEvent.MEDIA_CREATE_FAIL_EVENT, 1000, 0, MediaState.IDLE_STATE
                )
        );

        // 2-2-2) MediaCreateSuccessEvent : MediaState.ACTIVE_REQUEST > MediaState.ACTIVE_STATE
        Assert.assertTrue(
                mediaStateHandler.addState(
                        MediaEvent.MEDIA_CREATE_SUCCESS_EVENT,
                        MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE,
                        mediaCreateSuccessCallBack,
                        null,
                        MediaEvent.MEDIA_STOP_EVENT, 1000, 0, MediaState.ACTIVE_REQUEST
                )
        );

        // 2-2-3) MediaCreateFailEvent : MediaState.ACTIVE_REQUEST > MediaState.IDLE_STATE
        Assert.assertTrue(
                mediaStateHandler.addState(
                        MediaEvent.MEDIA_CREATE_FAIL_EVENT,
                        MediaState.ACTIVE_REQUEST, MediaState.IDLE_STATE,
                        mediaCreateFailCallBack,
                        null,
                        null, 0, 0
                )
        );

        // 2-2-4) MediaStopEvent : MediaState.ACTIVE_STATE > MediaState.IDLE_REQUEST
        Assert.assertTrue(
                mediaStateHandler.addState(
                        MediaEvent.MEDIA_STOP_EVENT,
                        MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST,
                        mediaStopCallBack,
                        null,
                        MediaEvent.MEDIA_DELETE_FAIL_EVENT, 1000, 0, MediaState.ACTIVE_STATE
                )
        );

        // 2-2-5) MediaDeleteSuccessEvent : MediaState.IDLE_REQUEST > MediaState.IDLE_STATE
        Assert.assertTrue(
                mediaStateHandler.addState(
                        MediaEvent.MEDIA_DELETE_SUCCESS_EVENT,
                        MediaState.IDLE_REQUEST,
                        MediaState.IDLE_STATE,
                        mediaDeleteSuccessCallBack,
                        null,
                        null, 0, 0
                )
        );

        // 2-2-6) MediaDeleteFailEvent : MediaState.IDLE_REQUEST > MediaState.ACTIVE_STATE
        Assert.assertTrue(
                mediaStateHandler.addState(
                        MediaEvent.MEDIA_DELETE_FAIL_EVENT,
                        MediaState.IDLE_REQUEST, MediaState.ACTIVE_STATE,
                        mediaDeleteFailCallBack,
                        null,
                        null, 0, 0
                )
        );

        Assert.assertFalse(mediaStateHandler.getEventList().isEmpty());

        ////////////////////////////////////////////////////////////////////////////

        // 2-3) CallStateHandler EventCondition
        // 2-3-1) InactiveStopEventCondition : CallEvent.INACTIVE_STOP_EVENT
        callStateHandler.addEventCondition(
                new InactiveStopEventCondition(
                        callStateHandler.getEvent(CallEvent.INACTIVE_STOP_EVENT)
                )
        );

        // 2-3-2) CallStopDoneSuccessEventCondition : CallEvent.CALL_STOP_DONE_SUCCESS_EVENT
        callStateHandler.addEventCondition(
                new CallStopDoneSuccessEventCondition(
                        callStateHandler.getEvent(CallEvent.CALL_STOP_DONE_SUCCESS_EVENT)
                )
        );

        // 2-4) MediaStateHandler EventCondition
        // 2-4-1) MediaDeleteSuccessEventCondition : MediaEvent.MEDIA_DELETE_SUCCESS_EVENT
        mediaStateHandler.addEventCondition(
                new MediaDeleteSuccessEventCondition(
                        mediaStateHandler.getEvent(MediaEvent.MEDIA_DELETE_SUCCESS_EVENT)
                )
        );

        // 2-5) CallStateHandler StateScheduler
        StateTaskManager.getInstance().addStateScheduler(callStateHandler, 100);

        // 2-6) MediaStateHandler StateScheduler
        StateTaskManager.getInstance().addStateScheduler(mediaStateHandler, 100);
    }

    public void stop () {
        StateManager stateManager = StateManager.getInstance();

        StateTaskManager.getInstance().removeStateScheduler(CallState.CALL_STATE_NAME);
        StateTaskManager.getInstance().removeStateScheduler(MediaState.MEDIA_STATE_NAME);

        stateManager.removeStateHandler(CallState.CALL_STATE_NAME);
        stateManager.removeStateHandler(MediaState.MEDIA_STATE_NAME);

        stateManager.stop();
    }

}

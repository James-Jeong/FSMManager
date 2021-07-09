package base.basic.call.base.callback;

import base.basic.call.base.CallInfo;
import base.basic.media.base.MediaEvent;
import base.basic.media.base.MediaState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.event.base.CallBack;
import state.basic.module.StateHandler;
import state.basic.unit.StateUnit;

/**
 * @class public class OfferEarlyNegoCallBack extends CallBack
 * @brief OfferEarlyNegoCallBack class
 */
public class OfferEarlyNegoCallBack extends CallBack {

    private static final Logger logger = LoggerFactory.getLogger(OfferEarlyNegoCallBack.class);

    public OfferEarlyNegoCallBack(String name) {
        super(name);
    }

    @Override
    public Object callBackFunc(Object... object) {
        if (object.length == 0) {
            return null;
        }

        /*CallInfo callInfo = (CallInfo) object[0];
        if (callInfo == null) { return null; }*/

        StateUnit stateUnit = getCurStateUnit();
        if (stateUnit == null) { return null; }

        CallInfo callInfo = (CallInfo) stateUnit.getData();
        if (callInfo == null) { return null; }

        StateManager stateManager = StateManager.getInstance();
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);
        if (mediaStateHandler == null) { return null; }

        logger.info("OfferEarlyNegoCallBack: callId={}, fromNo={}, toNo={}, mediaStateUnitName={}", callInfo.getCallId(), callInfo.getFromNo(), callInfo.getToNo(), callInfo.getMediaStateUnitName());

        return mediaStateHandler.fire(
                MediaEvent.MEDIA_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                MediaState.ACTIVE_REQUEST
        );
    }

}

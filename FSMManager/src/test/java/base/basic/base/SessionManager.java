package base.basic.base;


import base.basic.call.base.CallInfo;
import base.basic.call.base.CallState;
import base.basic.media.base.MediaState;
import state.StateManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @class public class SessionManager
 * @brief SessionManager class
 */
public class SessionManager {

    private final Map<String, CallInfo> callInfoMap = new HashMap<>();

    private static SessionManager sessionManager;

    public SessionManager() {
        // Nothing
    }

    public static SessionManager getInstance() {
        if (sessionManager == null) {
            sessionManager = new SessionManager();
        }

        return sessionManager;
    }

    public void createCall (String callId, String fromNo, String toNo) {
        synchronized (callInfoMap) {
            CallInfo callInfo = new CallInfo(callId, fromNo, toNo);
            StateManager stateManager = StateManager.getInstance();

            stateManager.addStateUnit(
                    callInfo.getSipStateUnitName(),
                    CallState.CALL_STATE_NAME,
                    CallState.INIT,
                    callInfo
            );

            stateManager.addStateUnit(
                    callInfo.getMediaStateUnitName(),
                    MediaState.MEDIA_STATE_NAME,
                    MediaState.IDLE_STATE,
                    callInfo
            );

            callInfoMap.putIfAbsent(callId, callInfo);
        }
    }

    public void removeCall (String callId) {
        synchronized (callInfoMap) {
            CallInfo callInfo = callInfoMap.get(callId);
            if (callInfo != null) {
                StateManager stateManager = StateManager.getInstance();

                stateManager.removeStateUnit(callInfo.getSipStateUnitName());
                stateManager.removeStateUnit(callInfo.getMediaStateUnitName());

                callInfoMap.remove(callId);
            }
        }
    }

    public CallInfo getCall (String callId) {
        synchronized (callInfoMap) {
            return callInfoMap.get(callId);
        }
    }

}

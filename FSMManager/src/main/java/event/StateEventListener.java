package event;

import state.StateHandler;
import state.StateManager;

import java.util.Map;

/**
 * @class public class EventListener implements EventCallBack
 * @brief EventListener class
 */
public class StateEventListener implements StateEventCallBack {

    public StateEventListener() {
        // Nothing
    }

    @Override
    public void onEvent(String handlerName, String event, String fromState) {
        if (handlerName == null) { return; }

        StateManager stateManager = StateManager.getInstance();
        StateHandler stateHandler = stateManager.getStateHandler(handlerName);
        if (stateHandler == null) { return; }

        Map<String, String> stateMap = stateHandler.findEvent(event);
        if (stateMap != null) {
            String toState = stateMap.get(fromState);
            stateHandler.nextState(toState);
        }
    }
}

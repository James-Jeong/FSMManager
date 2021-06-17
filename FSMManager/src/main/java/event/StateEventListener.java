package event;

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
    public void onEvent(String event, String fromState) {
        StateManager stateManager = StateManager.getInstance();
        Map<String, String> stateMap = stateManager.findEvent(event);
        if (stateMap != null) {
            String toState = stateMap.get(fromState);
            stateManager.nextState(toState);
        }
    }
}

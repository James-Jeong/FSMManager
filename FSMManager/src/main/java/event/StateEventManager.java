package event;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @class public class StateEventManager
 * @brief StateEventManager class
 */
public class StateEventManager {

    private StateEventCallBack listener = null;
    private static final Map<String, Map<String, String>> eventMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    public StateEventManager() {
        setListener(new StateEventListener());
    }

    ////////////////////////////////////////////////////////////////////////////////

    public synchronized void setListener(StateEventCallBack eventCallBack) {
        listener = eventCallBack;
    }

    public synchronized void addEvent(String event, String fromState, String toState) {
        Map<String, String> stateMap = getEventKeyMap(event);
        if (stateMap == null) {
            stateMap = new LinkedHashMap<>();
            stateMap.putIfAbsent(fromState, toState);
            eventMap.putIfAbsent(event, stateMap);
        } else {
            stateMap.putIfAbsent(fromState, toState);
        }
    }

    public Map<String, String> getEventKeyMap(String event) {
        if (eventMap.isEmpty()) { return null; }
        return eventMap.get(event);
    }

    public synchronized void callEvent(String handlerName, String event, String fromState) {
        if (listener == null) { return; }
        listener.onEvent(handlerName, event, fromState);
    }

}

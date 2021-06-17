package event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @class public class EventManager
 * @brief EventManager class
 */
public class StateEventManager {

    private static final Logger logger = LoggerFactory.getLogger(StateEventManager.class);

    private StateEventCallBack listener = null;
    private static final Map<String, Map<String, String>> eventMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    public StateEventManager() {
        // Nothing
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

    public synchronized void callEvent(String event, String fromState) {
        if (listener == null) { return; }
        listener.onEvent(event, fromState);
    }

}

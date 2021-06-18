package event;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @class public class StateEventManager
 * @brief StateEventManager class
 */
public class StateEventManager {

    // stateEventCallBack
    private StateEventCallBack stateEventCallBack = null;
    // Event Map
    private static final Map<String, Map<String, String>> eventMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public StateEventManager()
     * @brief StateEventManager 생성자 함수
     */
    public StateEventManager() {
        setListener(new StateEventListener());
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public synchronized void setListener(StateEventCallBack eventCallBack)
     * @brief StateEventCallBack 을 설정하는 함수
     * @param eventCallBack StateEventCallBack
     */
    public synchronized void setListener(StateEventCallBack eventCallBack) {
        stateEventCallBack = eventCallBack;
    }

    /**
     * @fn public synchronized void addEvent(String event, String fromState, String toState)
     * @brief 새로운 이벤트를 생성하는 함수
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     * @param toState 천이 후 State 이름
     */
    public synchronized void addEvent(String event, String fromState, String toState) {
        Map<String, String> stateMap = getStateMap(event);
        if (stateMap == null) {
            stateMap = new LinkedHashMap<>();
            stateMap.putIfAbsent(fromState, toState);
            eventMap.putIfAbsent(event, stateMap);
        } else {
            stateMap.putIfAbsent(fromState, toState);
        }
    }

    /**
     * @fn public Map<String, String> getEventKeyMap(String event)
     * @brief 지정한 이벤트에 등록된 State Map 을 반환하는 함수
     * @param event 이벤트 이름
     * @return 성공 시 State Map, 실패 시 null 반환
     */
    public Map<String, String> getStateMap(String event) {
        if (eventMap.isEmpty()) { return null; }
        return eventMap.get(event);
    }

    /**
     * @fn public synchronized void callEvent(String handlerName, String event, String fromState)
     * @brief 지정한 이벤트를 호출하는 함수
     * @param handlerName 이벤트를 호출하는 StateHandler 이름
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     */
    public synchronized void callEvent(String handlerName, String event, String fromState) {
        if (stateEventCallBack == null) { return; }
        stateEventCallBack.onEvent(handlerName, event, fromState);
    }

}

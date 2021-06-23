package state.basic.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class public class StateEventManager
 * @brief StateEventManager class
 */
public class StateEventManager {

    private static final Logger logger = LoggerFactory.getLogger(StateEventManager.class);

    // stateEventCallBack
    private StateEventCallBack stateEventCallBack = null;
    // Event Map
    private static final Map<String, Map<String, String>> eventMap = new ConcurrentHashMap<>();

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
     * @fn public void setListener(StateEventCallBack eventCallBack)
     * @brief StateEventCallBack 을 설정하는 함수
     * @param eventCallBack StateEventCallBack
     */
    public void setListener(StateEventCallBack eventCallBack) {
        stateEventCallBack = eventCallBack;
    }

    /**
     * @fn public void addEvent(String event, String fromState, String toState)
     * @brief 새로운 이벤트를 생성하는 함수
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     * @param toState 천이 후 State 이름
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean addEvent(String event, String fromState, String toState) {
        if (getToStateFromEvent(event, fromState) != null) {
            logger.warn("Duplicated event. (event={}, fromState={}, toState={})", event, fromState, toState);
            return false;
        }

        Map<String, String> stateMap = getStateMap(event);
        if (stateMap == null) {
            stateMap = new ConcurrentHashMap<>();
            stateMap.putIfAbsent(fromState, toState);
        }

        boolean result = eventMap.putIfAbsent(event, stateMap) == null;
        if (result) {
            logger.info("Success to add state into event. (event={}, fromState={}, toState={})", event, fromState, toState);
        } else {
            result = stateMap.putIfAbsent(fromState, toState) == null;
            if (result) {
                logger.info("Success to add state into event. (event={}, fromState={}, toState={})", event, fromState, toState);
            } else {
                logger.warn("Fail to add state into event. (event={}, fromState={}, toState={})", event, fromState, toState);
            }
        }

        return result;
    }

    public String getToStateFromEvent (String event, String fromState) {
        if (getStateMap(event) == null) { return null; }
        return getStateMap(event).get(fromState);
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
     * @fn public void callEvent(String handlerName, String event, String fromState)
     * @brief 지정한 이벤트를 호출하는 함수
     * @param handlerName 이벤트를 호출하는 StateHandler 이름
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     */
    public void callEvent(String handlerName, String event, String fromState) {
        if (stateEventCallBack == null) {
            logger.warn("Unknown event. (handlerName={}, event={}, fromState={})", handlerName, event, fromState);
            return;
        }

        stateEventCallBack.onEvent(handlerName, event, fromState);
    }

}

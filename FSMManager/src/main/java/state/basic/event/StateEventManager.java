package state.basic.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.info.ResultCode;
import state.basic.state.StateUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class public class StateEventManager
 * @brief StateEventManager class
 */
public class StateEventManager {

    private static final Logger logger = LoggerFactory.getLogger(StateEventManager.class);

    // stateEventCallBack
    private final StateEventCallBack stateEventCallBack;

    // Event Map
    private final Map<String, Map<String, String>> eventMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public StateEventManager()
     * @brief StateEventManager 생성자 함수
     */
    public StateEventManager() {
        stateEventCallBack = new StateEventListener();
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addEvent(String event, String fromState, String toState, String failState)
     * @brief 새로운 이벤트를 생성하는 함수
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     * @param toState 천이 후 State 이름
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean addEvent(String event, String fromState, String toState) {
        if (getToStateFromEvent(event, fromState) != null) {
            logger.warn("[{}] Duplicated event. (event={}, fromState={}, toState={})",
                    ResultCode.DUPLICATED_EVENT, event, fromState, toState
            );
            return false;
        }

        synchronized (eventMap) {
            Map<String, String> stateMap = getStateMap(event);
            if (stateMap == null) {
                stateMap = new HashMap<>();
                stateMap.putIfAbsent(fromState, toState);
            }

            boolean result = eventMap.putIfAbsent(event, stateMap) == null;
            if (result) {
                logger.debug("[{}] Success to add state into event. (event={}, fromState={}, toState={})",
                        ResultCode.SUCCESS_ADD_STATE, event, fromState, toState
                );
            } else {
                result = stateMap.putIfAbsent(fromState, toState) == null;
                if (result) {
                    logger.debug("[{}] Success to add state into event. (event={}, fromState={}, toState={})",
                            ResultCode.SUCCESS_ADD_STATE, event, fromState, toState
                    );
                } else {
                    logger.warn("[{}] Fail to add state into event. (event={}, fromState={}, toState={})",
                            ResultCode.FAIL_ADD_STATE, event, fromState, toState
                    );
                }
            }

            return result;
        }
    }

    /**
     * @fn public void removeAllEvents()
     * @brief 등록된 모든 이벤트들을 삭제하는 함수
     */
    public void removeAllEvents() {
        synchronized (eventMap) {
            eventMap.clear();
        }
    }

    /**
     * @fn public boolean removeEvent(String fromState)
     * @brief From state 를 Map 에서 삭제하는 함수
     * 다른 From state 와 To state 로 포함되어 있으면 다 삭제
     * @param event Event
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeEvent(String event) {
        synchronized (eventMap) {
            boolean result = eventMap.remove(event) != null;
            if (result) {
                logger.debug("[{}] Success to remove the from state. (event={})",
                        ResultCode.SUCCESS_REMOVE_STATE, event
                );
            } else {
                logger.debug("[{}] Fail to remove the from state. (event={})",
                        ResultCode.FAIL_REMOVE_STATE, event
                );
            }

            return result;
        }
    }

    /**
     * @fn public List<String> getAllEvents ()
     * @brief 정의된 모든 이벤트들을 새로운 리스트에 저장하여 반환하는 함수
     * @return 성공 시 정의된 이벤트 리스트, 실패 시 null 반환
     */
    public List<String> getAllEvents () {
        synchronized (eventMap) {
            if (eventMap.isEmpty()) {
                return null;
            }
            return new ArrayList<>(eventMap.keySet());
        }
    }

    /**
     * @fn public String getToStateFromEvent (String event, String fromState)
     * @brief 이벤트 이름과 From state 로 To state 를 반환하는 함수
     * @param event 이벤트 이름
     * @param fromState From state
     * @return 성공 시 To state, 실패 시 null 반환
     */
    public String getToStateFromEvent (String event, String fromState) {
        Map<String, String> stateMap = getStateMap(event);
        if (stateMap == null) { return null; }
        return stateMap.get(fromState);
    }

    /**
     * @fn public Map<String, String> getEventKeyMap(String event)
     * @brief 지정한 이벤트에 등록된 State Map 을 반환하는 함수
     * @param event 이벤트 이름
     * @return 성공 시 State Map, 실패 시 null 반환
     */
    public Map<String, String> getStateMap(String event) {
        synchronized (eventMap) {
            if (eventMap.isEmpty()) { return null; }
            return eventMap.get(event);
        }
    }

    /**
     * @fn public String callEvent(String handlerName, String event, StateUnit stateUnit, String failState, Object... params)
     * @brief 지정한 이벤트를 호출하는 함수
     * @param handlerName 이벤트를 호출하는 StateHandler 이름
     * @param event 이벤트 이름
     * @param stateUnit State unit
     * @param failState 천이 실패 시 반환될 State 이름
     * @param params CallBack 가변 매개변수
     * @return 성공 시 지정한 결과값 반환, 실패 시 null 반환
     */
    public String callEvent(String handlerName, String event, StateUnit stateUnit, String failState, Object... params) {
        return stateEventCallBack.onEvent(handlerName, event, stateUnit, failState, params);
    }

}

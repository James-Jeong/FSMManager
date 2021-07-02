package state.basic.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.event.base.StateEvent;
import state.basic.info.ResultCode;
import state.basic.module.StateHandler;
import state.basic.module.StateTaskManager;
import state.basic.module.base.StateTaskUnit;
import state.basic.unit.StateUnit;

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
    private final Map<String, StateEvent> eventMap = new HashMap<>();

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
     * @fn public boolean addEvent(String event, String fromState, String toState, String failEvent, int delay)
     * @brief 새로운 이벤트를 생성하는 함수
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     * @param toState 천이 후 State 이름
     * @param failEvent 천이 실패 후 실행될 이벤트 이름
     * @param delay 천이 실패 후 실행될 이벤트가 실행되기 위한 Timeout 시간
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean addEvent(String event, String fromState, String toState, String failEvent, int delay) {
        if (event == null || fromState == null || toState == null) {
            logger.warn("[{}] Fail to add event. (event={}, fromState={}, toState={})",
                    ResultCode.FAIL_ADD_EVENT, event, fromState, toState
            );
            return false;
        }

        StateEvent stateEvent = getStateEventByEvent(event);
        if (stateEvent != null) {
            logger.warn("[{}] Duplicated event. (event={}, fromState={}, toState={})",
                    ResultCode.DUPLICATED_EVENT, event, fromState, toState
            );
            return false;
        }

        synchronized (eventMap) {
            boolean result = eventMap.putIfAbsent(event, new StateEvent(fromState, toState, failEvent, delay)) == null;
            if (result) {
                logger.info("[{}] Success to add state into event. (event={}, fromState={}, toState={})",
                        ResultCode.SUCCESS_ADD_STATE, event, fromState, toState
                );
            } else {
                logger.warn("[{}] Fail to add state into event. (event={}, fromState={}, toState={})",
                        ResultCode.FAIL_ADD_STATE, event, fromState, toState
                );
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
                logger.info("[{}] Success to remove the from state. (event={})",
                        ResultCode.SUCCESS_REMOVE_STATE, event
                );
            } else {
                logger.info("[{}] Fail to remove the from state. (event={})",
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
            return new ArrayList<>(eventMap.keySet());
        }
    }

    /**
     * @fn public StateEvent getStateEventByEvent (String event)
     * @brief 이벤트 이름으로 StateEvent 를 반환하는 함수
     * @param event 이벤트 이름
     * @return 성공 시 StateEvent, 실패 시 null 반환
     */
    public StateEvent getStateEventByEvent(String event) {
        synchronized (eventMap) {
            return eventMap.get(event);
        }
    }

    /**
     * @fn public String callEvent(String handlerName, String event, StateUnit stateUnit, Object... params)
     * @brief 지정한 이벤트를 호출하는 함수
     * @param handlerName 이벤트를 호출하는 StateHandler 이름
     * @param event 이벤트 이름
     * @param stateUnit State unit
     * @param params CallBack 가변 매개변수
     * @return 성공 시 지정한 결과값 반환, 실패 시 null 반환
     */
    public String callEvent(String handlerName, String event, StateUnit stateUnit, Object... params) {
        StateHandler stateHandler = StateManager.getInstance().getStateHandler(handlerName);
        if (stateHandler == null) {
            logger.warn("[{}] ({}) Fail to find the stateHandler. Must define the handler. (event={}, stateUnit={})",
                    ResultCode.FAIL_GET_STATE_HANDLER, handlerName, event, stateUnit
            );
            return null;
        }

        StateEvent stateEvent = stateHandler.findStateEventFromEvent(event);
        if (stateEvent == null) {
            logger.warn("[{}] ({}) Fail to find the event. Must define the event. (event={}, stateUnit={})",
                    ResultCode.FAIL_GET_EVENT, handlerName, event, stateUnit
            );
            return null;
        }

        String fromState = stateEvent.getFromState();
        String toState = stateEvent.getToState();
        String failEvent = stateEvent.getFailEvent();
        int delay = stateEvent.getDelay();

        String result = stateEventCallBack.onEvent(stateHandler, event, stateUnit, fromState, toState, params);
        if (failEvent != null) {
            if (result != null) {
                // 천이에 성공했을 때,
                // 다음 상태에 대해 기대되는 상태 천이(또는 이벤트)가
                // 일정 시간 이후에 발생하지 않을 경우 지정한 실패 이벤트를 발생시킨다.
                StateTaskManager.getInstance().addTask(
                        stateUnit.getName() + "_" + toState,
                        new StateTaskUnit(
                                handlerName,
                                failEvent,
                                stateUnit,
                                delay
                        )
                );

                logger.info("[{}] ({}) FailEvent is reserved. (event={}, failEvent={}, delay={})",
                        ResultCode.SUCCESS_RESERVE_FAIL_STATE, handlerName, event, failEvent, delay
                );
            } else {
                logger.warn("[{}] ({}) FailEvent is not reserved. Transition is failed. (event={})",
                        ResultCode.FAIL_RESERVE_FAIL_STATE, handlerName, event
                );
            }
        }

        // 천이에 성공했을 때,
        // 만약 현재 상태가 기대되는 상태 천이의 현재 상태이면
        // 이전에 등록된 failEvent 를 취소시킨다.
        if (result != null) {
            StateTaskManager.getInstance().removeTask(stateUnit.getName() + "_" + fromState);
        }

        return result;
    }

}

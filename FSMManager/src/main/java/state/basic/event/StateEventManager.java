package state.basic.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.event.base.CallBack;
import state.basic.event.base.StateEvent;
import state.basic.info.ResultCode;
import state.basic.module.StateHandler;
import state.basic.module.base.StateTaskUnit;
import state.basic.unit.StateUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @class public class StateEventManager
 * @brief StateEventManager class
 */
public class StateEventManager {

    private static final Logger logger = LoggerFactory.getLogger(StateEventManager.class);

    // Event Map
    private final Map<String, StateEvent> eventMap = new HashMap<>();

    private final ReentrantLock stateLock = new ReentrantLock();

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public StateEventManager()
     * @brief StateEventManager 생성자 함수
     */
    public StateEventManager() {
        // Nothing
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public boolean addEvent(String event, String fromState, String toState, CallBack callBack, String nextEvent, int delay, Object... params)
     * @brief 새로운 이벤트를 생성하는 함수
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     * @param toState 천이 후 State 이름
     * @param callBack 천이 성공 후 실행될 CallBack
     * @param nextEvent 천이 실패 후 실행될 이벤트 이름
     * @param delay 천이 실패 후 실행될 이벤트가 실행되기 위한 Timeout 시간
     * @param nextEventCallBackParams 실패 후 실행될 이벤트의 CallBack 의 매개변수
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean addEvent(String event,
                            String fromState,
                            String toState,
                            CallBack callBack,
                            String nextEvent,
                            int delay,
                            Object... nextEventCallBackParams) {
        if (event == null || fromState == null || toState == null) {
            logger.warn("[{}] Fail to add event. (event={}, fromState={}, toState={})",
                    ResultCode.FAIL_ADD_EVENT, event, fromState, toState
            );
            return false;
        }

        if (fromState.equals(toState)) {
            logger.warn("[{}] Fail to add event. (From == To) (event={}, fromState={}, toState={})",
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
            stateEvent = new StateEvent(
                    event,
                    fromState,
                    toState,
                    callBack,
                    nextEvent,
                    delay,
                    nextEventCallBackParams
            );

            boolean result = eventMap.putIfAbsent(event, stateEvent) == null;
            if (result) {
                logger.info("[{}] Success to add state. (event={}, fromState={}, toState={})",
                        ResultCode.SUCCESS_ADD_STATE, stateEvent, fromState, toState
                );
            } else {
                logger.warn("[{}] Fail to add state. (event={}, fromState={}, toState={})",
                        ResultCode.FAIL_ADD_STATE, stateEvent, fromState, toState
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
     * @fn public String nextState(StateHandler stateHandler, String event, StateUnit stateUnit, Object... params)
     * @brief 지정한 이벤트에 일치하는 상태 천이를 진행하는 함수
     * @param stateHandler StateHandler
     * @param event 이벤트 이름
     * @param stateUnit State unit
     * @param params CallBack 가변 매개변수
     * @return 성공 시 천이 후 상태값 반환, 실패 시 null 또는 천이 전 상태값 반환
     */
    public String nextState(StateHandler stateHandler, String event, StateUnit stateUnit, Object... params) {
        StateEvent stateEvent = getStateEventByEvent(event);
        if (stateEvent == null) {
            logger.warn("[{}] ({}) Fail to find the event. Must define the event. (event={}, stateUnit={})",
                    ResultCode.FAIL_GET_EVENT, stateHandler.getName(), event, stateUnit
            );
            return null;
        }

        if (stateUnit == null) {
            logger.warn("[{}] ({}) StateUnit is null. (event={})",
                    ResultCode.NULL_OBJECT, stateHandler.getName(), event
            );
            return null;
        }

        try {
            stateLock.lock();

            String fromState = stateEvent.getFromState();
            String toState = stateEvent.getToState();
            String nextEvent = stateEvent.getNextEvent();
            int delay = stateEvent.getDelay();

            if (!fromState.equals(stateUnit.getCurState())) {
                logger.warn("[{}] ({}) Fail to transit. From state is not matched. (event={}, fromState: cur={}, expected={})",
                        ResultCode.FAIL_TRANSIT_STATE, stateHandler.getName(), event, stateUnit.getCurState(), fromState
                );
                return stateUnit.getCurState();
            }

            // 1) 상태 천이
            stateUnit.setPrevState(fromState);
            stateUnit.setCurState(toState);

            // 2) Prev Event 취소
            // 만약 현재 상태가 기대되는 상태 천이의 현재 상태이면
            // 이전에 등록된 nextEvent 를 취소시킨다.
            if (stateUnit.getNextEventKey() != null) {
                StateManager.getInstance().removeStateTaskUnit(
                        stateHandler.getName(),
                        stateUnit.getNextEventKey()
                );

                stateUnit.setNextEventKey(null);
            }

            // 3) Next Event 추가
            if (nextEvent != null) {
                // 다음 상태에 대해 기대되는 상태 천이(또는 이벤트)가
                // 일정 시간 이후에 발생하지 않을 경우 지정한 다음 이벤트를 발생시킨다.
                StateManager.getInstance().addStateTaskUnit(
                        stateHandler.getName(),
                        stateUnit.setNextEventKey(toState),
                        new StateTaskUnit(
                                stateHandler,
                                nextEvent,
                                stateUnit,
                                delay,
                                stateEvent.getNextEventCallBackParams()
                        )
                );
            }

            // 4) CallBack 실행
            CallBack callBack = stateEvent.getCallBack();
            if (callBack != null) {
                callBack.setCurStateUnit(stateUnit);
                stateUnit.setCallBackResult(
                        callBack.callBackFunc(params)
                );
            }
        } catch (Exception e) {
            logger.warn("[{}] ({}) Fail to transit. StateEventManager.nextState.Exception (event={}, curState={})",
                    ResultCode.FAIL_TRANSIT_STATE, stateHandler.getName(), event, stateUnit.getCurState(), e
            );
        } finally {
            stateLock.unlock();
        }

        return stateUnit.getCurState();
    }

}

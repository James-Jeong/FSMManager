package state.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.base.CallBack;

import java.util.*;

/**
 * @class public class StateContainer
 * @brief State Container class
 */
public class StateContainer {

    private static final Logger logger = LoggerFactory.getLogger(StateContainer.class);

    // State Map
    private final Map<String, Map<String, CallBack>> stateMap = new HashMap<>();

    // 현재 State 이름
    private String curState = null;
    // CallBack 결과값
    private Object callBackResult = null;
    // StateContainer 이름
    private final String name;

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public StateContainer()
     * @brief StateContainer 생성자 함수
     * @param name StateContainer 이름
     */
    public StateContainer(String name) {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public boolean addToStateByFromState(String fromState, String toState, CallBack callBack)
     * @brief From state 와 연관된 To state 를 모두 Map 에 추가하는 함수
     * @param fromState From state
     * @param toState To state
     * @param callBack CallBack
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean addToStateByFromState(String fromState, String toState, CallBack callBack) {
        synchronized (stateMap) {
            if (getCallBackByFromState(fromState, toState) != null) {
                return false;
            }

            Map<String, CallBack> toStateMap = getToStateByFromState(fromState);
            if (toStateMap == null) {
                toStateMap = new HashMap<>();
                toStateMap.putIfAbsent(toState, callBack);
            }

            boolean result = stateMap.putIfAbsent(fromState, toStateMap) == null;
            if (result) {
                logger.info("({}) Success to add state. (fromState={}, toState={})", name, fromState, toState);
            } else {
                result = toStateMap.putIfAbsent(toState, callBack) == null;
                if (result) {
                    logger.info("({}) Success to add state. (fromState={}, toState={})", name, fromState, toState);
                } else {
                    logger.info("({}) Fail to add state. (fromState={}, toState={})", name, fromState, toState);
                }
            }
            return result;
        }
    }

    /**
     * @fn public boolean removeFromState(String fromState)
     * @brief From state 를 Map 에서 삭제하는 함수
     * 다른 From state 와 To state 로 포함되어 있으면 다 삭제
     * @param fromState From state
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeFromState(String fromState) {
        synchronized (stateMap) {
            boolean result = stateMap.remove(fromState) != null;
            if (result) {
                // 다른 From state 와 To state 로 포함되어 있으면 다 삭제
                for (Map.Entry<String, Map<String, CallBack>> mapEntry : stateMap.entrySet()) {
                    if (mapEntry.getValue() == null) { continue; }
                    for (String toState : mapEntry.getValue().keySet()) {
                        if (toState.equals(fromState)) {
                            removeToStateByFromState(mapEntry.getKey(), fromState);
                        }
                    }
                }

                logger.info("({}) Success to remove the from state. (fromState={})", name, fromState);
            } else {
                logger.info("({}) Fail to remove the from state. (fromState={})", name, fromState);
            }
            return result;
        }
    }

    /**
     * @fn public boolean removeToStateByFromState(String fromState, String toState)
     * @brief From state 와 연관된 To state 를 Map 에서 삭제
     * From state 는 삭제되지 않고 To state 만 삭제
     * @param fromState From state
     * @param toState To state
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeToStateByFromState(String fromState, String toState) {
        synchronized (stateMap) {
            if (getToStateByFromState(fromState) == null) {
                return false;
            }

            boolean result = getToStateByFromState(fromState).remove(toState) != null;
            if (result) {
                logger.info("({}) Success to remove the to state. (fromState={}, toState={})", name, fromState, toState);
            } else {
                logger.info("({}) Fail to remove the to state. (fromState={}, toState={})", name, fromState, toState);
            }
            return result;
        }
    }

    /**
     * @fn public Map<String, CallBack> getToStateByFromState(String fromState)
     * @brief From state 와 연관된 To state Map 을 반환하는 함수
     * @param fromState From state
     * @return 성공 시 To state Map, 실패 시 null 반환
     */
    public Map<String, CallBack> getToStateByFromState(String fromState) {
        return stateMap.get(fromState);
    }

    /**
     * @fn public CallBack getCallBackByFromState(String fromState, String toState)
     * @brief From state 와 연관된 To state 의 CallBack 을 반환하는 함수
     * @param fromState From state
     * @param toState To state
     * @return 성공 시 CallBack, 실패 시 null 반환
     */
    public CallBack getCallBackByFromState(String fromState, String toState) {
        if (getToStateByFromState(fromState) == null) { return null; }
        return getToStateByFromState(fromState).get(toState);
    }

    /**
     * @fn public synchronized String getCurState ()
     * @brief 현재 State 이름을 반환하는 함수
     * @return 현재 State 이름
     */
    public synchronized String getCurState() {
        return curState;
    }

    /**
     * @fn public synchronized void setCurState (String state)
     * @brief 현재 State 를 설정하는 함수
     * @param curState 현재 State 이름
     */
    public synchronized void setCurState(String curState) {
        logger.info("({}) State is changed. ([{}] > [{}])", name, this.curState, curState);
        this.curState = curState;
    }

    /**
     * @fn public Object getCallBackResult()
     * @brief CallBack 실행 후 발생한 결과값을 반환하는 함수
     * @return 성공 시 결과갑, 실패 시 null 반환
     */
    public Object getCallBackResult() {
        return callBackResult;
    }

    /**
     * @fn public List<String> getAllStates ()
     * @brief 정의된 모든 상태들을 새로운 리스트에 저장하여 반환하는 함수
     * @return 성공 시 정의된 상태 리스트, 실패 시 null 반환
     */
    public List<String> getAllStates () {
        if (stateMap.isEmpty()) { return null; }
        return new ArrayList<>(stateMap.keySet());
    }

    /**
     * @fn public String nextState (String toState)
     * @brief 현재 상태에서 매개변수로 전달받은 다음 상태로 천이하는 함수
     * 이 함수에서 To state 와 연관된 CallBack 이 실행되며, CallBack 결과값이 StateContainer 에 저장됨
     * @param toState To state
     * @return 성공 시 To state, 실패 시 null 반환
     */
    public String nextState (String toState) {
        if (curState == null) {
            logger.warn("({}) Fail to transit. Current state is null. (curState=null, nextState={})", name, toState);
            callBackResult = null;
            return null;
        }

        if (curState.equals(toState)) {
            logger.warn("({}) Fail to transit. State is same. (curState={}, nextState={})", name, curState, toState);
            callBackResult = null;
            return null;
        }

        Map<String, CallBack> nextStateCallBackMap = getToStateByFromState(curState);
        if (nextStateCallBackMap == null) { return null; }

        // 1) 상태 천이 먼저 수행
        setCurState(toState);

        // 2) CallBack 함수 나중에 수행
        CallBack nextStateCallBack = nextStateCallBackMap.get(toState);
        if (nextStateCallBack == null) {
            logger.warn("({}) Fail to get the next state's call back. Not defined. (curState={}, nextState={})", name, curState, toState);
            callBackResult = null;
            return null;
        } else {
            callBackResult = nextStateCallBack.callBackFunc(toState);
        }

        return toState;
    }

}

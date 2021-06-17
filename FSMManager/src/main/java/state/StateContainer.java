package state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @class public class StateContainer
 * @brief State Container class
 */
public class StateContainer {

    private static final Logger logger = LoggerFactory.getLogger(StateContainer.class);

    private final Map<String, Map<String, CallBack>> stateMap = new LinkedHashMap<>();

    private String curState = null;
    private Object callBackResult = null;

    public StateContainer() {
        // Nothing
    }

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
                toStateMap = new LinkedHashMap<>();
                toStateMap.putIfAbsent(toState, callBack);
            }

            boolean result = stateMap.putIfAbsent(fromState, toStateMap) == null;
            if (result) {
                logger.debug("Success to add state. (fromState={}, toState={})", fromState, toState);
            } else {
                result = toStateMap.putIfAbsent(toState, callBack) == null;
                if (result) {
                    logger.debug("Success to add state. (fromState={}, toState={})", fromState, toState);
                } else {
                    logger.debug("Fail to add state. (fromState={}, toState={})", fromState, toState);
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

                logger.debug("Success to remove the from state. (fromState={})", fromState);
            } else {
                logger.debug("Fail to remove the from state. (fromState={})", fromState);
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
                logger.debug("Success to remove the to state. (fromState={}, toState={})", fromState, toState);
            } else {
                logger.debug("Fail to remove the to state. (fromState={}, toState={})", fromState, toState);
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
     * @fn public int getSizeOfStateMap ()
     * @brief State Map 의 전체 크기를 반환하는 함수
     * @return Map 전체 크기를 반환
     */
    public int getSizeOfStateMap () {
        return stateMap.size();
    }

    public String getCurState() {
        return curState;
    }

    public void setCurState(String curState) {
        this.curState = curState;
    }

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
            logger.warn("Fail to transit. Current state is null. (curState=null, nextState={})", toState);
            callBackResult = null;
            return null;
        }

        if (curState.equals(toState)) {
            logger.warn("Fail to transit. State is same. (curState={}, nextState={})", curState, toState);
            callBackResult = null;
            return null;
        }

        Map<String, CallBack> nextStateCallBackMap = getToStateByFromState(curState);
        if (nextStateCallBackMap == null) { return null; }

        CallBack nextStateCallBack = nextStateCallBackMap.get(toState);
        if (nextStateCallBack == null) {
            logger.warn("Fail to get the next state's call back. Not defined. (curState={}, nextState={})", curState, toState);
            callBackResult = null;
            return null;
        }

        callBackResult = nextStateCallBack.callBackFunc(toState);

        logger.debug("State is changed. ([{}] > [{}])", curState, toState);
        curState = toState;
        return toState;
    }

}

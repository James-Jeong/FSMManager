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

    public Map<String, CallBack> getToStateByFromState(String fromState) {
        return stateMap.get(fromState);
    }

    public CallBack getCallBackByFromState(String fromState, String toState) {
        if (getToStateByFromState(fromState) == null) { return null; }
        return getToStateByFromState(fromState).get(toState);
    }

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

    public List<String> getAllStates () {
        if (stateMap.isEmpty()) { return null; }
        return new ArrayList<>(stateMap.keySet());
    }

    public String nextState (String nextState) {
        if (curState == null) {
            logger.warn("Fail to transit. Current state is null. (curState=null, nextState={})", nextState);
            callBackResult = null;
            return null;
        }

        if (curState.equals(nextState)) {
            logger.warn("Fail to transit. State is same. (curState={}, nextState={})", curState, nextState);
            callBackResult = null;
            return null;
        }

        Map<String, CallBack> nextStateCallBackMap = getToStateByFromState(curState);
        if (nextStateCallBackMap == null) { return null; }

        CallBack nextStateCallBack = nextStateCallBackMap.get(nextState);
        if (nextStateCallBack == null) {
            logger.warn("Fail to get the next state's call back. Not defined. (curState={}, nextState={})", curState, nextState);
            callBackResult = null;
            return null;
        }

        callBackResult = nextStateCallBack.callBackFunc(nextState);

        logger.debug("State is changed. ([{}] > [{}])", curState, nextState);
        curState = nextState;
        return nextState;
    }

}

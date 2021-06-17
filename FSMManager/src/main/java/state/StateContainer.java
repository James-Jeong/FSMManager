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

    public boolean addCallBackFromMapByState (String fromState, String toState, CallBack callBack) {
        if (getCallBackFromMapByFromState(fromState, toState) != null) { return false; }

        Map<String, CallBack> toStateMap = getToStateMapFromMapByFromState(fromState);
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

    public boolean removeCallBackFromMapByState (String fromState) {
        boolean result = stateMap.remove(fromState) != null;
        if (result) {
            logger.debug("Success to remove state. (fromState={})", fromState);
        } else {
            logger.debug("Fail to remove state. (fromState={})", fromState);
        }
        return result;
    }

    public Map<String, CallBack> getToStateMapFromMapByFromState (String fromState) {
        return stateMap.get(fromState);
    }

    public CallBack getCallBackFromMapByFromState (String fromState, String toState) {
        if (getToStateMapFromMapByFromState(fromState) == null) { return null; }
        return getToStateMapFromMapByFromState(fromState).get(toState);
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

        Map<String, CallBack> nextStateCallBackMap = getToStateMapFromMapByFromState(curState);
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

    public Object getCallBackResult() {
        return callBackResult;
    }

    public List<String> getAllStates () {
        if (stateMap.isEmpty()) { return null; }
        return new ArrayList<>(stateMap.keySet());
    }

}

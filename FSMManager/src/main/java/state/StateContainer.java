package state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @class public class StateContainer
 * @brief State Container class
 */
public class StateContainer {

    private static final Logger logger = LoggerFactory.getLogger(StateContainer.class);

    private final Map<String, CallBack> stateMap = new LinkedHashMap<>();

    private String curState = null;

    public StateContainer() {
        // Nothing
    }

    public boolean addCallBackFromMapByState (String state, CallBack callBack) {
        boolean result = stateMap.putIfAbsent(state, callBack) == null;
        if (result) {
            logger.debug("Success to add state. (state={})", state);
        } else {
            logger.debug("Fail to add state. (state={})", state);
        }
        return result;
    }

    public boolean removeCallBackFromMapByState (String state) {
        boolean result = stateMap.remove(state) != null;
        if (result) {
            logger.debug("Success to remove state. (state={})", state);
        } else {
            logger.debug("Fail to remove state. (state={})", state);
        }
        return result;
    }

    public CallBack getCallBackFromMapByState (String state) {
        return stateMap.get(state);
    }

    public CallBack getCallBackByState (String  state) {
        return stateMap.get(state);
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
        if (curState == null) { return null; }
        if (curState.equals(nextState)) { return null; }

        CallBack nextStateCallBack = getCallBackFromMapByState(nextState);
        if (nextStateCallBack == null) { return null; }

        nextStateCallBack.callBackFunc(nextState);
        curState = nextState;
        return nextState;
    }

}

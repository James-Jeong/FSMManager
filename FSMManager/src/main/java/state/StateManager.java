package state;

import java.util.List;

/**
 * @class public class StateManager
 * @brief StateManager class
 */
public class StateManager {

    private static StateManager stateManager;

    private final StateContainer stateContainer;

    public StateManager() {
        stateContainer = new StateContainer();
    }

    public static StateManager getInstance() {
        if (stateManager == null) {
            stateManager = new StateManager();
        }

        return stateManager;
    }

    public boolean addState (String fromState, String toState, CallBack callBack) {
        return stateContainer.addToStateByFromState(fromState, toState, callBack);
    }

    public boolean removeFromState (String fromState) {
        return  stateContainer.removeFromState(fromState);
    }

    public boolean removeToStateByFromState (String fromState, String toState) {
        return stateContainer.removeToStateByFromState(fromState, toState);
    }

    public void setCurState (String state) {
        stateContainer.setCurState(state);
    }

    public String getCurState () {
        return stateContainer.getCurState();
    }

    public String nextState (String toState) {
        return stateContainer.nextState(toState);
    }

    public List<String> getStateList () {
        return stateContainer.getAllStates();
    }

    public Object getCallBackResult () {
        return stateContainer.getCallBackResult();
    }

}

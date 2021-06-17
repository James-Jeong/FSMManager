package state;

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

    public boolean addState (String state, CallBack callBack) {
        return stateContainer.addCallBackFromMapByState(state, callBack);
    }

    public boolean removeState (String state) {
        return  stateContainer.removeCallBackFromMapByState(state);
    }

    public void setCurState (String state) {
        stateContainer.setCurState(state);
    }

    public String getCurState () {
        return stateContainer.getCurState();
    }

    public String nextState (String state) {
        return stateContainer.nextState(state);
    }

}

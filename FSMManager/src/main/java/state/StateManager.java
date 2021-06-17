package state;

import event.StateEventManager;
import event.StateEventListener;

import java.util.List;
import java.util.Map;

/**
 * @class public class StateManager
 * @brief StateManager class
 */
public class StateManager {

    private static StateManager stateManager;

    private final StateContainer stateContainer;
    private final StateEventManager eventHandler;

    ////////////////////////////////////////////////////////////////////////////////

    public StateManager() {
        stateContainer = new StateContainer();
        eventHandler = new StateEventManager();

        eventHandler.setListener(new StateEventListener());
    }

    public static StateManager getInstance() {
        if (stateManager == null) {
            stateManager = new StateManager();
        }

        return stateManager;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public boolean addState (String event, String fromState, String toState, CallBack callBack) {
        eventHandler.addEvent(event, fromState, toState);
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

    public void fire (String event) {
        eventHandler.callEvent(event, getCurState());
    }

    public Map<String, String> findEvent (String event) {
        return eventHandler.getEventKeyMap(event);
    }

}

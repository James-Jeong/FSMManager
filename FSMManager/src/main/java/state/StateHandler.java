package state;

import event.StateEventManager;

import java.util.List;
import java.util.Map;

/**
 * @class public class StateHandler
 * @brief StateHandler class
 */
public class StateHandler {

    private final StateContainer stateContainer;
    private final StateEventManager stateEventManager;

    private final String name;

    public StateHandler(String name) {
       this.name = name;

        stateContainer = new StateContainer();
        stateEventManager = new StateEventManager();
    }

    public String getName() {
        return name;
    }

    public boolean addState (String event, String fromState, String toState, CallBack callBack) {
        stateEventManager.addEvent(event, fromState, toState);
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
        stateEventManager.callEvent(name, event, getCurState());
    }

    public Map<String, String> findEvent (String event) {
        return stateEventManager.getEventKeyMap(event);
    }


}

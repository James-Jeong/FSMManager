package state.basic.module.base;

import state.StateManager;
import state.basic.event.base.StateEvent;
import state.basic.unit.StateUnit;

/**
 * @class public abstract class EventCondition
 * @brief EventCondition class
 */
public abstract class EventCondition {

    private final StateManager stateManager;
    private final StateEvent stateEvent;
    private StateUnit curStateUnit;

    protected EventCondition(StateManager stateManager, StateEvent stateEvent) {
        this.stateManager = stateManager;
        this.stateEvent = stateEvent;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public boolean checkCondition() {
        // Must be implemented.
        return false;
    }

    public StateEvent getStateEvent() {
        return stateEvent;
    }

    public StateUnit getCurStateUnit() {
        return curStateUnit;
    }

    public void setCurStateUnit(StateUnit curStateUnit) {
        this.curStateUnit = curStateUnit;
    }
}

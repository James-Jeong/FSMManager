package state.basic.module.base;

import state.basic.event.base.StateEvent;
import state.basic.unit.StateUnit;

/**
 * @class public abstract class EventCondition
 * @brief EventCondition class
 */
public abstract class EventCondition {

    private final StateEvent stateEvent;
    private StateUnit curStateUnit;

    protected EventCondition(StateEvent stateEvent) {
        this.stateEvent = stateEvent;
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

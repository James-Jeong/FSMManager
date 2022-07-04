package base.basic.atm.base.condition;


import state.StateManager;
import state.basic.event.base.StateEvent;
import state.basic.module.base.EventCondition;

/**
 * @class public class InactiveStopEventCondition extends EventCondition
 * @brief InactiveStopEventCondition class
 */
public class RunFailEventCondition extends EventCondition {

    public RunFailEventCondition(StateManager stateManager, StateEvent stateEvent) {
        super(stateManager, stateEvent);
    }

    @Override
    public boolean checkCondition () {
        return true;
    }

}

package base.basic.call.base.condition;

import state.basic.event.base.StateEvent;
import state.basic.module.base.EventCondition;

/**
 * @class public class CallStopDoneSuccessEventCondition extends EventCondition
 * @brief CallStopDoneSuccessEventCondition class
 */
public class CallStopDoneSuccessEventCondition extends EventCondition {

    public CallStopDoneSuccessEventCondition(StateEvent stateEvent) {
        super(stateEvent);
    }

    @Override
    public boolean checkCondition () {
        return true;
    }

}

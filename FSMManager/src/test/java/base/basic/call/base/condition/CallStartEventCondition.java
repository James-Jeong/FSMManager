package base.basic.call.base.condition;

import state.basic.event.base.StateEvent;
import state.basic.module.base.EventCondition;

/**
 * @class public class CallStartEventCondition extends EventCondition
 * @brief CallStartEventCondition class
 */
public class CallStartEventCondition extends EventCondition {

    public CallStartEventCondition(StateEvent stateEvent) {
        super(stateEvent);
    }

    @Override
    public boolean checkCondition () {


        return true;
    }

}

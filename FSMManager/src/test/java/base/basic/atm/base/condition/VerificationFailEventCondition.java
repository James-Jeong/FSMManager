package base.basic.atm.base.condition;

import base.basic.atm.base.AtmAccount;
import state.basic.event.base.StateEvent;
import state.basic.module.base.EventCondition;
import state.basic.unit.StateUnit;

/**
 * @class public class InactiveStopEventCondition extends EventCondition
 * @brief InactiveStopEventCondition class
 */
public class VerificationFailEventCondition extends EventCondition {

    public VerificationFailEventCondition(StateEvent stateEvent) {
        super(stateEvent);
    }

    @Override
    public boolean checkCondition () {
        StateUnit stateUnit = getCurStateUnit();
        if (stateUnit == null) { return false; }

        AtmAccount atmAccount = (AtmAccount) stateUnit.getData();
        if (atmAccount == null) { return false; }

        return !atmAccount.isVerified() && atmAccount.isVerificationFailed();
    }

}

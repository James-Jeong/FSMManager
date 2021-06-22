package state.squirrel;

import org.squirrelframework.foundation.fsm.UntypedStateMachine;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;

/**
 * @class public class FsmContainer
 * @brief FsmContainer class
 */
public class FsmContainer {

    private final UntypedStateMachineBuilder untypedStateMachineBuilder;
    private UntypedStateMachine untypedStateMachine = null;

    public FsmContainer(UntypedStateMachineBuilder untypedStateMachineBuilder) {
        this.untypedStateMachineBuilder = untypedStateMachineBuilder;
    }

    public UntypedStateMachineBuilder getUntypedStateMachineBuilder() {
        return untypedStateMachineBuilder;
    }

    public UntypedStateMachine getUntypedStateMachine() {
        return untypedStateMachine;
    }

    public void setUntypedStateMachine(UntypedStateMachine untypedStateMachine) {
        this.untypedStateMachine = untypedStateMachine;
    }
}

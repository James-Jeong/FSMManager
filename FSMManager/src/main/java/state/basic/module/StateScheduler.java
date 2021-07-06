package state.basic.module;

import state.StateManager;
import state.basic.event.base.StateEvent;
import state.basic.module.base.AbstractStateTaskUnit;
import state.basic.unit.StateUnit;

import java.util.List;
import java.util.Map;

/**
 * @class public class StateScheduler
 * @brief StateScheduler class
 */
public class StateScheduler extends AbstractStateTaskUnit {

    private final StateHandler stateHandler;

    protected StateScheduler(StateHandler stateHandler, int delay) {
        super(delay);

        this.stateHandler = stateHandler;
    }

    @Override
    public void run() {
        StateManager stateManager = StateManager.getInstance();

        List<String> eventList = stateManager.getFailEventList();
        if (eventList.isEmpty()) { return; }

        Map<String, StateUnit> stateUnitMap = stateManager.getStateUnitMap();
        if (stateUnitMap.isEmpty()) { return; }

        for (String event : eventList) {
            StateEvent stateEvent = stateHandler.findStateEventByEvent(event);
            String fromState = stateEvent.getFromState();

            for (StateUnit stateUnit : stateUnitMap.values()) {
                if (stateUnit.getCurState().equals(fromState)) {
                    stateHandler.fire(
                            event,
                            stateUnit,
                            // TODO
                            (Object) null
                    );
                }
            }
        }
    }

}

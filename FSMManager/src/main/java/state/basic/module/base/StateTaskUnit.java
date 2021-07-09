package state.basic.module.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.module.StateHandler;
import state.basic.unit.StateUnit;

/**
 * @class public class StateTaskUnit extends AbstractStateTaskUnit
 * @brief StateTaskUnit class
 */
public class StateTaskUnit extends AbstractStateTaskUnit {

    private static final Logger logger = LoggerFactory.getLogger(StateTaskUnit.class);

    // StateHandler
    private final StateHandler stateHandler;
    // Event
    private final String event;
    // StateUnit
    private final StateUnit stateUnit;
    // Parameters for the event
    private final Object[] params;
    // Next event key
    private final String nextEventKey;

    ////////////////////////////////////////////////////////////////////////////////

    public StateTaskUnit(StateHandler stateHandler, String event, StateUnit stateUnit, int delay, Object... params) {
        super(delay);

        this.stateHandler = stateHandler;
        this.event = event;
        this.stateUnit = stateUnit;
        this.params = params;
        this.nextEventKey = stateUnit.getNextEventKey();
    }

    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public void run() {
        logger.info("({}) StateTaskUnit is started. (event={}, stateUnit={}, delay={})",
                stateHandler.getName(), event, stateUnit, getDelay()
        );

        stateHandler.fire(event, stateUnit, params);
        StateManager.getInstance().removeStateTaskUnit(stateHandler.getName(), nextEventKey);
    }

}

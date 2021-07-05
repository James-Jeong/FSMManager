package state.basic.module.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.module.StateHandler;
import state.basic.module.StateTaskManager;
import state.basic.unit.StateUnit;

/**
 * @class public class StateTaskUnit extends AbstractStateTaskUnit
 * @brief StateTaskUnit class
 */
public class StateTaskUnit extends AbstractStateTaskUnit {

    private static final Logger logger = LoggerFactory.getLogger(StateTaskUnit.class);

    private final StateHandler stateHandler;
    private final String event;
    private final StateUnit stateUnit;
    private final Object[] params;

    public StateTaskUnit(StateHandler stateHandler, String event, StateUnit stateUnit, int delay, Object... params) {
        super(delay);

        this.stateHandler = stateHandler;
        this.event = event;
        this.stateUnit = stateUnit;
        this.params = params;
    }

    @Override
    public void run() {
        logger.info("({}) StateTaskUnit is started. (event={}, stateUnit={}, delay={})",
                stateHandler.getName(), event, stateUnit, getDelay()
        );

        stateHandler.fire(event, stateUnit, params);
        StateTaskManager.getInstance().removeTask(stateHandler.getName(), stateUnit.getFailEventKey());
    }

}

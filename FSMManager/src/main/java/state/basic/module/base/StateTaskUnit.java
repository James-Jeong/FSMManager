package state.basic.module.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.module.StateHandler;
import state.basic.module.StateTaskManager;
import state.basic.unit.StateUnit;

/**
 * @class public class StateTaskUnit extends AbstractStateTaskUnit
 * @brief StateTaskUnit class
 */
public class StateTaskUnit extends AbstractStateTaskUnit {

    private static final Logger logger = LoggerFactory.getLogger(StateTaskUnit.class);

    private final String handlerName;
    private final String event;
    private final StateUnit stateUnit;

    public StateTaskUnit(String handlerName, String event, StateUnit stateUnit, int delay) {
        super(delay);

        this.handlerName = handlerName;
        this.event = event;
        this.stateUnit = stateUnit;
    }

    @Override
    public void run() {
        StateManager stateManager = StateManager.getInstance();
        StateHandler stateHandler = stateManager.getStateHandler(handlerName);
        if (stateHandler == null) {
            logger.warn("Not found the handler. (name={})", handlerName);
            return;
        }

        logger.info("({}) StateTaskUnit is started. (event={}, stateUnit={})", handlerName, event, stateUnit);
        stateHandler.fire(event, stateUnit, (Object) null);

        StateTaskManager.getInstance().removeTask(StateTaskUnit.class.getSimpleName());
    }

}

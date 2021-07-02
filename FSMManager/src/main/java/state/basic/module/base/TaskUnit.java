package state.basic.module.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.module.StateHandler;
import state.basic.module.TaskManager;
import state.basic.unit.StateUnit;

/**
 * @class public class TaskUnit extends AbstractTaskUnit
 * @brief TaskUnit class
 */
public class TaskUnit extends AbstractTaskUnit {

    private static final Logger logger = LoggerFactory.getLogger(TaskUnit.class);

    private final String handlerName;
    private final String event;
    private final StateUnit stateUnit;

    protected TaskUnit(String handlerName, String event, StateUnit stateUnit, int interval) {
        super(interval);

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

        // TODO : 실패 상태 없애야함
        stateHandler.fire(event, stateUnit, null, (Object) null);

        TaskManager.getInstance().removeTask(TaskUnit.class.getSimpleName());
    }

}

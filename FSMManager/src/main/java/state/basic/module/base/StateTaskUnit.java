package state.basic.module.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.module.StateHandler;
import state.basic.module.StateTaskManager;
import state.basic.module.retry.RetryManager;
import state.basic.module.retry.base.RetryStatus;
import state.basic.unit.StateUnit;

/**
 * @class public class StateTaskUnit extends AbstractStateTaskUnit
 * @brief StateTaskUnit class
 * 스케줄된 이벤트 클래스
 */
public class StateTaskUnit extends AbstractStateTaskUnit {

    private static final Logger logger = LoggerFactory.getLogger(StateTaskUnit.class);

    // Name
    private final String name;
    // StateHandler
    private final StateHandler stateHandler;
    // Event
    private final String event;
    // StateUnit
    private final StateUnit stateUnit;
    // Parameters for the event
    private final Object[] params;

    ////////////////////////////////////////////////////////////////////////////////

    public StateTaskUnit(String name,
                         StateHandler stateHandler,
                         String event,
                         StateUnit stateUnit,
                         int interval,
                         Object... params) {
        super(interval);

        this.name = name;
        this.stateHandler = stateHandler;
        this.event = event;
        this.stateUnit = stateUnit;
        this.params = params;
    }

    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public void run() {
        logger.info("({}) StateTaskUnit is started. (event={}, stateUnit={}, delay={})",
                stateHandler.getName(), event, stateUnit, getInterval()
        );

        StateTaskManager stateTaskManager = StateTaskManager.getInstance();

        // 1) 지정한 이벤트 실행
        stateHandler.fire(
                event,
                stateUnit,
                true,
                params
        );

        stateTaskManager.removeStateTaskUnit(stateHandler.getName(), name);

        // 2) 재시도 진행 중이면, 동일한 StateTaskUnit 정보로 StateTaskUnit 을 스케줄링한다.
        RetryManager retryManager = stateTaskManager.getRetryManager();
        RetryStatus retryStatus = retryManager.checkRetry(name);

        if (retryStatus == RetryStatus.ONGOING) {
            stateTaskManager.addStateTaskUnit(
                    stateHandler.getName(),
                    name,
                    new StateTaskUnit(
                            name,
                            stateHandler,
                            event,
                            stateUnit,
                            getInterval(),
                            params
                    ),
                    0
            );
        }
    }

}

package state.basic.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.module.base.StateTaskUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jamesj
 * @class public class StateTaskManager
 * @brief StateTaskManager class
 */
public class StateTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(StateTaskManager.class);

    private static StateTaskManager taskManager = null;

    private final ScheduledThreadPoolExecutor executor;

    private final Map<String, ScheduledFuture<?>> taskMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    public StateTaskManager() {
        int threadMaxCount;

        StateManager stateManager = StateManager.getInstance();
        int totalEventSize = stateManager.getTotalEventSize();
        if (totalEventSize <= 0) {
            threadMaxCount = 10;
        } else {
            threadMaxCount = totalEventSize;
        }

        logger.info("StateTaskManager thread max count: {}", threadMaxCount);
        executor = new ScheduledThreadPoolExecutor(threadMaxCount);
    }

    public static StateTaskManager getInstance ( ) {
        if (taskManager == null) {
            taskManager = new StateTaskManager();
        }

        return taskManager;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void stop ( ) {
        synchronized (taskMap) {
            for (ScheduledFuture<?> scheduledFuture : taskMap.values()) {
                scheduledFuture.cancel(true);
            }
        }

        executor.shutdown();
        logger.info("StateTaskManager ends.");
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addTask (String name, StateTaskUnit stateTaskUnit)
     * @brief StateTaskManager 에 새로운 StateTaskUnit 를 등록하는 함수
     */
    public void addTask (String name, StateTaskUnit stateTaskUnit) {
        if (name == null || stateTaskUnit == null) { return; }

        synchronized (taskMap) {
            if (taskMap.get(name) != null) {
                logger.warn("Hashmap Key duplication error. (name={})", name);
                return;
            }

            ScheduledFuture<?> scheduledFuture = null;
            try {
                scheduledFuture = executor.scheduleAtFixedRate(
                        stateTaskUnit,
                        stateTaskUnit.getDelay(),
                        stateTaskUnit.getDelay(),
                        TimeUnit.MILLISECONDS
                );
            } catch (Exception e) {
                logger.warn("StateTaskManager.addTask.Exception", e);
            }

            if (taskMap.put(name, scheduledFuture) == null) {
                logger.info("StateTaskUnit [{}] is added.", name);
            }
        }
    }

    /**
     * @fn public ScheduledFuture<?> findTask (String name)
     * @brief 지정한 이름의 StateTaskUnit 를 반환하는 함수
     * @param name StateTaskUnit 이름
     * @return 성공 시 StateTaskUnit, 실패 시 null 반환
     */
    public ScheduledFuture<?> findTask (String name) {
        if (name == null) { return null; }

        synchronized (taskMap) {
            if (taskMap.isEmpty()) {
                return null;
            }

            return taskMap.get(name);
        }
    }

    /**
     * @fn public void removeTask (String name)
     * @brief 지정한 이름의 StateTaskUnit 를 삭제하는 함수
     * @param name StateTaskUnit 이름
     */
    public void removeTask (String name) {
        if (name == null) { return; }

        synchronized (taskMap) {
            if (taskMap.isEmpty()) {
                return;
            }

            ScheduledFuture<?> scheduledFuture = findTask(name);
            if (scheduledFuture == null) {
                logger.warn("Fail to find the StateTaskUnit. (name={})", name);
                return;
            }

            try {
                scheduledFuture.cancel(true);

                if (taskMap.remove(name) != null) {
                    logger.info("StateTaskUnit [{}] is removed.", name);
                }
            } catch (Exception e) {
                logger.warn("StateTaskManager.addTask.Exception", e);
            }
        }
    }

}

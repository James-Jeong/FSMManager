package state.basic.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.module.base.StateTaskUnit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    public StateTaskManager() {
        executor = new ScheduledThreadPoolExecutor(10);
    }

    public static StateTaskManager getInstance ( ) {
        if (taskManager == null) {
            taskManager = new StateTaskManager();
        }

        return taskManager;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void stop ( ) {
        for (ScheduledFuture<?> scheduledFuture : taskMap.values()) {
            scheduledFuture.cancel(true);
        }

        executor.shutdown();
        logger.info("() () () Interval Task Manager ends.");
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addJob (String name, TaskUnit runner)
     * @brief TaskManager 에 새로운 Task 를 등록하는 함수
     */
    public void addTask (String name, StateTaskUnit taskUnit) {
        if (taskMap.get(name) != null) {
            logger.warn("() () () TaskManager: Hashmap Key duplication error.");
            return;
        }

        ScheduledFuture<?> scheduledFuture = null;
        try {
            scheduledFuture = executor.scheduleAtFixedRate(
                    taskUnit,
                    0,
                    taskUnit.getDelay(),
                    TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            logger.warn("() () () TaskManager.addTask.Exception", e);
        }

        logger.info("() () () [{}] is added.", name);
        taskMap.put(name, scheduledFuture);
    }

    public ScheduledFuture<?> findTask (String name) {
        if (taskMap.isEmpty()) {
            return null;
        }

        return taskMap.get(name);
    }

    public void removeTask (String name) {
        if (taskMap.isEmpty()) {
            return;
        }

        ScheduledFuture<?> scheduledFuture = findTask(name);
        if (scheduledFuture == null) {
            return;
        }

        try {
            scheduledFuture.cancel(true);

            taskMap.remove(name);
            logger.info("() () () [{}] is removed.", name);
        } catch (Exception e) {
            logger.warn("() () () TaskManager.addTask.Exception", e);
        }
    }

}

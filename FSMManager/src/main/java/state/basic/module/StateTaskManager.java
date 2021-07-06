package state.basic.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.info.ResultCode;
import state.basic.module.base.StateTaskUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jamesj
 * @class public class StateTaskManager
 * @brief StateTaskManager class
 */
public class StateTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(StateTaskManager.class);

    // StateTaskManager singleton object
    private static StateTaskManager taskManager = null;

    // StateScheduler Map
    private final Map<String, ScheduledThreadPoolExecutor> stateSchedulerMap = new HashMap<>();

    // ScheduledThreadPoolExecutor Map
    private final Map<String, ScheduledThreadPoolExecutor> taskMap = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////

    public StateTaskManager() {
        // Nothing
    }

    public static StateTaskManager getInstance ( ) {
        if (taskManager == null) {
            taskManager = new StateTaskManager();
        }

        return taskManager;
    }

    /**
     * @fn public void addStateScheduler (String handlerName, int delay)
     * @brief StateTaskManager 에 새로운 StateScheduler 를 등록하는 함수
     */
    public void addStateScheduler(StateHandler stateHandler, int delay) {
        if (stateHandler == null || delay < 0) { return; }

        String handlerName = stateHandler.getName();
        synchronized (stateSchedulerMap) {
            if (stateSchedulerMap.get(handlerName) != null) {
                logger.warn("[{}] ({}) StateScheduler Hashmap Key duplication error.",
                        ResultCode.DUPLICATED_KEY, handlerName
                );
                return;
            }

            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = null;
            try {
                StateScheduler stateScheduler = new StateScheduler(stateHandler, delay);

                scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
                scheduledThreadPoolExecutor.scheduleAtFixedRate(
                        stateScheduler,
                        0,
                        stateScheduler.getDelay(),
                        TimeUnit.MILLISECONDS
                );
            } catch (Exception e) {
                logger.warn("[{}] ({}) StateTaskManager.startScheduler.Exception",
                        ResultCode.FAIL_ADD_STATE_TASK_UNIT, stateHandler.getName(), e
                );
            }

            if (scheduledThreadPoolExecutor != null &&
                    stateSchedulerMap.put(handlerName, scheduledThreadPoolExecutor) == null) {
                logger.info("[{}] ({}) StateScheduler is added.",
                        ResultCode.SUCCESS_ADD_STATE_TASK_UNIT, handlerName
                );
            }
        }

    }

    /**
     * @fn private ScheduledThreadPoolExecutor findStateScheduler (String handlerName)
     * @brief 지정한 이름의 StateScheduler 를 반환하는 함수
     * @param handlerName handlerName
     * @return 성공 시 StateScheduler, 실패 시 null 반환
     */
    private ScheduledThreadPoolExecutor findStateScheduler (String handlerName) {
        if (handlerName == null) { return null; }

        synchronized (stateSchedulerMap) {
            if (stateSchedulerMap.isEmpty()) {
                return null;
            }

            return stateSchedulerMap.get(handlerName);
        }
    }

    /**
     * @fn public void removeStateScheduler (String handlerName)
     * @brief 지정한 이름의 StateScheduler 를 삭제하는 함수
     * @param handlerName StateHandler 이름
     */
    public void removeStateScheduler(String handlerName) {
        if (handlerName == null) { return; }

        synchronized (stateSchedulerMap) {
            if (stateSchedulerMap.isEmpty()) {
                return;
            }

            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = findStateScheduler(handlerName);
            if (scheduledThreadPoolExecutor == null) {
                logger.warn("[{}] ({}) Fail to find the StateScheduler.",
                        ResultCode.FAIL_GET_STATE_TASK_UNIT, handlerName
                );
                return;
            }

            try {
                scheduledThreadPoolExecutor.shutdown();

                if (stateSchedulerMap.remove(handlerName) != null) {
                    logger.info("[{}] ({}) StateScheduler is removed.",
                            ResultCode.SUCCESS_REMOVE_STATE_TASK_UNIT, handlerName
                    );
                }
            } catch (Exception e) {
                logger.warn("[{}] ({}) StateTaskManager.stopStateScheduler.Exception",
                        ResultCode.FAIL_REMOVE_STATE_TASK_UNIT, handlerName, e
                );
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addTask (String handlerName, String name, StateTaskUnit stateTaskUnit)
     * @brief StateTaskManager 에 새로운 StateTaskUnit 를 등록하는 함수
     */
    public void addTask (String handlerName, String name, StateTaskUnit stateTaskUnit) {
        if (name == null || stateTaskUnit == null) { return; }

        synchronized (taskMap) {
            if (taskMap.get(name) != null) {
                logger.warn("[{}] ({}) StateTaskUnit Hashmap Key duplication error. (name={})",
                        ResultCode.DUPLICATED_KEY, handlerName, name
                );
                return;
            }

            ScheduledThreadPoolExecutor executor = null;
            try {
                executor = new ScheduledThreadPoolExecutor(1);
                executor.scheduleAtFixedRate(
                        stateTaskUnit,
                        stateTaskUnit.getDelay(),
                        stateTaskUnit.getDelay(),
                        TimeUnit.MILLISECONDS
                );
            } catch (Exception e) {
                logger.warn("[{}] ({}) StateTaskManager.addTask.Exception",
                        ResultCode.FAIL_ADD_STATE_TASK_UNIT, handlerName, e
                );
            }

            if (executor != null &&
                    taskMap.put(name, executor) == null) {
                logger.info("[{}] ({}) StateTaskUnit [{}] is added.",
                        ResultCode.SUCCESS_ADD_STATE_TASK_UNIT, handlerName, name
                );
            }
        }
    }

    /**
     * @fn private ScheduledThreadPoolExecutor findTask (String name)
     * @brief 지정한 이름의 StateTaskUnit 를 반환하는 함수
     * @param name StateTaskUnit 이름
     * @return 성공 시 StateTaskUnit, 실패 시 null 반환
     */
    private ScheduledThreadPoolExecutor findTask (String name) {
        if (name == null) { return null; }

        synchronized (taskMap) {
            if (taskMap.isEmpty()) {
                return null;
            }

            return taskMap.get(name);
        }
    }

    /**
     * @fn public void removeTask (String handlerName, String name)
     * @brief 지정한 이름의 StateTaskUnit 를 삭제하는 함수
     * @param handlerName StateHandler 이름
     * @param name StateTaskUnit 이름
     */
    public void removeTask (String handlerName, String name) {
        if (name == null) { return; }

        synchronized (taskMap) {
            if (taskMap.isEmpty()) {
                return;
            }

            ScheduledThreadPoolExecutor executor = findTask(name);
            if (executor == null) {
                logger.warn("[{}] ({}) Fail to find the StateTaskUnit. (name={})",
                        ResultCode.FAIL_GET_STATE_TASK_UNIT, handlerName, name
                );
                return;
            }

            try {
                executor.shutdown();

                if (taskMap.remove(name) != null) {
                    logger.info("[{}] ({}) StateTaskUnit [{}] is removed.",
                            ResultCode.SUCCESS_REMOVE_STATE_TASK_UNIT, handlerName, name
                    );
                }
            } catch (Exception e) {
                logger.warn("[{}] ({}) StateTaskManager.removeTask.Exception",
                        ResultCode.FAIL_REMOVE_STATE_TASK_UNIT, handlerName, e
                );
            }
        }
    }

}

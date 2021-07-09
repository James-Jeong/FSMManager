package state.basic.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.info.ResultCode;
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

    // StateScheduler Map
    private final Map<String, ScheduledThreadPoolExecutor> stateSchedulerMap = new HashMap<>();

    // ScheduledThreadPoolExecutor Map
    private final Map<String, ScheduledFuture<?>> stateTaskUnitMap = new HashMap<>();

    private final ScheduledThreadPoolExecutor executor;

    ////////////////////////////////////////////////////////////////////////////////

    public StateTaskManager(int threadMaxSize) {
        executor = new ScheduledThreadPoolExecutor(threadMaxSize);
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
     * @fn public void addStateTaskUnit (String handlerName, String name, StateTaskUnit stateTaskUnit)
     * @brief StateTaskManager 에 새로운 StateTaskUnit 를 등록하는 함수
     */
    public void addStateTaskUnit(String handlerName, String name, StateTaskUnit stateTaskUnit) {
        if (name == null || stateTaskUnit == null) { return; }

        synchronized (stateTaskUnitMap) {
            if (stateTaskUnitMap.get(name) != null) {
                logger.warn("[{}] ({}) StateTaskUnit Hashmap Key duplication error. (name={})",
                        ResultCode.DUPLICATED_KEY, handlerName, name
                );
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
                logger.warn("[{}] ({}) StateTaskManager.addTask.Exception",
                        ResultCode.FAIL_ADD_STATE_TASK_UNIT, handlerName, e
                );
            }

            if (stateTaskUnitMap.put(name, scheduledFuture) == null) {
                logger.info("[{}] ({}) StateTaskUnit [{}] is added.",
                        ResultCode.SUCCESS_ADD_STATE_TASK_UNIT, handlerName, name
                );
            }
        }
    }

    /**
     * @fn private ScheduledFuture<?> findTask (String name)
     * @brief 지정한 이름의 StateTaskUnit 를 반환하는 함수
     * @param name StateTaskUnit 이름
     * @return 성공 시 StateTaskUnit, 실패 시 null 반환
     */
    private ScheduledFuture<?> findTask (String name) {
        if (name == null) { return null; }

        synchronized (stateTaskUnitMap) {
            if (stateTaskUnitMap.isEmpty()) {
                return null;
            }

            return stateTaskUnitMap.get(name);
        }
    }

    /**
     * @fn public void removeStateTaskUnit (String handlerName, String name)
     * @brief 지정한 이름의 StateTaskUnit 를 삭제하는 함수
     * @param handlerName StateHandler 이름
     * @param stateTaskUnitName StateTaskUnit 이름
     */
    public void removeStateTaskUnit(String handlerName, String stateTaskUnitName) {
        if (stateTaskUnitName == null) { return; }

        synchronized (stateTaskUnitMap) {
            if (stateTaskUnitMap.isEmpty()) {
                return;
            }

            ScheduledFuture<?> scheduledFuture = findTask(stateTaskUnitName);
            if (scheduledFuture == null) {
                logger.warn("[{}] ({}) Fail to find the StateTaskUnit. (name={})",
                        ResultCode.FAIL_GET_STATE_TASK_UNIT, handlerName, stateTaskUnitName
                );
                return;
            }

            try {
                scheduledFuture.cancel(true);

                if (stateTaskUnitMap.remove(stateTaskUnitName) != null) {
                    logger.info("[{}] ({}) StateTaskUnit [{}] is removed.",
                            ResultCode.SUCCESS_REMOVE_STATE_TASK_UNIT, handlerName, stateTaskUnitName
                    );
                }
            } catch (Exception e) {
                logger.warn("[{}] ({}) StateTaskManager.removeTask.Exception",
                        ResultCode.FAIL_REMOVE_STATE_TASK_UNIT, handlerName, e
                );
            }
        }
    }

    public void stop ( ) {
        for (ScheduledFuture<?> scheduledFuture : stateTaskUnitMap.values()) {
            scheduledFuture.cancel(true);
        }

        executor.shutdown();
        logger.info("() () () Interval Task Manager ends.");
    }

}

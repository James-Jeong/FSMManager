package state.basic.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.event.retry.RetryManager;
import state.basic.info.ResultCode;
import state.basic.module.base.StateTaskUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jamesj
 * @class public class StateTaskManager
 * @brief StateTaskManager class
 * 이벤트 스케줄링 클래스
 */
public class StateTaskManager {

    private static final Logger logger = LoggerFactory.getLogger(StateTaskManager.class);

    private static StateTaskManager stateTaskManager;

    private final ScheduledThreadPoolExecutor executor;

    // StateScheduler Map
    private final Map<String, ScheduledThreadPoolExecutor> stateSchedulerMap = new HashMap<>();
    private final ReentrantLock stateSchedulerLock = new ReentrantLock();

    // ScheduledThreadPoolExecutor Map
    private final Map<String, ScheduledFuture<?>> stateTaskUnitMap = new HashMap<>();
    private final ReentrantLock stateTaskLock = new ReentrantLock();

    // RetryManager
    private final RetryManager retryManager = new RetryManager();

    ////////////////////////////////////////////////////////////////////////////////

    public StateTaskManager(int threadMaxSize) {
        executor = new ScheduledThreadPoolExecutor(threadMaxSize);
    }

    public static StateTaskManager getInstance() {
        if (stateTaskManager == null) {
            stateTaskManager = new StateTaskManager(StateManager.getInstance().getTaskThreadMaxCount());
        }

        return stateTaskManager;
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addStateScheduler (String handlerName, int delay)
     * @brief StateTaskManager 에 새로운 StateScheduler 를 등록하는 함수
     */
    public void addStateScheduler(StateHandler stateHandler, int delay) {
        if (stateHandler == null || delay < 0) { return; }

        try {
            stateSchedulerLock.lock();

            String handlerName = stateHandler.getName();
            if (stateSchedulerMap.get(handlerName) != null) {
                logger.warn("[{}] ({}) StateScheduler Hashmap Key duplication error.",
                        ResultCode.DUPLICATED_KEY, handlerName
                );
            } else {
                StateScheduler stateScheduler = new StateScheduler(stateHandler, delay);
                ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
                scheduledThreadPoolExecutor.scheduleAtFixedRate(
                        stateScheduler,
                        0,
                        stateScheduler.getInterval(),
                        TimeUnit.MILLISECONDS
                );

                if (stateSchedulerMap.put(handlerName, scheduledThreadPoolExecutor) == null) {
                    logger.info("[{}] ({}) StateScheduler is added.",
                            ResultCode.SUCCESS_ADD_STATE_TASK_UNIT, handlerName
                    );
                }
            }
        } catch (Exception e) {
            logger.warn("[{}] ({}) StateTaskManager.startScheduler.Exception",
                    ResultCode.FAIL_ADD_STATE_TASK_UNIT, stateHandler.getName(), e
            );
        } finally {
            stateSchedulerLock.unlock();
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

        try {
            stateSchedulerLock.lock();

            if (!stateSchedulerMap.isEmpty()) {
                return stateSchedulerMap.get(handlerName);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.warn("[{}] ({}) StateTaskManager.findStateScheduler.Exception",
                    ResultCode.FAIL_GET_STATE_TASK_UNIT, handlerName, e
            );
            return null;
        } finally {
            stateSchedulerLock.unlock();
        }
    }

    /**
     * @fn public void removeStateScheduler (String handlerName)
     * @brief 지정한 이름의 StateScheduler 를 삭제하는 함수
     * @param handlerName StateHandler 이름
     */
    public void removeStateScheduler(String handlerName) {
        if (handlerName == null) { return; }

        try {
            stateSchedulerLock.lock();

            if (!stateSchedulerMap.isEmpty()) {
                ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = findStateScheduler(handlerName);
                if (scheduledThreadPoolExecutor == null) {
                    logger.warn("[{}] ({}) Fail to find the StateScheduler.",
                            ResultCode.FAIL_GET_STATE_TASK_UNIT, handlerName
                    );
                } else {
                    scheduledThreadPoolExecutor.shutdown();

                    if (stateSchedulerMap.remove(handlerName) != null) {
                        logger.info("[{}] ({}) StateScheduler is removed.",
                                ResultCode.SUCCESS_REMOVE_STATE_TASK_UNIT, handlerName
                        );
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("[{}] ({}) StateTaskManager.stopStateScheduler.Exception",
                    ResultCode.FAIL_REMOVE_STATE_TASK_UNIT, handlerName, e
            );
        } finally {
            stateSchedulerLock.unlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addStateTaskUnit (String handlerName, String name, StateTaskUnit stateTaskUnit)
     * @brief StateTaskManager 에 새로운 StateTaskUnit 를 등록하는 함수
     */
    public void addStateTaskUnit(String handlerName, String stateTaskUnitName, StateTaskUnit stateTaskUnit, int retryCount) {
        if (stateTaskUnitName == null || stateTaskUnit == null) { return; }

        try {
            stateTaskLock.lock();

            if (stateTaskUnitMap.get(stateTaskUnitName) != null) {
                logger.warn("[{}] ({}) StateTaskUnit Hashmap Key duplication error. (name={})",
                        ResultCode.DUPLICATED_KEY, handlerName, stateTaskUnitName
                );
            } else {
                ScheduledFuture<?> scheduledFuture = executor.scheduleAtFixedRate(
                        stateTaskUnit,
                        stateTaskUnit.getInterval(),
                        stateTaskUnit.getInterval(),
                        TimeUnit.MILLISECONDS
                );

                if (stateTaskUnitMap.put(stateTaskUnitName, scheduledFuture) == null) {
                    logger.info("[{}] ({}) StateTaskUnit [{}] is added.",
                            ResultCode.SUCCESS_ADD_STATE_TASK_UNIT, handlerName, stateTaskUnitName
                    );
                }

                if (retryCount > 0) {
                    retryManager.addRetryUnit(stateTaskUnitName, retryCount, 0);
                }
            }
        } catch (Exception e) {
            logger.warn("[{}] ({}) StateTaskManager.addTask.Exception",
                    ResultCode.FAIL_ADD_STATE_TASK_UNIT, handlerName, e
            );
        } finally {
            stateTaskLock.unlock();
        }
    }

    /**
     * @fn private ScheduledFuture<?> findTask (String name)
     * @brief 지정한 이름의 StateTaskUnit 를 반환하는 함수
     * @param stateTaskUnitName StateTaskUnit 이름
     * @return 성공 시 StateTaskUnit, 실패 시 null 반환
     */
    private ScheduledFuture<?> findTask (String stateTaskUnitName) {
        if (stateTaskUnitName == null) { return null; }

        try {
            stateTaskLock.lock();

            if (!stateTaskUnitMap.isEmpty()) {
                return stateTaskUnitMap.get(stateTaskUnitName);
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.warn("[{}] StateTaskManager.findTask.Exception (name={})",
                    ResultCode.FAIL_GET_STATE_TASK_UNIT, stateTaskUnitName, e
            );
            return null;
        } finally {
            stateTaskLock.unlock();
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

        try {
            stateTaskLock.lock();

            if (!stateTaskUnitMap.isEmpty()) {
                ScheduledFuture<?> scheduledFuture = findTask(stateTaskUnitName);
                if (scheduledFuture == null) {
                    logger.warn("[{}] ({}) Fail to find the StateTaskUnit. (name={})",
                            ResultCode.FAIL_GET_STATE_TASK_UNIT, handlerName, stateTaskUnitName
                    );
                } else {
                    scheduledFuture.cancel(true);

                    if (stateTaskUnitMap.remove(stateTaskUnitName) != null) {
                        logger.info("[{}] ({}) StateTaskUnit [{}] is removed.",
                                ResultCode.SUCCESS_REMOVE_STATE_TASK_UNIT, handlerName, stateTaskUnitName
                        );
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("[{}] ({}) StateTaskManager.removeTask.Exception",
                    ResultCode.FAIL_REMOVE_STATE_TASK_UNIT, handlerName, e
            );
        } finally {
            stateTaskLock.unlock();
        }
    }

    public void stop ( ) {
        for (ScheduledFuture<?> scheduledFuture : stateTaskUnitMap.values()) {
            scheduledFuture.cancel(true);
        }

        executor.shutdown();
        logger.info("() () () Interval Task Manager ends.");
    }

    ////////////////////////////////////////////////////////////////////////////////

    public RetryManager getRetryManager() {
        return retryManager;
    }

}

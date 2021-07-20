package base.basic.media;

import base.basic.atm.base.TestUtil;
import base.basic.base.SessionManager;
import base.basic.call.base.CallInfo;
import base.basic.media.base.MediaEvent;
import base.basic.media.base.MediaState;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.module.StateHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @class public class AmfMediaStateTest
 * @brief AmfMediaStateTest class
 */
public class BasicMediaStateTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicMediaStateTest.class);

    private final StopWatch stopWatch = new StopWatch();

    private final StateManager stateManager = StateManager.getInstance();

    ////////////////////////////////////////////////////////////////////////////////

    public void testStart () {
        SessionManager sessionManager = SessionManager.getInstance();

        sessionManager.createCall("Call1", "01012345678", "01056781234");
        CallInfo callInfo1 = sessionManager.getCall("Call1");
        normalTest(callInfo1);

        sessionManager.createCall("Call2", "01056781234", "01012345678");
        CallInfo callInfo2 = sessionManager.getCall("Call2");
        //timingTest(callInfo2);

        sessionManager.removeCall("Call1");
        sessionManager.removeCall("Call2");
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String mediaStart (CallInfo callInfo) {
        //logger.info("@ Media is started!");
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);

        return mediaStateHandler.fire(
                MediaEvent.MEDIA_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                false,
                MediaState.ACTIVE_REQUEST
        );
    }

    public String mediaStop (CallInfo callInfo) {
        //logger.info("@ Media is stopped!");
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);

        return mediaStateHandler.fire(
                MediaEvent.MEDIA_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                false,
                MediaState.IDLE_REQUEST
        );
    }

    public String mediaCreateSuccess (CallInfo callInfo) {
        //logger.info("@ Success to create media!");
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);

        return mediaStateHandler.fire(
                MediaEvent.MEDIA_CREATE_SUCCESS_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                false,
                MediaState.ACTIVE_STATE
        );
    }

    public String mediaCreateFail (CallInfo callInfo) {
        //logger.info("@ Fail to create media!");
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);

        return mediaStateHandler.fire(
                MediaEvent.MEDIA_CREATE_FAIL_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                false,
                MediaState.IDLE_STATE
        );
    }

    public String mediaDeleteSuccess (CallInfo callInfo) {
        //logger.info("@ Success to delete media!");
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);

        return mediaStateHandler.fire(
                MediaEvent.MEDIA_DELETE_SUCCESS_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                false,
                MediaState.IDLE_STATE
        );
    }

    public String mediaDeleteFail (CallInfo callInfo) {
        //logger.info("@ Fail to delete media!");
        StateHandler mediaStateHandler = stateManager.getStateHandler(MediaState.MEDIA_STATE_NAME);

        return mediaStateHandler.fire(
                MediaEvent.MEDIA_DELETE_FAIL_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                false,
                MediaState.ACTIVE_STATE
        );
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest (CallInfo callInfo) {
        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        this.stopWatch.reset();
        this.stopWatch.start();

        Assert.assertEquals(MediaState.ACTIVE_REQUEST, mediaStart(callInfo));
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getSuccessCallBackResult());

        // 상태 처리 실패 시 반환될 실패 상태값 정상 동작하는지 확인
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, mediaStop(callInfo));
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getSuccessCallBackResult());

        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaCreateSuccess(callInfo));
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getSuccessCallBackResult());

        Assert.assertEquals(MediaState.IDLE_REQUEST, mediaStop(callInfo));
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getSuccessCallBackResult());

        Assert.assertEquals(MediaState.IDLE_STATE, mediaDeleteSuccess(callInfo));
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getSuccessCallBackResult());

        this.stopWatch.stop();
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }

    public void timingTest(CallInfo callInfo) {
        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(100);

        int totalTaskCount;

        int initDelayMs = 100;
        int delayMs = 1;

        // mediaStart
        for (totalTaskCount = 0; totalTaskCount < 1; totalTaskCount++) {
            ScheduledFuture<?> scheduledFuture;
            try {
                scheduledFuture = executor.scheduleAtFixedRate(
                        () -> mediaStart(callInfo),
                        initDelayMs,
                        delayMs,
                        TimeUnit.MILLISECONDS
                );
                logger.info("mediaStart is scheduled.");
                taskMap.putIfAbsent(String.valueOf(totalTaskCount), scheduledFuture);
            } catch (Exception e) {
                logger.warn("Schedule Exception", e);
            }
        }

        // mediaCreateSuccess
        for (totalTaskCount = 0; totalTaskCount < 1; totalTaskCount++) {
            ScheduledFuture<?> scheduledFuture;
            try {
                scheduledFuture = executor.scheduleAtFixedRate(
                        () -> mediaCreateSuccess(callInfo),
                        initDelayMs,
                        delayMs,
                        TimeUnit.MILLISECONDS
                );
                logger.info("mediaCreateSuccess is scheduled.");
                taskMap.putIfAbsent(String.valueOf(totalTaskCount), scheduledFuture);
            } catch (Exception e) {
                logger.warn("Schedule Exception", e);
            }
        }

        // mediaStop
        for (totalTaskCount = 0; totalTaskCount < 1; totalTaskCount++) {
            ScheduledFuture<?> scheduledFuture;
            try {
                scheduledFuture = executor.scheduleAtFixedRate(
                        () -> mediaStop(callInfo),
                        initDelayMs,
                        delayMs,
                        TimeUnit.MILLISECONDS
                );
                logger.info("mediaStop is scheduled.");
                taskMap.putIfAbsent(String.valueOf(totalTaskCount), scheduledFuture);
            } catch (Exception e) {
                logger.warn("Schedule Exception", e);
            }
        }

        // mediaDeleteSuccess
        for (totalTaskCount = 0; totalTaskCount < 1; totalTaskCount++) {
            ScheduledFuture<?> scheduledFuture;
            try {
                scheduledFuture = executor.scheduleAtFixedRate(
                        () -> mediaDeleteSuccess(callInfo),
                        initDelayMs,
                        delayMs,
                        TimeUnit.MILLISECONDS
                );
                logger.info("mediaDeleteSuccess is scheduled.");
                taskMap.putIfAbsent(String.valueOf(totalTaskCount), scheduledFuture);
            } catch (Exception e) {
                logger.warn("Schedule Exception", e);
            }
        }

        TestUtil.sleep(2000);

        for (ScheduledFuture<?> scheduledFuture : taskMap.values()) {
            scheduledFuture.cancel(true);
        }

        executor.shutdown();
        ////////////////////////////////////////////////////////////////////////////////
    }

}

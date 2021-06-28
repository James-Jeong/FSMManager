package base.basic.media;

import base.basic.call.base.CallInfo;
import base.basic.media.base.MediaCallBack;
import base.basic.media.base.MediaState;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.state.StateHandler;

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

    private static final String MEDIA_START_EVENT = "media_start_success";
    private static final String MEDIA_STOP_EVENT = "media_stop_success";
    private static final String MEDIA_CREATE_SUCCESS_EVENT = "media_create_success";
    private static final String MEDIA_CREATE_FAIL_EVENT = "media_create_fail";
    private static final String MEDIA_DELETE_SUCCESS_EVENT = "media_delete_success";
    private static final String MEDIA_DELETE_FAIL_EVENT = "media_delete_fail";

    ////////////////////////////////////////////////////////////////////////////////

    private static final String MEDIA_STATE_NAME = "media_state";
    private final StateManager stateManager = StateManager.getInstance();
    private StateHandler mediaStateHandler = null;

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        String callId = "Call";
        CallInfo callInfo = new CallInfo(
                callId,
                "01012345678",
                "01056781234"
        );

        stateManager.addStateUnit(callInfo.getMediaStateUnitName(), MediaState.IDLE_STATE);
        stateManager.addStateHandler(MEDIA_STATE_NAME);
        mediaStateHandler = stateManager.getStateHandler(MEDIA_STATE_NAME);

        ////////////////////////////////////////////////////////////////////////////////
        // 1. CallBack 함수 정의
        MediaCallBack mediaStartCallBack = new MediaCallBack(MEDIA_START_EVENT);
        MediaCallBack mediaCreateSuccessCallBack = new MediaCallBack(MEDIA_CREATE_SUCCESS_EVENT);
        MediaCallBack mediaCreateFailCallBack = new MediaCallBack(MEDIA_CREATE_FAIL_EVENT);
        MediaCallBack mediaStopCallBack = new MediaCallBack(MEDIA_STOP_EVENT);
        MediaCallBack mediaDeleteSuccessCallBack = new MediaCallBack(MEDIA_DELETE_SUCCESS_EVENT);
        MediaCallBack mediaDeleteFailCallBack = new MediaCallBack(MEDIA_DELETE_FAIL_EVENT);
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 2. 상태 정의
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_START_EVENT, MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST, mediaStartCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_CREATE_SUCCESS_EVENT, MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE, mediaCreateSuccessCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_CREATE_FAIL_EVENT, MediaState.ACTIVE_REQUEST, MediaState.IDLE_STATE, mediaCreateFailCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_STOP_EVENT, MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST, mediaStopCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_DELETE_SUCCESS_EVENT, MediaState.IDLE_REQUEST, MediaState.IDLE_STATE, mediaDeleteSuccessCallBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_DELETE_FAIL_EVENT, MediaState.IDLE_REQUEST, MediaState.ACTIVE_STATE, mediaDeleteFailCallBack));

        Assert.assertFalse(mediaStateHandler.getEventList().isEmpty());
        Assert.assertFalse(mediaStateHandler.getStateList().isEmpty());
        ////////////////////////////////////////////////////////////////////////////////

        normalTest(callInfo);
        timingTest(callInfo);

        stateManager.removeStateUnit(callInfo.getMediaStateUnitName());
        stateManager.removeStateHandler(MEDIA_STATE_NAME);
        mediaStateHandler = null;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String mediaStart (CallInfo callInfo) {
        //logger.info("@ Media is started!");
        return mediaStateHandler.fire(
                MEDIA_START_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                MediaState.IDLE_STATE
        );
    }

    public String mediaStop (CallInfo callInfo) {
        //logger.info("@ Media is stopped!");
        return mediaStateHandler.fire(
                MEDIA_STOP_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                MediaState.ACTIVE_STATE
        );
    }

    public String mediaCreateSuccess (CallInfo callInfo) {
        //logger.info("@ Success to create media!");
        return mediaStateHandler.fire(
                MEDIA_CREATE_SUCCESS_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                MediaState.ACTIVE_REQUEST
        );
    }

    public String mediaCreateFail (CallInfo callInfo) {
        //logger.info("@ Fail to create media!");
        return mediaStateHandler.fire(MEDIA_CREATE_FAIL_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                MediaState.ACTIVE_REQUEST
        );
    }

    public String mediaDeleteSuccess (CallInfo callInfo) {
        //logger.info("@ Success to delete media!");
        return mediaStateHandler.fire(
                MEDIA_DELETE_SUCCESS_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                MediaState.IDLE_REQUEST
        );
    }

    public String mediaDeleteFail (CallInfo callInfo) {
        //logger.info("@ Fail to delete media!");
        return mediaStateHandler.fire(
                MEDIA_DELETE_FAIL_EVENT,
                StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()),
                MediaState.IDLE_REQUEST);
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
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

        // 상태 처리 실패 시 반환될 실패 상태값 정상 동작하는지 확인
        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaStop(callInfo));
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaCreateSuccess(callInfo));
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

        Assert.assertEquals(MediaState.IDLE_REQUEST, mediaStop(callInfo));
        Assert.assertEquals(MediaState.ACTIVE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

        Assert.assertEquals(MediaState.IDLE_STATE, mediaDeleteSuccess(callInfo));
        Assert.assertEquals(MediaState.IDLE_REQUEST, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getPrevState());
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCurState());
        Assert.assertEquals(MediaState.IDLE_STATE, StateManager.getInstance().getStateUnit(callInfo.getMediaStateUnitName()).getCallBackResult());

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
                logger.warn("() () () Schedule Exception", e);
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
                logger.warn("() () () Schedule Exception", e);
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
                logger.warn("() () () Schedule Exception", e);
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
                logger.warn("() () () Schedule Exception", e);
            }
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (ScheduledFuture<?> scheduledFuture : taskMap.values()) {
            scheduledFuture.cancel(true);
        }

        executor.shutdown();
        ////////////////////////////////////////////////////////////////////////////////
    }

}

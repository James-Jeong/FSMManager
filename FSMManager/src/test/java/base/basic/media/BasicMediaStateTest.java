package base.basic.media;

import base.squirrel.media.base.MediaState;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.squirrel.CallBack;
import state.basic.StateHandler;

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
        stateManager.addStateHandler(MEDIA_STATE_NAME);
        mediaStateHandler = stateManager.getStateHandler(MEDIA_STATE_NAME);

        normalTest();

        stateManager.removeStateHandler(MEDIA_STATE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void mediaStart () {
        logger.info("@ Media is started!");
        mediaStateHandler.fire(MEDIA_START_EVENT);
    }

    public void mediaStop () {
        logger.info("@ Media is stopped!");
        mediaStateHandler.fire(MEDIA_STOP_EVENT);
    }

    public void mediaCreateSuccess () {
        logger.info("@ Success to create media!");
        mediaStateHandler.fire(MEDIA_CREATE_SUCCESS_EVENT);
    }

    public void mediaCreateFail () {
        logger.info("@ Fail to create media!");
        mediaStateHandler.fire(MEDIA_CREATE_FAIL_EVENT);
    }

    public void mediaDeleteSuccess () {
        logger.info("@ Success to delete media!");
        mediaStateHandler.fire(MEDIA_DELETE_SUCCESS_EVENT);
    }

    public void mediaDeleteFail () {
        logger.info("@ Fail to delete media!");
        mediaStateHandler.fire(MEDIA_DELETE_FAIL_EVENT);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void normalTest () {
        ////////////////////////////////////////////////////////////////////////////////
        // 1. CallBack 함수 정의
        CallBack callBack = object -> {
            if (object.length == 0) { return null; }

            String stateName = (String) object[0];
            System.out.println("CallBack is called. (curState=" + stateName + ")");

            return stateName;
        };
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 2. 상태 정의
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_START_EVENT, MediaState.IDLE_STATE, MediaState.ACTIVE_REQUEST, callBack));

        Assert.assertTrue(mediaStateHandler.addState(MEDIA_CREATE_SUCCESS_EVENT, MediaState.ACTIVE_REQUEST, MediaState.ACTIVE_STATE, callBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_CREATE_FAIL_EVENT, MediaState.ACTIVE_REQUEST, MediaState.IDLE_STATE, callBack));

        Assert.assertTrue(mediaStateHandler.addState(MEDIA_STOP_EVENT, MediaState.ACTIVE_STATE, MediaState.IDLE_REQUEST, callBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_DELETE_SUCCESS_EVENT, MediaState.IDLE_REQUEST, MediaState.IDLE_STATE, callBack));
        Assert.assertTrue(mediaStateHandler.addState(MEDIA_DELETE_FAIL_EVENT, MediaState.IDLE_REQUEST, MediaState.ACTIVE_STATE, callBack));

        Assert.assertNotNull(mediaStateHandler.getStateList());
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        this.stopWatch.start();

        mediaStart();

        mediaStateHandler.setCurState(MediaState.IDLE_STATE);

        mediaStart();
        Assert.assertEquals(MediaState.ACTIVE_REQUEST, mediaStateHandler.getCallBackResult());

        mediaCreateSuccess();
        Assert.assertEquals(MediaState.ACTIVE_STATE, mediaStateHandler.getCallBackResult());

        mediaStop();
        Assert.assertEquals(MediaState.IDLE_REQUEST, mediaStateHandler.getCallBackResult());

        mediaDeleteSuccess();
        Assert.assertEquals(MediaState.IDLE_STATE, mediaStateHandler.getCallBackResult());

        this.stopWatch.stop();
        logger.info("Done. (total time: {} s)", String.format("%.3f", ((double) this.stopWatch.getTime()) / 1000));
        ////////////////////////////////////////////////////////////////////////////////
    }

}

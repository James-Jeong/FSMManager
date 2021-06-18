package base;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.base.CallBack;
import state.module.StateHandler;

/**
 * @class public class AmfMediaStateTest
 * @brief AmfMediaStateTest class
 */
public class AmfMediaStateFsmTest {

    private static final Logger logger = LoggerFactory.getLogger(AmfMediaStateFsmTest.class);

    private static final String MEDIA_START_EVENT = "media_start_success";
    private static final String MEDIA_STOP_EVENT = "media_stop_success";
    private static final String MEDIA_CREATE_SUCCESS_EVENT = "media_create_success";
    private static final String MEDIA_CREATE_FAIL_EVENT = "media_create_fail";
    private static final String MEDIA_DELETE_SUCCESS_EVENT = "media_delete_success";
    private static final String MEDIA_DELETE_FAIL_EVENT = "media_delete_fail";

    ////////////////////////////////////////////////////////////////////////////////

    private static final String MEDIA_STATE_NAME = "media_state";

    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testStart () {
        StateManager stateManager = StateManager.getInstance();
        stateManager.addFsm(MEDIA_STATE_NAME, new MediaFsm(), MediaState.class, null, null);
        normalTest();
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

        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이


        ////////////////////////////////////////////////////////////////////////////////
    }

}

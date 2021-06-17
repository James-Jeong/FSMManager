import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import state.CallBack;
import state.StateManager;

/**
 * @class public class TestMain
 * @brief Test Main class
 */
public class TestMain {

    @BeforeClass
    public static void setUp () {

    }

    @Test
    public void TotalTest () {
        StateManager stateManager = StateManager.getInstance();

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
        Assert.assertTrue(stateManager.addState(CallState.INIT.name(), CallState.OFFER.name(), callBack));

        Assert.assertTrue(stateManager.addState(CallState.OFFER.name(), CallState.EARLY_NEGO_REQ.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.OFFER.name(), CallState.NEGO_REQ.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.OFFER.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(CallState.EARLY_NEGO_REQ.name(), CallState.EARLY_MEDIA.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.EARLY_NEGO_REQ.name(), CallState.INACTIVE.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.EARLY_MEDIA.name(), CallState.NEGO_REQ.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.EARLY_MEDIA.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(CallState.NEGO_REQ.name(), CallState.ACTIVE.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.NEGO_REQ.name(), CallState.INACTIVE.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.NEGO_REQ.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(CallState.ACTIVE.name(), CallState.HANGUP_REQ.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.INACTIVE.name(), CallState.HANGUP_REQ.name(), callBack));

        Assert.assertTrue(stateManager.addState(CallState.HANGUP_REQ.name(), CallState.INIT.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.HANGUP_REQ.name(), CallState.IDLE.name(), callBack));

        Assert.assertNotNull(stateManager.getStateList());
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 3. 상태 천이
        Assert.assertNull(stateManager.nextState(CallState.INACTIVE.name())); // 비정상 천이 > 아직 처음 상태가 정의되지 않음
        Assert.assertNull(stateManager.getCallBackResult());

        stateManager.setCurState(CallState.INIT.name());

        Assert.assertNotNull(stateManager.nextState(CallState.OFFER.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.OFFER.name());

        Assert.assertNull(stateManager.nextState(CallState.INACTIVE.name())); // 비정상 천이 > 정의되지 않은 상태로 천이
        Assert.assertNull(stateManager.getCallBackResult());

        Assert.assertNotNull(stateManager.nextState(CallState.EARLY_NEGO_REQ.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.EARLY_NEGO_REQ.name());

        Assert.assertNotNull(stateManager.nextState(CallState.EARLY_MEDIA.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.EARLY_MEDIA.name());

        Assert.assertNotNull(stateManager.nextState(CallState.NEGO_REQ.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.NEGO_REQ.name());

        Assert.assertNotNull(stateManager.nextState(CallState.ACTIVE.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.ACTIVE.name());

        Assert.assertNull(stateManager.nextState(CallState.ACTIVE.name())); // 비정상 천이 > 동일 상태로 천이
        Assert.assertNull(stateManager.getCallBackResult());

        Assert.assertNotNull(stateManager.nextState(CallState.HANGUP_REQ.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.HANGUP_REQ.name());

        Assert.assertNotNull(stateManager.nextState(CallState.INIT.name()));
        Assert.assertEquals(stateManager.getCallBackResult(), CallState.INIT.name());
        ////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////
        // 4. 상태 삭제

        Assert.assertTrue(stateManager.removeFromState(CallState.HANGUP_REQ.name()));
        Assert.assertFalse(stateManager.removeFromState(CallState.HANGUP_REQ.name()));

        Assert.assertTrue(stateManager.removeToStateByFromState(CallState.EARLY_MEDIA.name(), CallState.NEGO_REQ.name()));
        Assert.assertFalse(stateManager.removeToStateByFromState(CallState.EARLY_MEDIA.name(), CallState.NEGO_REQ.name()));

        Assert.assertNotNull(stateManager.nextState(CallState.OFFER.name()));
        Assert.assertNotNull(stateManager.nextState(CallState.EARLY_NEGO_REQ.name()));
        Assert.assertNotNull(stateManager.nextState(CallState.EARLY_MEDIA.name()));
        Assert.assertNull(stateManager.nextState(CallState.NEGO_REQ.name()));

        Assert.assertNull(stateManager.nextState(CallState.HANGUP_REQ.name()));

        ////////////////////////////////////////////////////////////////////////////////
    }

}

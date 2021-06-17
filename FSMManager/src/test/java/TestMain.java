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

        CallBack callBack = object -> {
            if (object.length == 0) { return; }

            String stateName = (String) object[0];
            System.out.println(stateName);
        };

        Assert.assertTrue(stateManager.addState(CallState.INIT.name(), callBack));
        Assert.assertTrue(stateManager.addState(CallState.OFFER.name(), callBack));

        stateManager.setCurState(CallState.INIT.name());

        Assert.assertNotNull(stateManager.nextState(CallState.OFFER.name()));
    }

}

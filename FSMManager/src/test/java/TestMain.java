import base.basic.base.ServiceManager;
import base.basic.call.BasicCallStateTest;
import base.basic.media.BasicMediaStateTest;
import org.junit.BeforeClass;
import org.junit.Test;

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

        ////////////////////////////////////////////////////////////////////////////////
        // # basic

        ServiceManager serviceManager = ServiceManager.getInstance();

        serviceManager.start();

        BasicCallStateTest basicCallStateTest = new BasicCallStateTest();
        basicCallStateTest.testStart();

        BasicMediaStateTest basicMediaStateTest = new BasicMediaStateTest();
        basicMediaStateTest.testStart();

        serviceManager.stop();

        ////////////////////////////////////////////////////////////////////////////////
        // # Squirrel FSM

        //SquirrelCallStateFsmTest squirrelCallStateFsmTest = new SquirrelCallStateFsmTest();
        //squirrelCallStateFsmTest.testStart();

        //SquirrelMediaStateFsmTest squirrelMediaStateFsmTest = new SquirrelMediaStateFsmTest();
        //squirrelCallStateFsmTest.testStart();

        //SquirrelCallAndMediaStateFsmTest squirrelCallAndMediaStateFsmTest = new SquirrelCallAndMediaStateFsmTest();
        //squirrelCallAndMediaStateFsmTest.testStart();

        ////////////////////////////////////////////////////////////////////////////////
        // # Akka FSM

        //AkkaMediaStateTest akkaMediaStateTest = new AkkaMediaStateTest();
        //akkaMediaStateTest.testStart();
    }

}

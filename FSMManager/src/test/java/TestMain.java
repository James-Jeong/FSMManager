import base.basic.base.ServiceManager;
import base.basic.call.BasicCallStateTest;
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

        //BasicMediaStateTest basicMediaStateTest = new BasicMediaStateTest();
        //basicMediaStateTest.testStart();

        BasicCallStateTest basicCallStateTest = new BasicCallStateTest();
        basicCallStateTest.testStart();

        serviceManager.stop();

        ////////////////////////////////////////////////////////////////////////////////
        // # ATM state

        //BasicAtmStateTest basicAtmStateTest = new BasicAtmStateTest();
        //basicAtmStateTest.testStart();

        ////////////////////////////////////////////////////////////////////////////////
        // # Squirrel FSM

        /*SquirrelCallStateFsmTest squirrelCallStateFsmTest = new SquirrelCallStateFsmTest();
        squirrelCallStateFsmTest.testStart();

        SquirrelMediaStateFsmTest squirrelMediaStateFsmTest = new SquirrelMediaStateFsmTest();
        squirrelCallStateFsmTest.testStart();

        SquirrelCallAndMediaStateFsmTest squirrelCallAndMediaStateFsmTest = new SquirrelCallAndMediaStateFsmTest();
        squirrelCallAndMediaStateFsmTest.testStart();*/

        ////////////////////////////////////////////////////////////////////////////////
        // # Akka FSM

        /*AkkaMediaStateTest akkaMediaStateTest = new AkkaMediaStateTest();
        akkaMediaStateTest.testStart();*/
    }

}

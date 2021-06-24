import base.basic.BasicCallAndMediaStateTest;
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

        BasicCallStateTest basicCallStateTest = new BasicCallStateTest();
        basicCallStateTest.testStart();

        BasicMediaStateTest basicMediaStateTest = new BasicMediaStateTest();
        basicMediaStateTest.testStart();

        BasicCallAndMediaStateTest basicCallAndMediaStateTest = new BasicCallAndMediaStateTest();
        basicCallAndMediaStateTest.testStart();

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

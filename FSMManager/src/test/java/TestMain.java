import base.AmfCallAndMediaStateTest;
import base.call.AmfCallStateTest;
import base.media.AmfMediaStateFsmTest;
import base.media.AmfMediaStateTest;
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
        //AmfCallStateTest amfCallStateTest = new AmfCallStateTest();
        //amfCallStateTest.testStart();

        //AmfMediaStateTest amfMediaStateTest = new AmfMediaStateTest();
        //amfMediaStateTest.testStart();

        //AmfCallAndMediaStateTest amfCallAndMediaStateTest = new AmfCallAndMediaStateTest();
        //amfCallAndMediaStateTest.testStart();

        AmfMediaStateFsmTest amfMediaStateFsmTest = new AmfMediaStateFsmTest();
        amfMediaStateFsmTest.testStart();
    }

}

import base.AmfCallAndMediaStateTest;
import base.AmfCallStateTest;
import base.AmfMediaStateTest;
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
        AmfCallStateTest amfCallStateTest = new AmfCallStateTest();
        amfCallStateTest.testStart();

        AmfMediaStateTest amfMediaStateTest = new AmfMediaStateTest();
        amfMediaStateTest.testStart();

        AmfCallAndMediaStateTest amfCallAndMediaStateTest = new AmfCallAndMediaStateTest();
        amfCallAndMediaStateTest.testStart();
    }

}

import base.AmfCallStateTest;
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
    }

}

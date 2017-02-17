
package spotifysystemTest.logic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spotifysystem.logic.ApiFunctionHandler;
import static org.junit.Assert.*;
import org.junit.ClassRule;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import spotifysystem.logic.MainLogic;
import spotifysystem.logic.WebSite;

/**
 *
 * @author Owner
 */
public class ApiFunctionHandlerTest {

    static String code;

    public ApiFunctionHandlerTest() {
    }


    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        WebSite.logIn("shufflertest", "1234");
        code = WebSite.getCode();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testWithWrongCode() {
        int status = ApiFunctionHandler.getTokens("1234");
        assertTrue(status < 2);
    }

}

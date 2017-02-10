package spotifysystemTest.logic;

import static junit.framework.TestCase.assertFalse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import spotifysystem.logic.WebSite;

public class WebSiteTest {

    public WebSiteTest() {
    }


    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void logInWorks() {
        WebSite.logIn("shufflertest", "1234");
        assertFalse(WebSite.getCode().equals(""));
    }

}

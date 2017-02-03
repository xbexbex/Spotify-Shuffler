/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spotifysystemTest.logic;

import com.wrapper.spotify.Api;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import spotifysystem.logic.AuthHandler;
import static org.junit.Assert.*;

/**
 *
 * @author Owner
 */
public class AuthHandlerTest {

    public AuthHandlerTest() {
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
    public void testWithWrongCode() {
        int status = AuthHandler.getTokens("1234");
        assertTrue(status < 2);
    }

    @Test
    public void apiBuilderTest() {
        Api testApi = Api.builder()
                .clientId("cb4d60eaad584defba20088354bf6bbc")
                .clientSecret("895f9958fdd04170a1095adf5ad83ef3")
                .redirectURI("http://localhost:8888/callback")
                .build();
        Api api = AuthHandler.returnApi(true);
        assertTrue(api.equals(testApi));
    }

    @Test
    public void apiBuilderTest2() {
        Api testApi = Api.builder()
                .accessToken("1234")
                .refreshToken("4321")
                .build();
        AuthHandler.setAToken("1234");
        AuthHandler.setRToken("4321");
        Api api = AuthHandler.returnApi(false);
        assertTrue(api.equals(testApi));
    }
}

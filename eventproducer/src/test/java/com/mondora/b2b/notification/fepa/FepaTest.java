package com.mondora.b2b.notification.fepa;

import com.teamsystem.fe.passiva.UserAuthenticationClient;
import com.teamsystem.fepa.passiva.*;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by mmondora on 01/02/2017.
 */
public class FepaTest {
    static final String WS_FEPA_PASSIVA_USER_AUTH_ENDPOINT = "http://fepa-ws-test.teamsystem.com/knos/wcf/FE/Passiva/UserAuthentication.svc/http";
    static final String WS_FEPA_PASSIVA_SERVIZIO_ENDPOINT  = "http://fepa-ws-test.teamsystem.com/knos/wcf/FE/Passiva/ServizioFEPassiva.svc/http";

    static final String username = "hubTechUserTest";
    static final String password = "hubTechUserTest";
    static final String authenticateAs = "12345678";

    @Test
    public void testLogin() {
        System.setProperty("WS_FEPA_PASSIVA_USER_AUTH_ENDPOINT", WS_FEPA_PASSIVA_USER_AUTH_ENDPOINT);

        // TODO recuperare le credenziali corrette!
        UserIdentityInputDC user = new UserIdentityInputDC();
        try {
            user.setSecurityToken(getSecurityToken(username, password, authenticateAs));
        } catch (Exception e) {
            String errMsg = e.getMessage();
            assertTrue("Wrong user/pass", errMsg.indexOf("Fault Code: 503 Username o password errati.") != -1);
        }
    }

    @Ignore
    @Test
    public void testConnection() throws Exception {
        System.setProperty("WS_FEPA_PASSIVA_USER_AUTH_ENDPOINT", WS_FEPA_PASSIVA_USER_AUTH_ENDPOINT);
        System.setProperty("WS_FEPA_PASSIVA_SERVIZIO_ENDPOINT",  WS_FEPA_PASSIVA_SERVIZIO_ENDPOINT);

        IServizioFEPassiva fepa = new ServizioFEPassiva().getHttpEndpointIServizioFEPassiva();
        UserIdentityInputDC user = new UserIdentityInputDC();

        user.setSecurityToken(getSecurityToken(username, password, authenticateAs));
        user.setUsername(username);

        SearchFEInputDC search = new SearchFEInputDC();
        search.setPageSize(100);
        SearchFEOutputDC out = fepa.searchFE(user, search);

        assertNotNull(out);
    }

    public String getSecurityToken(String user, String pass, String as) throws Exception {
        UserAuthenticationClient uac = new UserAuthenticationClient();
        return uac.login( user, pass, as );
    }
}

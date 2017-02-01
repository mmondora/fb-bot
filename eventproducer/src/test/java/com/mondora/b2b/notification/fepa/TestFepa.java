package com.mondora.b2b.notification.fepa;

import com.teamsystem.fe.passiva.UserAuthenticationClient;
import com.teamsystem.fepa.passiva.*;
import com.teamsystem.fepa.userauth.BaseUserIdentityInputDC;
import com.teamsystem.fepa.userauth.IUserAuthentication;
import com.teamsystem.fepa.userauth.TicketOutputDC;
import com.teamsystem.fepa.userauth.UserAuthentication;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by mmondora on 01/02/2017.
 */
public class TestFepa {

    @Test
    public void testConnection() throws Exception {
        System.setProperty("WS_FEPA_ATTIVA_USER_AUTH_ENDPOINT","http://fepa-ws-test.teamsystem.com/knos/wcf/FE/Passiva/UserAuthentication.svc");

        IServizioFEPassiva fepa = new ServizioFEPassiva().getHttpEndpointIServizioFEPassiva();
        UserIdentityInputDC user = new UserIdentityInputDC();

        String u = "hubTechUserTest";
        user.setSecurityToken(getSecurityToken(u, "hubTechUserTest", "12345678"));
        user.setUsername(u);

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

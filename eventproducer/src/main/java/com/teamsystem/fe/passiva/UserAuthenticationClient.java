package com.teamsystem.fe.passiva;

import com.mondora.teamsystem.hub.utils.Property;
import com.mondora.teamsystem.hub.utils.StringThreadLocal;
import com.mondora.teamsystem.hub.utils.server.ThreadLocalLoggingInInterceptor;
import com.teamsystem.fepa.userauth.*;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 01/02/2017.
 */
public class UserAuthenticationClient {
    private static final Logger LOG = LoggerFactory.getLogger(UserAuthenticationClient.class);

    public String login(String username, String password, String authenticateAs) throws Exception {
        long now = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
//            LOG.debug(append("action", "fepaLogin").
//                            <LogstashMarker>and(append("step", "input")).
//                            <LogstashMarker>and(append("username", username)).
//                            <LogstashMarker>and(append("authenticateAs", authenticateAs)),
            LOG.debug(
                    "Attempting login for user '{}' as '{}", username, authenticateAs);
        }

        username = username.toLowerCase();
        password = password.toLowerCase();

        IUserAuthentication iUserAuthentication;
        TicketOutputDC ticketOutputDC;
        try {
            BaseUserIdentityInputDC baseUserIdentityInputDC = getBaseUserIdentityInputDC(username);
            iUserAuthentication = getService();
            ticketOutputDC = iUserAuthentication.getTicket(baseUserIdentityInputDC);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
//            LOG.error(append("action", "getTicket").
//                            <LogstashMarker>and(append("step", "output")).
//                            <LogstashMarker>and(append("username", username)).
//                            <LogstashMarker>and(append("exec_time", System.currentTimeMillis() - now)),
//                    "Error while getTicket for user '" + username + "' errorMessage: '" + errorMessage + "'", e);
            LOG.error("Error while getTicket for user '" + username + "' errorMessage: '" + errorMessage + "'", e);
            String output = StringThreadLocal.get();
            StringThreadLocal.unset();
            throw new Exception(errorMessage);
        }
        String ticket = ticketOutputDC.getTicket();

        if (LOG.isDebugEnabled()) {
//            LOG.debug(append("action", "getTicket").
//                            <LogstashMarker>and(append("step", "output")).
//                            <LogstashMarker>and(append("username", username)).
//                            <LogstashMarker>and(append("ticket", ticket)).
//                            <LogstashMarker>and(append("exec_time", System.currentTimeMillis() - now)),
//                    "Received ticket '{}' for user '{}'", ticket, username);
            LOG.debug("Received ticket '{}' for user '{}'", ticket, username);
        }

        String securityToken;
        try {
            String digest = generateDigest(username, password, ticket, ticketOutputDC.getClientIPAddress());
            DigestAuthenticationInputDC digestAuthenticationInputDC = getDigestAuthenticationInputDC(username, ticket, digest, authenticateAs);
            AuthenticationOutputDC authenticationOutputDC = iUserAuthentication.digestAuthentication(digestAuthenticationInputDC);
            securityToken = authenticationOutputDC.getUserIdentity().getSecurityToken();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
//            LOG.error(append("action", "digestAuthentication").
//                            <LogstashMarker>and(append("step", "output")).
//                            <LogstashMarker>and(append("username", username)).
//                            <LogstashMarker>and(append("exec_time", System.currentTimeMillis() - now)),
//                    "Error while trying digestAuthentication for user '" + username + "' errorMessage: '" + errorMessage + "'", e);
            LOG.error("Error while trying digestAuthentication for user '" + username + "' errorMessage: '" + errorMessage + "'", e);
            String output = StringThreadLocal.get();
            StringThreadLocal.unset();
            throw new Exception(errorMessage);
        }
        if (LOG.isDebugEnabled()) {
//            LOG.debug(append("action", "fepaLogin").
//                            <LogstashMarker>and(append("step", "output")).
//                            <LogstashMarker>and(append("username", username)).
//                            <LogstashMarker>and(append("ticket", ticket)).
//                            <LogstashMarker>and(append("securityToken", securityToken)).
//                            <LogstashMarker>and(append("exec_time", System.currentTimeMillis() - now)),
//                    "Received token '{}' for user '{}'", securityToken, username);
            LOG.debug("Received token '{}' for user '{}'", securityToken, username);
        }
//
        return securityToken;
    }

    protected BaseUserIdentityInputDC getBaseUserIdentityInputDC(String username) {
        BaseUserIdentityInputDC baseUserIdentityInputDC = new BaseUserIdentityInputDC();
        baseUserIdentityInputDC.setUsername(username);
        return baseUserIdentityInputDC;
    }

    protected String generateDigest(String username, String password, String ticket, String clientIPAddress) throws Exception {
        String key = Utils.calculateSHA1(username + password).toUpperCase();
        return Utils.calculateSHA1(username + clientIPAddress + key + ticket).toUpperCase();
    }

    protected DigestAuthenticationInputDC getDigestAuthenticationInputDC(String username, String ticket, String digest, String authenticateAs) {
        DigestAuthenticationInputDC digestAuthenticationInputDC = new DigestAuthenticationInputDC();
        digestAuthenticationInputDC.setUserName(username);
        digestAuthenticationInputDC.setTicket(ticket);
        digestAuthenticationInputDC.setDigest(digest);
//        digestAuthenticationInputDC.setAutenticateAs(authenticateAs);
        return digestAuthenticationInputDC;
    }

    protected IUserAuthentication getService() {
        JaxWsProxyFactoryBean factory = getJaxWsProxyFactoryBean("WS_FEPA_PASSIVA_USER_AUTH_ENDPOINT");
        factory.setServiceClass(IUserAuthentication.class);
        IUserAuthentication port = (IUserAuthentication) factory.create();
        return port;
    }

    protected JaxWsProxyFactoryBean getJaxWsProxyFactoryBean(String endpointEnvKey) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.getInInterceptors().add(new LoggingInInterceptor());
        factory.getInInterceptors().add(new ThreadLocalLoggingInInterceptor());
        factory.getOutInterceptors().add(new LoggingOutInterceptor());
        factory.setBindingId("http://schemas.xmlsoap.org/wsdl/soap12/");

        //Properties properties = new Properties();
        String endpointAddress = null;
        try {
            //properties.load(JaxWsProxyFactoryBean.class.getResourceAsStream("/tshub.properties"));
            //endpointAddress = Property.get(properties, endpointEnvKey, null);
            endpointAddress = Property.get(endpointEnvKey, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (endpointAddress == null) {
            endpointAddress = Property.get(endpointEnvKey, "http://127.0.0.1:" + Property.get("PORT", 9080) + "/FepaAttiva");
        }
        factory.setAddress(endpointAddress);
        return factory;
    }
}
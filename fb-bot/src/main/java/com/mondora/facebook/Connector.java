package com.mondora.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mondora.Database;
import com.mondora.Utils;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import com.mondora.model.FBUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.mondora.Utils.convertStreamToString;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Connector {
    protected static final String PAGE_ACCESS_TOKEN = Utils.getenv("FACEBOOK_PAGE_ACCESS_TOKEN");
    protected static final String FACEBOOK_API_VERSION = Utils.getenv("FACEBOOK_API_VERSION", "v2.6");

    private static final Logger LOG = LoggerFactory.getLogger(Connector.class);

    public static void sendGenericMessage(String id, String payload) {
        if (checkIdActive(id)) {
            sendGenericMessage_NOCheck(payload);
        } else {
            LOG.info("User id:" + id + " ignored message.");
        }
    }

    /*
        Send without checking to any URL
        it adds only the access_token
     */
    protected static void POST(String hostname, String payload) {
        LOG.debug("--- StartOfTransmission");
        try {
            String url = hostname + "?access_token=" + PAGE_ACCESS_TOKEN;
            HttpURLConnection urlc = (HttpURLConnection) new URL(url).openConnection();
            urlc.setRequestMethod("POST");
            urlc.setDoOutput(true);
            urlc.setRequestProperty("Content-Type", "application/json");
            if (LOG.isDebugEnabled()) {
                LOG.debug("POST " + url);
                LOG.debug(payload);
            }

            try (OutputStream output = urlc.getOutputStream()) {
                output.write(payload.getBytes());
            }

            LOG.info("POST " + hostname + " " + urlc.getResponseCode() + " " + urlc.getResponseMessage());
            if (urlc.getResponseCode() >= 200 && urlc.getResponseCode() < 300) {
                if (LOG.isDebugEnabled()) try {
                    String json = convertStreamToString(urlc.getInputStream());
                    LOG.debug("Response " + json);
                } catch (IOException ie) {
                }
            } else {
                if (LOG.isErrorEnabled()) try {
                    String err = convertStreamToString(urlc.getErrorStream());
                    LOG.error(err);
                } catch (Exception ie) {
                }
            }
            LOG.debug("--- EndOfTransmission");

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void sendGenericMessage_NOCheck(String payload) {
        new Thread(() -> {
            String hostname = "https://graph.facebook.com/" + FACEBOOK_API_VERSION + "/me/messages";
            POST(hostname, payload);
        }).run();
    }

    static public FBUser readMessengerData(String id) {
        try {
            String hostname = "https://graph.facebook.com/" + FACEBOOK_API_VERSION + "/" + id;
            String url = hostname + "?fields=first_name,last_name,profile_pic,locale,timezone,gender";
            url += "&access_token=" + PAGE_ACCESS_TOKEN;
            if (LOG.isDebugEnabled()) {
                LOG.debug(" --- StartOfTransmission");
                LOG.debug("URL " + url);
            }
            HttpURLConnection urlc = (HttpURLConnection) new URL(url).openConnection();
            urlc.setRequestProperty("Content-Type", "application/json");
            String json = convertStreamToString(urlc.getInputStream());
            String err = convertStreamToString(urlc.getErrorStream());
            LOG.info("GET " + hostname + " " + urlc.getResponseCode() + " " + urlc.getResponseMessage());

            if (urlc.getResponseCode() >= 200 && urlc.getResponseCode() < 300) {
                LOG.debug("Response " + json);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(json);

                com.mondora.model.FBUser user = new FBUser();
                user.first_name = node.get("first_name").textValue();
                user.last_name = node.get("last_name").textValue();
//                user.gender = node.get("gender").textValue();
//                user.profile_pic = node.get("profile_pic").textValue();
//                user.timezone = node.get("timezone").textValue();
                user.messenger_id = id;

                Database.saveUser(id, user);
                return user;
            } else {
                LOG.error("Error " + err);
                return null;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        } finally {
            LOG.debug(" --- EndOfTransmission");
        }
    }


    protected static boolean checkIdActive(String id) {
        return Database.findUser(id).active;
    }

    public static void sendTextMessage(String id, String text) {
        String payload = "{" +
                "\"recipient\": { \"id\": \"" + id + "\"}," +
                "\"message\": { \"text\": \"" + text + "\"  }" +
                "}";

        sendGenericMessage(id, payload);
    }

    public static void sendStructuredMessage(String id, String uuid, String text) {
        TwoChoicePostback o = new TwoChoicePostback();
        o.recipient.id = id;
        PBPayload payload = o.message.attachment.payload;
        payload.addElement(text, "", "https://app.agyo.io/console/index.html");
        payload.last().addPostbackButton("Rifiuta", "Rifiuta@" + uuid);
        payload.last().addPostbackButton("Accetta", "Accetta@" + uuid);
        sendPostback(o);
    }

    public static void sendPostback(TwoChoicePostback o) {
        String json = Utils.toJson(o);
        if (json != null) {
            sendGenericMessage(o.recipient.id, json);
        }
    }

    public static String getId(JsonNode node) {
        String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
        return id;
    }

    public TwoChoicePostback createPostBack(String id) {
        TwoChoicePostback o = new TwoChoicePostback();
        o.recipient.id = id;
        return o;
    }

    public TwoChoicePostback createPostBack(JsonNode node) {
        String id = getId(node);
        return createPostBack(id);
    }
}

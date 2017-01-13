package com.mondora.facebook.sending;

import com.mondora.Utils;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static com.mondora.Utils.convertStreamToString;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Sender {
    static final String PAGE_ACCESS_TOKEN = Utils.getenv("FACEBOOK_PAGE_ACCESS_TOKEN");
    static final String FACEBOOK_API_VERSION = Utils.getenv("FACEBOOK_API_VERSION", "v2.6");

    private static final Logger LOG = LoggerFactory.getLogger(Sender.class);

    public static void sendGenericMessage(String payload) {
        new Thread(() -> {
            LOG.debug("--- StartOfTransmission");
            try {

                String hostname = "https://graph.facebook.com/" + FACEBOOK_API_VERSION + "/me/messages";
                String url = hostname + "?access_token=" + PAGE_ACCESS_TOKEN;
                HttpURLConnection urlc = (HttpURLConnection) new URL(url).openConnection();
                urlc.setRequestMethod("POST");
                urlc.setDoOutput(true);
                urlc.setRequestProperty("Content-Type", "application/json");
                if( LOG.isDebugEnabled() ) {
                    LOG.debug("POST " + url);
                    LOG.debug( payload );
                }

                try (OutputStream output = urlc.getOutputStream()) {
                    output.write(payload.getBytes());
                }
                String json = convertStreamToString(urlc.getInputStream());
                String err = convertStreamToString(urlc.getErrorStream());
                LOG.info("POST " + hostname + " " + urlc.getResponseCode() + " " + urlc.getResponseMessage());
                if (urlc.getResponseCode() >= 200 && urlc.getResponseCode() < 300) {
                    LOG.debug("Response " + json);
                } else {
                    LOG.error(err);
                }
                LOG.debug("--- EndOfTransmission");

            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }).run();
    }

    public static void sendTextMessage(String id, String text) {
        String payload = "{" +
                "\"recipient\": { \"id\": \"" + id + "\"}," +
                "\"message\": { \"text\": \"" + text + "\"  }" +
                "}";

        sendGenericMessage(payload);
    }

    public static void sendStructuredMessage(String id, String uuid, String text) {
        TwoChoicePostback o = new TwoChoicePostback();
        o.recipient.id = id;
        PBPayload payload = o.message.attachment.payload;
        payload.addElement("Agyo, nuovo documento", text, "https://app.agyo.io/console/index.html");
        payload.last().addPostbackButton("Rifiuta", "Rifiuta@" + uuid);
        payload.last().addPostbackButton("Accetta", "Accetta@" + uuid);

        String json = Utils.toJson(o);
        if (json != null) {
            sendGenericMessage(json);
        }
    }

    ;
}

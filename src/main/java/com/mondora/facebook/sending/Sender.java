package com.mondora.facebook.sending;

import com.mondora.Utils;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static com.mondora.Utils.convertStreamToString;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Sender {
    static final String PAGE_ACCESS_TOKEN =  Utils.getenv("FACEBOOK_PAGE_ACCESS_TOKEN");
    static final String FACEBOOK_API_VERSION = Utils.getenv( "FACEBOOK_API_VERSION", "v2.6" );

    private static final Logger LOG = LoggerFactory.getLogger(Sender.class);

    public static void sendGenericMessage(String payload) {
        new Thread(() -> {
            String id = Thread.currentThread().getName() + "/" + Thread.currentThread().getId();
            LOG.debug(id + " --- StartOfTransmission");
            try {
                //EAALrRUp2rHEBAH1ZAPIYfzzGHwRNDYLP0KICrOiKOCknfiMjQ2NmCgBb0Ud6QzKSRx2JUXi8YVvrj07ZBbxorZBkCbrQ6usvxMR2BHarZAGToYAPvea1bzHs6vKILL3YIkuNc8xgh23V7i07shswxRHgIPTUz5ftcgVXSY1ZA3gZDZD
                String url = "https://graph.facebook.com/" + FACEBOOK_API_VERSION + "/me/messages";
                url += "?access_token=" + PAGE_ACCESS_TOKEN;
                URLConnection urlc = new URL(url).openConnection();
                ((HttpURLConnection) urlc).setRequestMethod("POST");
                urlc.setDoOutput(true);
                urlc.setRequestProperty("Content-Type", "application/json");

                try (OutputStream output = urlc.getOutputStream()) {
                    output.write(payload.getBytes());
                }
                String json = convertStreamToString(urlc.getInputStream());
                String err = convertStreamToString(((HttpURLConnection) urlc).getErrorStream());

                if (LOG.isDebugEnabled()) {
                    LOG.debug(id + " POST to URL " + url);
                    LOG.debug(id + " " + payload);
                }
                if( err != null && ! err.isEmpty() ) LOG.error(id + " " + err);
                if( json != null && ! json.isEmpty() ) LOG.info(json);
                LOG.debug(id + " --- EndOfTransmission");

            } catch (Exception e) {
                LOG.debug(id + " --- Exception !!", e);
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

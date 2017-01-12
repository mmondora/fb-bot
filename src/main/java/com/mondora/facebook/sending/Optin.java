package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mondora.Database;
import com.mondora.HelloController;
import com.mondora.Utils;
import com.mondora.facebook.Configuration;
import com.mondora.model.FBUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.mondora.Utils.convertStreamToString;

/**
 * Created by mmondora on 12/01/2017.
 */ //"object":"page","entry":[{"id":"292024547861549","time":1483539426515,"messaging":[{"recipient":{"id":"292024547861549"},"timestamp":1483539426515,"sender":{"id":"1250148388409499"},"optin":{"ref":"PASS_THROUGH_PARAM"}}]}]}
public class Optin extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Optin.class);


    public void run(JsonNode node) {
        JsonNode optin = node.get("entry").get(0).get("messaging").get(0).get("optin");
        if (optin != null) {
            String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
            FBUser u = readMessengerData(id);
            u.b2b_id = node.get("entry").get(0).get("messaging").get(0).get("optin").get("ref").asText();
            if (u != null) {
                Sender.sendTextMessage(id, "Ciao " + u.first_name + " e benvenuto.");
                Database.saveMap("users.obj");
            } else
                Sender.sendTextMessage(id, "Ciao e benvenuto.");
        }
    }

    private FBUser readMessengerData(String id) {
        FBUser find = Database.findUser(id);
        if (find != null) {
            LOG.debug("Cache Hit for user " + find.messenger_id + " " + find.first_name + " " + find.last_name);
            return find;
        } else
            try {
                String url = "https://graph.facebook.com/" + Configuration.FACEBOOK_API_VERSION + "/" + id;
                url += "?fields=first_name,last_name,profile_pic,locale,timezone,gender";
                url += "&access_token=" + Configuration.PAGE_ACCESS_TOKEN;
                LOG.debug(" --- StartOfTransmission");
                LOG.debug("URL " + url);
                HttpURLConnection urlc = (HttpURLConnection) new URL(url).openConnection();
                urlc.setRequestProperty("Content-Type", "application/json");
                String json = convertStreamToString(urlc.getInputStream());
                String err = convertStreamToString(urlc.getErrorStream());
                LOG.debug("URL " + urlc.getResponseCode() + " " + urlc.getResponseMessage());

                if (urlc.getResponseCode() >= 200 && urlc.getResponseCode() < 300) {

                    LOG.debug("Response " + json);

                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(json);

                    FBUser user = new FBUser();
                    user.first_name = node.get("first_name").textValue();
                    user.last_name = node.get("last_name").textValue();
//                user.gender = node.get("gender").textValue();
//                user.profile_pic = node.get("profile_pic").textValue();
//                user.timezone = node.get("timezone").textValue();
                    user.messenger_id = id;

                    Database.saveUser(id, user);
                    return user;
                } else {
                    try {
                        LOG.error("Error " + err);
                    } catch (Exception e) {
                    }
                    return null;
                }
            } catch (Exception e) {
                LOG.error("\n--- Exception !!", e);
                return null;
            } finally {
                LOG.debug(" --- EndOfTransmission");
            }
    }
}

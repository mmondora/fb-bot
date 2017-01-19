package com.mondora.facebook.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.Connector;
import com.mondora.model.FBUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 12/01/2017.
 */ //"object":"page","entry":[{"id":"292024547861549","time":1483539426515,"messaging":[{"recipient":{"id":"292024547861549"},"timestamp":1483539426515,"sender":{"id":"1250148388409499"},"optin":{"ref":"PASS_THROUGH_PARAM"}}]}]}
public class Optin extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Optin.class);

    public void run(JsonNode node) {
        LOG.info( "Optin");
        JsonNode optin = node.get("entry").get(0).get("messaging").get(0).get("optin");
        if (optin != null) {
            String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
            FBUser u = readMessengerData(id);
            u.b2b_id = node.get("entry").get(0).get("messaging").get(0).get("optin").get("ref").asText();
            if (u != null) {
                sendTextMessage(id, "Ciao " + u.first_name + " e benvenuto.");
                Database.saveMap("users.obj");
            } else
                sendTextMessage(id, "Ciao e benvenuto.");
        }
    }
}

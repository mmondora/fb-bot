package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.Connector;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.FBUser;

/**
 * Created by mmondora on 19/01/2017.
 */
public class SettingHubID extends Connector implements Strategy {

    @Override
    public void run(JsonNode node) {
        JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
        String text = msg.get("text").asText();
        text = text.trim();
        String hubId = text.substring( text.indexOf( ' ' ) + 1);
        String id = getId( node );
        if( hubId != null && ! hubId.isEmpty() ) {
            FBUser user = Database.findUser(id);
            user.b2b_id = hubId;
            Database.saveUser(id, user);
            sendTextMessage(id, "Grazie " + user.first_name + " per aver fornito le tue credenziali.");
        } else {
            sendTextMessage(id, "Credenziali non valide");
        }
    }
}

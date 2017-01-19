package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.Connector;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.FBUser;

import javax.xml.crypto.Data;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Subscribe extends Connector implements Strategy {

    @Override
    public void run(JsonNode node) {
        String id = getId(node);
        FBUser user = Database.findUser(id);
        user.active = true;
//        Database.saveUser(id,user);
        sendTextMessage(id, "Grazie " + user.first_name + " per la sottoscrizione.");
    }
}

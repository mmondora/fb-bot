package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.Connector;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.FBUser;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Unsubscribe extends Connector implements Strategy {
    @Override
    public void run(JsonNode node) {
        String id = getId(node);
        FBUser user = Database.findUser(id);
        sendTextMessage(id, user.first_name + " ti abbiamo tolto dalle sottoscrizione. 'subscribe' per riattivare");
        user.active = false;
//        Database.saveUser(id, user);
    }
}


package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.Utils;
import com.mondora.facebook.Connector;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.FBUser;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Status extends Connector implements Strategy {
    @Override
    public void run(JsonNode node) {
        String id = getId( node );
        FBUser user = Database.findUser(id);
        String text = "'Status': { 'User':'" + user.first_name + " " + user.last_name +
                "', 'active':" + (user.active?" 'yes'":" 'no'") +
                (user.b2b_id!=null?", 'hub':'"+user.b2b_id+"'":"") + " }";
        String payload = "{" +
                "\"recipient\": { \"id\": \"" + id + "\"}," +
                "\"message\": { \"text\": \"" + text + "\"  }" +
                "}";
        sendGenericMessage_NOCheck(payload);
    }
}
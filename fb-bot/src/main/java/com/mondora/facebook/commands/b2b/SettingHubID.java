package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.Utils;
import com.mondora.facebook.Connector;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.FBUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 19/01/2017.
 */
public class SettingHubID extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(SettingHubID.class);

    @Override
    public void run(JsonNode node) {
        LOG.info( "Imposto credenziali {}", Utils.toJson(node));
        JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
        String text = msg.get("text").asText();
        String id = getId( node );
        String[] in = split(text);
        if( (in != null && in.length != 3) && ( ! checkCredenziali( in[1], in[2] ) ) ) {
            sendTextMessage(id, "Credenziali non valide.");
        } else {
            FBUser user = Database.findUser(id);
            user.b2b_id = in[1];
            user.cred = in[2];
            Database.saveUser(id, user);
            sendTextMessage(id, "Grazie " + user.first_name + " per aver fornito le tue credenziali.");

        }
    }

    private boolean checkCredenziali(String id, String secret) {
        // test di una login sul b2b ?
        if( id.isEmpty() || secret.isEmpty() ) return false;
        try {
            return true;
        } catch( Exception e ) {
            if( LOG.isDebugEnabled() ) {
                LOG.info("Credenziali {} non valide.", id);
                LOG.debug( e.getMessage(), e );
            } else
                LOG.info( "Credenziali {} non valide. {}", id, e.getMessage() );
            return false;
        }
    }

    public String[] split(String in) {
        return in.split(" ");
    }


}

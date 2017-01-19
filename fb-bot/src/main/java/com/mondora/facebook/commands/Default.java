package com.mondora.facebook.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Utils;
import com.mondora.facebook.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Default extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Default.class);
    @Override
    public void run(JsonNode node) {
        LOG.debug( Utils.toJson(node));
        String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
        JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
        if( msg != null ) {
            String text = msg.get("text").asText();
            String out = "Grazie per la richiesta '" + text + "' ma non so ancora aiutarti.";
            sendTextMessage(id, out);
        }
    }
}

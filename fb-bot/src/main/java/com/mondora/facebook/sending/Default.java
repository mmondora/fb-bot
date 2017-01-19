package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Default extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Default.class);
    @Override
    public void run(JsonNode node) {
        LOG.info( "Default");
        String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
        JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
        if( msg != null ) {
            String text = msg.get("text").asText();
            String out = "Grazie per la richiesta '" + text + "'";
            sendTextMessage(id, out);
        }
    }
}

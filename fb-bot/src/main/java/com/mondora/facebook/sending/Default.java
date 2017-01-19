package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Default extends Sender implements Strategy {
    @Override
    public void run(JsonNode node) {
        String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
        JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
        String text = msg.get("text").asText();
        String out = "Grazie per la richiesta '" + text + "'";
        sendTextMessage(id, "Grazie per la richiesta '" + out + "'");
    }
}

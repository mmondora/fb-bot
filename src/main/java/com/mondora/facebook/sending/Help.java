package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Help extends Sender implements Strategy {
    @Override
    public void run(JsonNode node) {
        String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
        sendTextMessage(id, "commands: help, fattura");
    }
}

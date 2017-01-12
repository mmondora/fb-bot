package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.HelloController;

import java.util.UUID;

/**
 * Created by mmondora on 12/01/2017.
 */ //{"object":"page","entry":[{"id":"292024547861549","time":1483540183362,"messaging":[{"sender":{"id":"1250148388409499"},"recipient":{"id":"292024547861549"},"timestamp":1483540183207,"message":{"mid":"mid.1483540183207:5b7bfbd757","seq":16121,"text":"azz"}}]}]}
public class Message extends Sender implements Strategy {

    public void run(JsonNode node) {
        JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
        if (msg != null) {
            String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
            String text = msg.get("text").asText();
            if (text != null)
                if (text.toLowerCase().contains("is_echo")) {
                } else if (text.toLowerCase().contains("help")) {
                    new Help().run(node);
                } else if (text.toLowerCase().contains("fattura")) {
                    String uuid = UUID.randomUUID().toString();
                    String fattua = "fattura da PINCO PALLINO di " + Math.random() * 500 + "€ ";
                    sendStructuredMessage(id, uuid, fattua);
                    Database.addPostbacks(uuid, fattua);
                } else {
                    sendTextMessage(id, "Grazie per la richiesta '" + text + "'");
                }
        }
    }
}

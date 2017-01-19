package com.mondora.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.facebook.sending.*;

/**
 * Created by mmondora on 12/01/2017.
 */
public class StrategyBuilder {
    public static Strategy builder(JsonNode node) {
        JsonNode n = node.get("entry").get(0).get("messaging").get(0).get("optin");
        if (n != null) return new Optin();

        n = node.get("entry").get(0).get("messaging").get(0).get("postback");
        if (n != null) return new PostBack();

        n = node.get("entry").get(0).get("messaging").get(0).get("read");
        if (n != null) return new Read();

        return new Message();
    }

    public static Strategy buildStrategyFromMessage(String text) {
        Strategy out = new Default();
        if (text != null)
            if (text.toLowerCase().contains("is_echo")) {
            } else if (text.toLowerCase().contains("help")) {
                out = new Help();
            } else if (text.toLowerCase().equals("fattura")) {
                out = new FatturaNotification();
            } else if (text.toLowerCase().equals("stats")) {
                out = new Stats();
            } else if (text.toLowerCase().equals("listToday")) {
                out = new ListToday();
            } else if (text.toLowerCase().equals("list")) {
                out = new List();
            }
        return out;
    }

    public static Strategy buildFromPostback( String act, String uuid, String text ) {
        return new PostBackDefault(act, uuid, text );
    }
}

package com.mondora.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.facebook.commands.*;
import com.mondora.facebook.commands.b2b.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 12/01/2017.
 */
public class StrategyBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(StrategyBuilder.class);

    public static Strategy builder(JsonNode node) {
        JsonNode n = node.get("entry").get(0).get("messaging").get(0).get("optin");
        if (n != null) return new Optin();

        n = node.get("entry").get(0).get("messaging").get(0).get("postback");
        if (n != null) return new PostBackHandler();

        n = node.get("entry").get(0).get("messaging").get(0).get("read");
        if (n != null) return new Read();

        n = node.get("entry").get(0).get("messaging").get(0).get("delivery");
        if (n != null) return new Ignore();

        return new MessageHandler();
    }

    public static Strategy buildStrategyFromMessage(String text) {
        Strategy out = new Default();
        if (text != null) {
            String t = text.toLowerCase().trim();
            if (t.contains("echo")) {
            } else if (t.contains("help")) {
                out = new Help();
            } else if (t.equals("fattura")) {
                out = new FatturaNotification();
            } else if (t.equals("stats")) {
                out = new Stats();
            } else if (t.equals("listtoday")) {
                out = new ListToday();
            } else if (t.equals("list")) {
                out = new List();
            } else if (t.startsWith("sethubid")) {
                out = new SettingHubID();
            } else if (t.equals("status")) {
                out = new Status();
            } else if (t.startsWith("subscribe")) {
                out = new Subscribe();
            } else if (t.startsWith("unsubscribe")) {
                out = new Unsubscribe();
            }
        }
        return out;
    }

    public static Strategy buildFromPostback( String act, String uuid, String text ) {
        if( act.equalsIgnoreCase("view")) return new View( act, uuid, text );
        return new PostBackDefault(act, uuid, text );
    }
}

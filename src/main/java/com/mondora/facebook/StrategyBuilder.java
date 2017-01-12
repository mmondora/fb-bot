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
}

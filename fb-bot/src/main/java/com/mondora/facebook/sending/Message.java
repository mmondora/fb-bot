package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Utils;
import com.mondora.facebook.StrategyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 12/01/2017.
 */ //{"object":"page","entry":[{"id":"292024547861549","time":1483540183362,"messaging":[{"sender":{"id":"1250148388409499"},"recipient":{"id":"292024547861549"},"timestamp":1483540183207,"message":{"mid":"mid.1483540183207:5b7bfbd757","seq":16121,"text":"azz"}}]}]}
public class Message extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Default.class);
    public void run(JsonNode node) {
        LOG.debug( "run Message " + Utils.toJson(node) );
        JsonNode msg = node.get("entry").get(0).get("messaging").get(0).get("message");
        if (msg != null) {
            String text = msg.get("text").asText();
            Strategy s = StrategyBuilder.buildStrategyFromMessage(text);
            if( s != null ) s.run(node);
        }
    }
}

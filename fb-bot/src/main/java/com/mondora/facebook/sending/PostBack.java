package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.HelloController;
import com.mondora.facebook.StrategyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 12/01/2017.
 */
public class PostBack extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(PostBack.class);
    @Override
    public void run(JsonNode node) {
        String payload = node.get("entry").get(0).get("messaging").get(0).get("postback").get("payload").asText();
        LOG.debug("Postback -> " + payload);
        int at = payload.indexOf('@');
        if (at > 0) {
            String uuid = payload.substring(at + 1);
            String act = payload.substring(0, at);
            String text = Database.getPostback(uuid);
            if( text != null ) {
                Strategy s = StrategyBuilder.buildFromPostback(act, uuid, text);
                if( s != null ) s.run( node );
            } else
                LOG.debug("Postback  -> " + act + " " + uuid + " : non disponibile" );
        } else {
            Strategy s = StrategyBuilder.buildStrategyFromMessage(payload);
            if( s != null ) s.run(node);
        }
    }
}

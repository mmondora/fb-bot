package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 19/01/2017.
 */
public class PostBackDefault extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(PostBackDefault.class);
    protected String act;
    protected String uuid;
    protected String text;

    public PostBackDefault(String act, String uuid, String text) {
        this.act = act;
        this.uuid = uuid;
        this.text = text;
    }

    @Override
    public void run(JsonNode node) {
        LOG.info( "PostBack default");
        Database.removePostback(uuid);
        String id = getId( node );
        sendTextMessage(id, act + ":" + text);

        // send message to me
        if( LOG.isDebugEnabled() ) sendTextMessage("1253251894751382", "id " + id + " " + act + ":" + text);
    }
}

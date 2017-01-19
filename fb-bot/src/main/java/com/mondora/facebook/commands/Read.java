package com.mondora.facebook.commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Read implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Read.class);

    @Override
    public void run(JsonNode node) {
        if( node != null ) {
            String watermark =  node.get("entry").get(0).get("messaging").get(0).get("read").get("watermark").asText();
            LOG.info("Read message " + watermark );
        }
    }
}

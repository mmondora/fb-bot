package com.mondora.facebook.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Ignore implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Ignore.class);
    @Override
    public void run(JsonNode node) {
        LOG.debug( "IGNORE " + Utils.toJson(node));
    }
}

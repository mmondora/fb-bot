package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by mmondora on 19/01/2017.
 */
public class FatturaNotification extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Default.class);

    @Override
    public void run(JsonNode node) {
        LOG.info("Nuova fattura");
        String id = getId( node );
        String uuid = UUID.randomUUID().toString();
        String fattua = "fattura da PINCO PALLINO di " + Math.random() * 500 + "€ ";
        sendStructuredMessage(id, uuid, fattua);
        Database.addPostbacks(uuid, fattua);
    }
}

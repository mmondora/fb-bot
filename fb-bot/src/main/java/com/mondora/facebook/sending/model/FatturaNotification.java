package com.mondora.facebook.sending.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.sending.Default;
import com.mondora.facebook.sending.Sender;
import com.mondora.facebook.sending.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by mmondora on 19/01/2017.
 */
public class FatturaNotification extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Default.class);
    static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00€");

    @Override
    public void run(JsonNode node) {
        String fattua = "Ricevuta fattura da " + Database.randomCustomer() + " di " + decimalFormat.format(Math.random() * 500) + "€ ";
        LOG.info("Nuova " + fattua );
        String id = getId( node );
        String uuid = UUID.randomUUID().toString();
        sendStructuredMessage(id, uuid, fattua);
        Database.addPostbacks(uuid, fattua);
    }
}

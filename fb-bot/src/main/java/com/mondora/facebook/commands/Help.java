package com.mondora.facebook.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.facebook.Connector;
import com.mondora.facebook.postback.PBElement;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 12/01/2017.
 */
public class Help extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Help.class);

    @Override
    public void run(JsonNode node) {
        LOG.info( "Help");
        TwoChoicePostback o = createPostBack(node);
        PBPayload payload = o.message.attachment.payload;
        PBElement element = payload.addElement("I comandi disponibili sono", "Status, Subscribe, Unsubscribe, Stats, sethubid, list, listToday", null);
        element.addPostbackButton("Simula notifica", "fattura");
        element.addPostbackButton("Lista di oggi", "listToday");
        element.addPostbackButton("Statistiche", "stats");
        sendPostback(o);
    }
}

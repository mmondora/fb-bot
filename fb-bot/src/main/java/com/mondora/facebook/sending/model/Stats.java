package com.mondora.facebook.sending.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.DateUtils;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import com.mondora.facebook.sending.Sender;
import com.mondora.facebook.sending.Strategy;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Stats extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Stats.class);

    static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00€");

    @Override
    public void run(JsonNode node) {
        LOG.info("stats");
        String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
        TwoChoicePostback o = createPostBack(id);
        Collection<Fattura> l = new java.util.ArrayList<Fattura>();
        Database.listFattura().stream().filter(j -> DateUtils.isToday(j.data)).iterator().forEachRemaining(j -> l.add(j));
        int q = l.size();
        double tot = l.stream().mapToDouble(j -> j.importo).sum();
        String textStat = "Oggi hai ricevuto " + q + " fattur" + (q>1?"e":"a");
        PBPayload payload = o.message.attachment.payload;
        payload.addElement(textStat,
                " per un totale di " + decimalFormat.format(tot)
                , "https://app.agyo.io/console/index.html").
                addPostbackButton("Lista", "listToday");
        sendPostback(o);

    }
}

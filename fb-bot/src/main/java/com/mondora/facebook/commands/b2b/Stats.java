package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.DateUtils;
import com.mondora.Utils;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import com.mondora.facebook.Connector;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.FBUser;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by mmondora on 19/01/2017.
 */
public class Stats extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(Stats.class);

    @Override
    public void run(JsonNode node) {
        String id = node.get("entry").get(0).get("messaging").get(0).get("sender").get("id").asText();
        LOG.info("Stats {}", id);
        TwoChoicePostback o = createPostBack(id);
        FBUser user = Database.findUser(id);
        if( user.b2b_id != null ) {
            Collection<Fattura> l = Database.listFattura(user.b2b_id).stream().filter(j -> DateUtils.isToday(j.data)).collect(Collectors.toList());
            int q = l.size();
            double tot = l.stream().mapToDouble(j -> j.importo).sum();
            String textStat = "Oggi hai ricevuto " + q + " fattur" + (q > 1 ? "e" : "a");
            PBPayload payload = o.message.attachment.payload;
            payload.addElement(textStat,
                    " per un totale di " + Utils.DECIMAL_FORMAT.format(tot)
                    , "https://app.agyo.io/console/index.html").
                    addPostbackButton("Lista", "listToday");
            sendPostback(o);
        } else
            sendTextMessage(id,"Mancano le tue credenziali hub b2b.");
    }
}

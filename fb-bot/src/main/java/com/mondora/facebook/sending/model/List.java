package com.mondora.facebook.sending.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import com.mondora.facebook.sending.Sender;
import com.mondora.facebook.sending.Strategy;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 * Created by mmondora on 19/01/2017.
 */
public class List extends Sender implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(List.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("DD-MM-yyyy");
    static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00€");

    public void sendListaFatture(String id, Iterator<Fattura> leneco) {
        LOG.info("sendListaFatture");
        leneco.forEachRemaining(oo ->
                {
                    TwoChoicePostback o = createPostBack(id);
                    PBPayload payload = o.message.attachment.payload;
                    String text = "Fattura del " + sdf.format(oo.data) + " da " + oo.mittente + " " + decimalFormat.format(oo.importo) + "€";
                    payload.addElement(text, null, "https://app.agyo.io/console/index.html").
                            addPostbackButton("view", "View@" + oo.id);
                    sendPostback(o);
                    Database.addPostbacks(oo.id, oo.id);
                }
        );
    }

    @Override
    public void run(JsonNode node) {
        sendListaFatture( getId(node), Database.listFattura().iterator() );
    }
}

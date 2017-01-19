package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import com.mondora.model.Fattura;

import java.util.Iterator;

/**
 * Created by mmondora on 19/01/2017.
 */
public class List extends Sender implements  Strategy {
    public void sendListaFatture(String id, Iterator<Fattura> leneco) {
        TwoChoicePostback o = createPostBack(id);
        PBPayload payload = o.message.attachment.payload;
        leneco.forEachRemaining(oo ->
                {
                    String text = "del " + oo.data + " da " + oo.mittente + " " + oo.importo + "€";
                    payload.addElement("Agyo, fattura", text, "https://app.agyo.io/console/index.html").
                            addPostbackButton("view", "View@" + oo.id);
                    Database.addPostbacks(oo.id, oo.id);
                }
        );
        sendPostback(o);
    }

    @Override
    public void run(JsonNode node) {
        sendListaFatture( getId(node), Database.listFattura().iterator() );
    }
}

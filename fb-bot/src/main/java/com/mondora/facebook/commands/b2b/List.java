package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.Connector;
import com.mondora.facebook.commands.Strategy;
import com.mondora.facebook.postback.PBPayload;
import com.mondora.facebook.postback.TwoChoicePostback;
import com.mondora.model.FBUser;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by mmondora on 19/01/2017.
 */
public class List extends Connector implements Strategy {
    private static final Logger LOG = LoggerFactory.getLogger(List.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("DD-MM-yyyy");
    static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00€");

    public void sendListaFatture(String id, Collection<Fattura> leneco) {
        LOG.info("sendListaFatture {}", id);
        if (leneco.isEmpty())
            sendTextMessage(id, "In questo momento non hai fatture");
        else
            leneco.forEach(oo ->
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
        String id = getId(node);
        sendListaFatture(getId(node), getFatture(id));
    }

    public Collection<Fattura> getFatture(String id) {
        FBUser user = Database.findUser(id);
        if (user.b2b_id != null) return Database.listFattura(user.b2b_id);
        return Collections.EMPTY_LIST;
    }
}

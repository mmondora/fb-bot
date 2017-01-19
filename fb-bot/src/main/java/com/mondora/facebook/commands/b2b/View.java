package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.facebook.commands.PostBackDefault;
import com.mondora.facebook.commands.Strategy;
import com.mondora.model.Fattura;

/**
 * Created by mmondora on 19/01/2017.
 */
public class View extends PostBackDefault implements Strategy {
    public View(String act, String uuid, String text) {
        super(act, uuid, text);
    }

    @Override
    public void run(JsonNode node) {
        Fattura f = Database.findFattura(uuid);
        sendTextMessage( getId(node), "Hai visto la fattura di " + f.mittente + " " + f.importo );
    }
}

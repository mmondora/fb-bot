package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.DateUtils;
import com.mondora.model.Fattura;

import java.util.Iterator;

/**
 * Created by mmondora on 19/01/2017.
 */
public class ListToday extends List {
    @Override
    public void run(JsonNode node) {
        Iterator<Fattura> l = Database.listFattura().stream().filter(o -> DateUtils.isToday(o.data)).iterator();
        sendListaFatture(getId(node), l);
    }
}

package com.mondora.facebook.sending.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.DateUtils;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Created by mmondora on 19/01/2017.
 */
public class ListToday extends List {
    private static final Logger LOG = LoggerFactory.getLogger(ListToday.class);

    @Override
    public void run(JsonNode node) {
        LOG.info("ListToday");
        Iterator<Fattura> l = Database.listFattura().stream().filter(o -> DateUtils.isToday(o.data)).iterator();
        sendListaFatture(getId(node), l);
    }
}

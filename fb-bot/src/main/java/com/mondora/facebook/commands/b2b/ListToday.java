package com.mondora.facebook.commands.b2b;

import com.fasterxml.jackson.databind.JsonNode;
import com.mondora.Database;
import com.mondora.DateUtils;
import com.mondora.model.Fattura;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Created by mmondora on 19/01/2017.
 */
public class ListToday extends List {
    private static final Logger LOG = LoggerFactory.getLogger(ListToday.class);

    @Override
    public void run(JsonNode node) {
        String id = getId(node);
        LOG.info("ListToday {}", id);
        Collection<Fattura> l = getFatture(id).stream().
                filter(o -> DateUtils.isToday(o.data)).
                collect(Collectors.toList());
        sendListaFatture(id, l);
    }
}

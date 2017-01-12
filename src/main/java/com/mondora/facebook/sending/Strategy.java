package com.mondora.facebook.sending;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by mmondora on 12/01/2017.
 */
public interface Strategy {
    public void run(JsonNode node);
}

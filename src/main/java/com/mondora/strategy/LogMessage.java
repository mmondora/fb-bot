package com.mondora.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mondora.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by mmondora on 12/01/2017.
 */
public class LogMessage implements ExecuteStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(LogMessage.class);

    @Override
    public void execute(Event event) {
        try {
            LOG.info( Event.toJSON(event) );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

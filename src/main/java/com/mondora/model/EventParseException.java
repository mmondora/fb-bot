package com.mondora.model;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by mmondora on 12/01/2017.
 */
public class EventParseException extends Exception {
    private static final long serialVersionUID = 648390577952462271L;

    public EventParseException() {
    }

    public EventParseException(String message) {
        super(message);
    }

    public EventParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventParseException(Throwable cause) {
        super(cause);
    }
}

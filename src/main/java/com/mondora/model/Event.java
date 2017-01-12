package com.mondora.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.HashMap;

import static java.lang.System.currentTimeMillis;

/**
 * |__________________________________________________________
 * |   _                      __                              |
 * |  (_) __ _  ___  ___  ___/ /__  _______ _ _______  __ _   |
 * |     /  ' \/ _ \/ _ \/ _  / _ \/ __/ _ `// __/ _ \/  ' \  |
 * |(_) /_/_/_/\___/_//_/\_,_/\___/_/  \_,_(_)__/\___/_/_/_/  |
 * |                 - computing essence -                    |
 * |__________________________________________________________|
 * |
 * Created by atibi on 16/06/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event extends HashMap<String, Object> implements Serializable {
    private static final long serialVersionUID = 5238146525504225931L;

    public static Event fromJSON(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, Event.class);
        } catch (JsonMappingException | JsonParseException e) {
            throw new EventParseException(e.getMessage(), e);
        }
    }

    public static String toJSON(Event event) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(event);
    }

    public static Event build(Event event) {
        Event e = build();
        e.setRequestId(event.getRequestId());
        e.setUser(event.getUser());
        e.setAppName(event.getAppName());
        e.setTimestamp(currentTimeMillis());
        return e;
    }

    public static Event build() {
        return new Event();
    }

    public String getUser() {
        return getAsString("user");
    }

    public void setUser(String user) {
        this.put("user", user);
    }

    public String getAppName() {
        return getAsString("appName");
    }

    public void setAppName(String appName) {
        this.put("appName", appName);
    }

    public String getId() {
        return getAsString("id");
    }

    public void setId(String id) {
        this.put("id", id);
    }

    public String getRequestId() {
        return getAsString("requestId");
    }

    public void setRequestId(String requestId) {
        this.put("requestId", requestId);
    }

    public String getEventSource() {
        return getAsString("eventSource");
    }

    public void setEventSource(String eventSource) {
        this.put("eventSource", eventSource);
    }

    public String getEventName() {
        return getAsString("eventName");
    }

    public void setEventName(String eventName) {
        this.put("eventName", eventName);
    }

    public Long getTimestamp() {
        return getAsLong("timestamp");
    }

    public void setTimestamp(Long timestamp) {
        this.put("timestamp", timestamp);
    }

    public String getAsString(String key) {
        return (String) this.get(key);
    }

    public Long getAsLong(String key) {
        return (Long) this.get(key);
    }
}


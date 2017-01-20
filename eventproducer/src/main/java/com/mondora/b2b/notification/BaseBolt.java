package com.mondora.b2b.notification;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Tuple;
import com.mondora.teamsystem.hub.b2b.event.EventHash;
import com.mondora.teamsystem.hub.b2b.event.Status;
import com.mondora.teamsystem.hub.b2b.state.Event;
import com.mondora.teamsystem.hub.eventhub.EventHubSendClient;
import com.mondora.teamsystem.hub.eventhub.EventHubSendClientFactory;
import com.mondora.teamsystem.hub.storage.StorageFile;
import com.mondora.teamsystem.hub.storage.StorageFileImpl;
import com.mondora.teamsystem.hub.storm.BaseEventHubBolt;
import com.mondora.teamsystem.hub.utils.json.JsonParseException;
import com.mondora.teamsystem.hub.utils.json.JsonSerializerFactory;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Map;

/**
 * Created by mmondora on 13/01/2017.
 */
public abstract class BaseBolt extends BaseEventHubBolt {
    private static final long serialVersionUID = -6179789893877330889L;
    private transient static final Logger LOG = LoggerFactory.getLogger(BaseBolt.class);
    private transient EventHubSendClient eventHubSendClient;
    private transient JsonSerializerFactory jsonSerializerFactory;

    protected JsonSerializerFactory getJsonSerializerFactory() {
        return jsonSerializerFactory;
    }

    protected EventHubSendClient getEventHubSendClient() {
        return eventHubSendClient;
    }

    @Override
    protected void internalPrepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.eventHubSendClient = EventHubSendClientFactory.getInstance().getEventHubSendClient();
        this.jsonSerializerFactory = new JsonSerializerFactory();
    }

    // TODO non fa fail e ack ma lancia l'eccezione
    protected void emitEvent(Status event, Tuple tuple, String eventName, String eventDescription, String eventSource) {
        if (LOG.isInfoEnabled()) {
            ThreadContext.put("action", "emitEvent");
            ThreadContext.put("step", "input");
            ThreadContext.put("payload", event.toString());
            LOG.info("Emitting event {}", eventName);
        }
        event.setEventName(eventName);
        event.setStatusDescription(eventDescription);
        event.setTimestamp(System.currentTimeMillis());
        event.setEventSource(eventSource);
        event.setEventHash(EventHash.create(event.getHubId(), event.getAvailableTo(), event.getEventName(), event.getRequestId(), event.getStorageRef()));
        String eventAsString;
        try {
            eventAsString = jsonSerializerFactory.toJson(event);
            eventHubSendClient.send(eventAsString);
            if (LOG.isInfoEnabled()) {
                ThreadContext.put("step", "output");
                LOG.info("Emitted event {}", eventName);
            }
            ack(tuple);
        } catch (Exception e) {
            ThreadContext.put("step", "output");
            ThreadContext.put("error_code", "500");
            LOG.error("500 Internal Server Error. " + e.getMessage(), e);
            fail(tuple, e);
        }
    }

    /**
     * Deserialize tuple into Event object, ack if deserialize fails
     *
     * @param tuple
     * @return Event
     */
    protected Event deserializeEvent(final Tuple tuple) {
        return deserialize(tuple, Event.class);
    }

//    /**
//     * Deserialize tuple into PassiveSdiEvent object, ack if deserialize fails
//     *
//     * @param tuple
//     * @return PassiveSdiEvent
//     */
//    protected PassiveSdiEvent deserializePassiveSdiEvent(final Tuple tuple) {
//        return deserialize(tuple, PassiveSdiEvent.class);
//    }

    /**
     * Deserialize tuple into Status object, ack if deserialize fails
     *
     * @param tuple
     * @return Status
     */
    protected Status deserializeStatus(final Tuple tuple) {
        return deserialize(tuple, Status.class);
    }

    private <T> T deserialize(final Tuple tuple, Class claz) {
        if (LOG.isDebugEnabled()) {
            ThreadContext.put("action", "deserialize");
            ThreadContext.put("step", "input");
            ThreadContext.put("payload", tuple.toString());
            LOG.info("Deserialize {}", claz.getSimpleName());
        }

        String jsonEvent = tuple.getString(0);
        T object = null;
        try {
            object = (T) jsonSerializerFactory.fromJson(jsonEvent, claz);
            if (LOG.isInfoEnabled()) {
                LOG.info("Deserialized {}", claz.getSimpleName());
            }
        } catch (JsonParseException e) {
            ThreadContext.put("step", "output");
            ThreadContext.put("error_code", "500");
            LOG.error("500 Internal Server Error. " + e.getMessage(), e);
            ack(tuple);
        }
        return object;
    }

    protected StorageFile getStorageFile(String storageConnectionString) throws URISyntaxException, InvalidKeyException {
        return new StorageFileImpl(storageConnectionString);
    }
}

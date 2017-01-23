package com.mondora.b2b.notification;

/**
 * Created by mmondora on 13/01/2017.
 */

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mondora.teamsystem.hub.b2b.event.Status;
import com.mondora.teamsystem.hub.servicebus.TopicHttpClientFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import java.util.Map;

/**
 * |__________________________________________________________
 * |   _                      __                              |
 * |  (_) __ _  ___  ___  ___/ /__  _______ _ _______  __ _   |
 * |     /  ' \/ _ \/ _ \/ _  / _ \/ __/ _ `// __/ _ \/  ' \  |
 * |(_) /_/_/_/\___/_//_/\_,_/\___/_/  \_,_(_)__/\___/_/_/_/  |
 * |                 - computing essence -                    |
 * |__________________________________________________________|
 * |
 * Author Davide Pedone <davide.pedone@mondora.com>
 * 15/06/16
 */
public class PublishOnServiceBusBolt extends BaseBolt {
    private transient static final Logger LOG = LogManager.getLogger(PublishOnServiceBusBolt.class);

    public static String toJson(Object o) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void internalPrepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    }

    @Override
    public void execute(Tuple tuple) {
        long now = System.currentTimeMillis();
        ThreadContext.clearAll();
        ThreadContext.put("action", "execute");
        ThreadContext.put("step", "input");
        LOG.info("Filter event");
        Status event = (Status) tuple.getValueByField("status");
        if (event != null && event.isValid()) {
            String json = toJson(event);
            if (json != null) {
                publishOnServiceBus(event);
                ThreadContext.put("step", "output");
                ThreadContext.put("exec_time", String.valueOf(System.currentTimeMillis() - now));
                LOG.info("Event emitted: {}", event.toString());
            }
            emit(tuple, new Values(event));
        }
        ack(tuple);
    }

    private void publishOnServiceBus(Status event) {
        String json = toJson(event);
        TopicHttpClientFactory.getInstance().getTopicHttpClient().send("{}");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("status"));
    }

}
package com.mondora;

import com.mondora.model.Event;
import com.mondora.model.EventParseException;

import com.mondora.strategy.StrategyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.TextMessage;


/**
 * Created by mmondora on 12/01/2017.
 */
@Component
public class MessageConsumerListener implements javax.jms.MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumerListener.class);
    private final StrategyService strategyService;

    @Autowired
    public MessageConsumerListener(final StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @JmsListener(destination = "${servicebustopic.topicname}",
            containerFactory = "jmsListenerContainerFactory",
            subscription = "${servicebustopic.subscription_name}" )
    @Override
    public void onMessage(final Message message) {
        long now = System.currentTimeMillis();
        MDC.put("action", "onMessage");

        if (!(message instanceof TextMessage)) {
//            LOG.error(append("step", "output").and(append("exec_time", System.currentTimeMillis() - now)).and(append("error_code", "400")), "400 Bad Request. Received unexpected message type: " + message.getClass().getName());
            LOG.error("400 Bad Request. Received unexpected message type: " + message.getClass().getName() );
            return;
        }
        String json = null;
        try {
            json = ((TextMessage) message).getText();
            if (LOG.isDebugEnabled()) {
//                LOG.debug(append("step", "input").and(append("payload", json)), "Received new event.");
                LOG.debug("Received new event.");
            }
            Event event = Event.fromJSON(json);
            updateMDC(event);
            strategyService.execute(event);
            if (LOG.isInfoEnabled()) {
                LOG.info( "ACK.");
//                LOG.info(append("step", "output").and(append("payload", json)).and(append("exec_time", System.currentTimeMillis() - now)), "ACK.");
            }
        } catch (EventParseException e) {
//            LOG.error(append("step", "output").and(append("exec_time", String.valueOf(System.currentTimeMillis() - now))).and(append("error_code", "400")), "400 Bad Request. Received unexpected json: " + json);
            LOG.error("400 Bad Request. Received unexpected json: " + json);
        } catch (Exception e) {
//            LOG.error(append("step", "output").and(append("exec_time", System.currentTimeMillis() - now)).and(append("error_code", "500")), "500 Internal Server Error. " + e.getMessage(), e);
            LOG.error("500 Internal Server Error. " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void updateMDC(Event event) {
        MDC.put("request_id", event.getRequestId());
        MDC.put("user", event.getUser());
        MDC.put("app_name", event.getAppName());
    }
}

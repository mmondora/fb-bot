package com.mondora.b2b.notification;

import com.mondora.teamsystem.hub.servicebus.ServiceBusClientException;
import com.mondora.teamsystem.hub.servicebus.TopicHttpClientFactory;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by mmondora on 20/01/2017.
 */
public class Messages {

    @Test
    public void send() {
        System.setProperty("servicebustopic_policyname", "write");
//        System.setProperty("servicebustopic.policykey", "vIbsS6i+RIQyjNzgKfZxIHIsistDKTaEbus2UVX4JYw=");
        System.setProperty("servicebustopic_hostname", "bus-b2bhub-dev.servicebus.windows.net");
        System.setProperty("servicebustopic_topicname", "b2b_notification");
        try {
            TopicHttpClientFactory.getInstance().getTopicHttpClient().send("{\"status\":\"Il mio amico FoodEmperor!\"}");
            fail("must not be here");
        } catch (ServiceBusClientException e) {
            assertEquals("404", e.getCode());
        }
    }

    @Test
    public void sendAndReceive() throws JMSException {
        System.setProperty("servicebustopic_policyname", "write");
        System.setProperty("servicebustopic_policykey", "vIbsS6i+RIQyjNzgKfZxIHIsistDKTaEbus2UVX4JYw=");
        System.setProperty("servicebustopic_hostname", "bus-b2bhub-dev.servicebus.windows.net");
        System.setProperty("servicebustopic_topicname", "b2b_notification");
        TopicHttpClientFactory.getInstance().getTopicHttpClient().send("{\"status\":\"Il mio amico FoodEmperor!\"}");

        System.out.println("Hei");
        ConnectionFactory jmsConnectionFactory =
                jmsConnectionFactory("read", "toEVLa/1wVXHNSCu9PvUJiQTNiBXc4tRDLpTAaynJJk=", "bus-b2bhub-dev.servicebus.windows.net", "fbbot");
        Connection connection = jmsConnectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic( "b2b_notification" );

        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                System.out.println("Message " + message);
            }
        });

        connection.close();
    }

    public ConnectionFactory jmsConnectionFactory(@Value("${BUS_USER}") final String username,
                                                  @Value("${BUS_PASS}") final String password,
                                                  @Value("${servicebustopic_hostname}") final String hostname,
                                                  @Value("${info.build.name}") final String clientId) {
        String urlString = String.format("amqps://%1s?amqp.idleTimeout=3600000", hostname);
        JmsConnectionFactory jmsConnectionFactory = new JmsConnectionFactory(urlString);
        jmsConnectionFactory.setUsername(username);
        jmsConnectionFactory.setPassword(password);
        jmsConnectionFactory.setClientID(clientId);
        jmsConnectionFactory.setReceiveLocalOnly(true);
        return new CachingConnectionFactory(jmsConnectionFactory);
    }

    public JmsListenerContainerFactory jmsListenerContainerFactory(final ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(connectionFactory);
        defaultJmsListenerContainerFactory.setSubscriptionDurable(Boolean.TRUE);
        defaultJmsListenerContainerFactory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return defaultJmsListenerContainerFactory;
    }

}

@Component
class MessageConsumerListener implements javax.jms.MessageListener {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumerListener.class);

    @JmsListener(destination = "${servicebustopic_topicname}",
            containerFactory = "jmsListenerContainerFactory",
            subscription = "${servicebustopic_subscription_name}")
    @Override
    public void onMessage(final Message message) {
        long now = System.currentTimeMillis();
        MDC.put("action", "onMessage");

        if (!(message instanceof TextMessage)) {
//            LOG.error(append("step", "output").and(append("exec_time", System.currentTimeMillis() - now)).and(append("error_code", "400")), "400 Bad Request. Received unexpected message type: " + message.getClass().getName());
            LOG.error("400 Bad Request. Received unexpected message type: " + message.getClass().getName());
            return;
        }
        String json = null;
        try {
            json = ((TextMessage) message).getText();
            if (LOG.isDebugEnabled()) {
//                LOG.debug(append("step", "input").and(append("payload", json)), "Received new event.");
                LOG.debug("Received new event.");
            }
        } catch (Exception e) {
//            LOG.error(append("step", "output").and(append("exec_time", System.currentTimeMillis() - now)).and(append("error_code", "500")), "500 Internal Server Error. " + e.getMessage(), e);
            LOG.error("500 Internal Server Error. " + e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}


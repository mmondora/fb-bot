package com.mondora.configuration;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

/**
 * |__________________________________________________________
 * |   _                      __                              |
 * |  (_) __ _  ___  ___  ___/ /__  _______ _ _______  __ _   |
 * |     /  ' \/ _ \/ _ \/ _  / _ \/ __/ _ `// __/ _ \/  ' \  |
 * |(_) /_/_/_/\___/_//_/\_,_/\___/_/  \_,_(_)__/\___/_/_/_/  |
 * |                 - computing essence -                    |
 * |__________________________________________________________|
 * |
 * Created by atibi on 03/07/16.
 */
@Configuration
public class JMSConfiguration {

    @Bean
    public ConnectionFactory jmsConnectionFactory(@Value("${BUS_USER}") final String username,
                                                  @Value("${BUS_PASS}") final String password,
                                                  @Value("${servicebustopic.hostname}") final String hostname,
                                                  @Value("${info.build.name}") final String clientId) {
        String urlString = String.format("amqps://%1s?amqp.idleTimeout=3600000", hostname);
        JmsConnectionFactory jmsConnectionFactory = new JmsConnectionFactory(urlString);
        jmsConnectionFactory.setUsername(username);
        jmsConnectionFactory.setPassword(password);
        jmsConnectionFactory.setClientID(clientId);
        jmsConnectionFactory.setReceiveLocalOnly(true);
        return new CachingConnectionFactory(jmsConnectionFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(final ConnectionFactory jmsConnectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(jmsConnectionFactory);
        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory(final ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory defaultJmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();
        defaultJmsListenerContainerFactory.setConnectionFactory(connectionFactory);
        defaultJmsListenerContainerFactory.setSubscriptionDurable(Boolean.TRUE);
        defaultJmsListenerContainerFactory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return defaultJmsListenerContainerFactory;
    }
}
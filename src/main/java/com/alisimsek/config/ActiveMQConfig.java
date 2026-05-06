package com.alisimsek.config;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.util.ErrorHandler;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJms
@RequiredArgsConstructor
public class ActiveMQConfig {

    private final RedeliveryPolicyProperties redeliveryPolicyProperties;

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String username;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${app.jms.listener.concurrencyRange:5}")
    private String listenerConcurrencyRange;

    private static final String TYPE = "_type";
    private static final String JMS_ERROR_HANDLER = "JmsErrorHandler";

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        
        // Configure redelivery policy for dead letter queue
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setMaximumRedeliveries(redeliveryPolicyProperties.getMaximumRedeliveries());
        redeliveryPolicy.setInitialRedeliveryDelay(redeliveryPolicyProperties.getInitialRedeliveryDelay());
        redeliveryPolicy.setRedeliveryDelay(redeliveryPolicyProperties.getRedeliveryDelay());
        redeliveryPolicy.setUseExponentialBackOff(redeliveryPolicyProperties.isUseExponentialBackoff());
        redeliveryPolicy.setBackOffMultiplier(redeliveryPolicyProperties.getBackOffMultiplier());

        connectionFactory.setRedeliveryPolicy(redeliveryPolicy);

        return connectionFactory;
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        JsonMapper mapper = new JsonMapper();
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(mapper);
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName(TYPE);

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("com.alisimsek.messaging.BaseMessage", com.alisimsek.messaging.BaseMessage.class);
        typeIdMappings.put("com.alisimsek.messaging.TrainerWorkloadMessage", com.alisimsek.messaging.TrainerWorkloadMessage.class);
        converter.setTypeIdMappings(typeIdMappings);
        return converter;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setDeliveryPersistent(true);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory, 
                                                                         MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setConcurrency(listenerConcurrencyRange);
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.SESSION_TRANSACTED);
        factory.setErrorHandler(new ErrorHandler() {
            @Override
            public void handleError(Throwable t) {
                org.slf4j.LoggerFactory.getLogger(JMS_ERROR_HANDLER).error("JMS listener error: {}", t.getMessage(), t);
            }
        });
        return factory;
    }
}


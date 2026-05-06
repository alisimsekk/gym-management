package com.alisimsek.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "activemq.redelivery-policy")
@Data
public class RedeliveryPolicyProperties {

    private int maximumRedeliveries;
    private long initialRedeliveryDelay;
    private long redeliveryDelay;
    private boolean useExponentialBackoff;
    private double backOffMultiplier;
}

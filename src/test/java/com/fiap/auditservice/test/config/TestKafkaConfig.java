package com.fiap.auditservice.test.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestKafkaConfig {

    @Bean
    @Primary
    public KafkaProperties kafkaProperties() {
        KafkaProperties props = new KafkaProperties();
        props.getBootstrapServers().clear();
        props.getBootstrapServers().add("localhost:9092");
        return props;
    }
}
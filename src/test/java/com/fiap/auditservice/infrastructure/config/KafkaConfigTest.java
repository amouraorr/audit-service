package com.fiap.auditservice.infrastructure.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

    @Mock
    private KafkaProperties kafkaProperties;

    @InjectMocks
    private KafkaConfig kafkaConfig;

    @Test
    @DisplayName("Deve criar factory com deserializadores de String por padrão")
    void shouldCreateFactoryWithStringDeserializers() {
        // Arrange
        Map<String, Object> props = new HashMap<>();
        when(kafkaProperties.buildConsumerProperties()).thenReturn(props);

        // Act
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                kafkaConfig.kafkaListenerContainerFactory(kafkaProperties);

        // Assert
        assertNotNull(factory, "Factory não deve ser nulo");
        ConsumerFactory<String, String> consumerFactory = (ConsumerFactory<String, String>) factory.getConsumerFactory();
        assertNotNull(consumerFactory, "ConsumerFactory não deve ser nulo");
        assertTrue(consumerFactory instanceof DefaultKafkaConsumerFactory, "Deve ser DefaultKafkaConsumerFactory");

        @SuppressWarnings("unchecked")
        Map<String, Object> configs = ((DefaultKafkaConsumerFactory<String, String>) consumerFactory).getConfigurationProperties();
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG),
                "KEY_DESERIALIZER_CLASS_CONFIG deve ser StringDeserializer");
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG),
                "VALUE_DESERIALIZER_CLASS_CONFIG deve ser StringDeserializer");

        // Verify
        verify(kafkaProperties, times(1)).buildConsumerProperties();
    }

    @Test
    @DisplayName("Deve sobrescrever deserializadores existentes para String")
    void shouldOverrideExistingDeserializersWithString() {
        // Arrange
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Object.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, Object.class);
        props.put("some.other.config", "value");
        when(kafkaProperties.buildConsumerProperties()).thenReturn(props);

        // Act
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                kafkaConfig.kafkaListenerContainerFactory(kafkaProperties);

        // Assert
        assertNotNull(factory);
        @SuppressWarnings("unchecked")
        Map<String, Object> configs = ((DefaultKafkaConsumerFactory<String, String>) factory.getConsumerFactory()).getConfigurationProperties();

        // Asserts that previously set values were overwritten by StringDeserializer
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG),
                "KEY_DESERIALIZER_CLASS_CONFIG deve ser sobrescrito com StringDeserializer");
        assertEquals(StringDeserializer.class, configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG),
                "VALUE_DESERIALIZER_CLASS_CONFIG deve ser sobrescrito com StringDeserializer");

        assertEquals("value", configs.get("some.other.config"));

        // Verify
        verify(kafkaProperties, times(1)).buildConsumerProperties();
    }
}
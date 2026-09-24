package com.example.errorprocessor.config;

import com.example.errorprocessor.event.ErrorEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, ErrorEvent> consumerFactory() {

        Map<String, Object> config = new HashMap<>();

        // Kafka broker
        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        // Consumer group
        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                "error-processor-group"
        );

        // Key deserializer
        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        // Value deserializer
        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JacksonJsonDeserializer.class
        );

        // Allow our ErrorEvent package
        config.put(
                JacksonJsonDeserializer.TRUSTED_PACKAGES,
                "com.example.errorprocessor.event"
        );

        // Tell Jackson which class to create from the JSON
        config.put(
                JacksonJsonDeserializer.VALUE_DEFAULT_TYPE,
                ErrorEvent.class
        );

        // Ignore the producer's __TypeId__ header
        config.put(
                JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS,
                false
        );

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ErrorEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, ErrorEvent> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, ErrorEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}
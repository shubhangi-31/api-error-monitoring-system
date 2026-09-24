package com.example.apierrormonitor.kafka;

import com.example.apierrormonitor.event.ErrorEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaErrorProducer {

    private static final String TOPIC = "api-errors";

    private final KafkaTemplate<String, ErrorEvent> kafkaTemplate;

    public KafkaErrorProducer(KafkaTemplate<String, ErrorEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendError(ErrorEvent errorEvent) {
        kafkaTemplate.send(
                TOPIC,
                errorEvent.getEndpoint(),
                errorEvent
        );
    }
}
package com.example.apierrormonitor.kafka;

import com.example.apierrormonitor.event.ErrorEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

class KafkaErrorProducerTest {

    @Test
    void shouldSendErrorEventToKafka() {

        KafkaTemplate<String, ErrorEvent> kafkaTemplate =
                mock(KafkaTemplate.class);

        KafkaErrorProducer producer =
                new KafkaErrorProducer(kafkaTemplate);

        ErrorEvent event = new ErrorEvent(
                "2026-09-24T16:00:00",
                "api-error-monitor",
                "/api/orders/999",
                "GET",
                404,
                "OrderNotFoundException",
                "Order with id 999 not found",
                "request-123"
        );

        producer.sendError(event);

        verify(kafkaTemplate).send(
                "api-errors",
                "/api/orders/999",
                event
        );
    }
}
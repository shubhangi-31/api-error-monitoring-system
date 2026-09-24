package com.example.errorprocessor.kafka;

import com.example.errorprocessor.entity.ErrorLog;
import com.example.errorprocessor.event.ErrorEvent;
import com.example.errorprocessor.repository.ErrorLogRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ErrorEventConsumerTest {

    @Test
    void shouldSaveErrorEventToDatabase() {

        ErrorLogRepository repository = mock(ErrorLogRepository.class);

        ErrorEventConsumer consumer =
                new ErrorEventConsumer(repository);

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

        consumer.consume(event);

        verify(repository).save(any(ErrorLog.class));
    }
}
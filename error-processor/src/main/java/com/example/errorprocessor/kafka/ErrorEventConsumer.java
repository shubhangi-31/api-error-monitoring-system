package com.example.errorprocessor.kafka;

import com.example.errorprocessor.entity.ErrorLog;
import com.example.errorprocessor.event.ErrorEvent;
import com.example.errorprocessor.repository.ErrorLogRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.stereotype.Service;

@Service
public class ErrorEventConsumer {

    private final ErrorLogRepository errorLogRepository;

    public ErrorEventConsumer(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    @KafkaListener(
            topics = "api-errors",
            groupId = "error-processor-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 1000)
    )
    public void consume(ErrorEvent errorEvent) {
//        throw new RuntimeException("Testing Kafka retry and DLT");

        System.out.println("Received error event:");
        System.out.println(errorEvent);

        ErrorLog errorLog = new ErrorLog(
                errorEvent.getTimestamp(),
                errorEvent.getServiceName(),
                errorEvent.getEndpoint(),
                errorEvent.getHttpMethod(),
                errorEvent.getStatusCode(),
                errorEvent.getErrorType(),
                errorEvent.getMessage(),
                errorEvent.getRequestId()
        );

        errorLogRepository.save(errorLog);

        System.out.println("Error saved to database.");
    }
}
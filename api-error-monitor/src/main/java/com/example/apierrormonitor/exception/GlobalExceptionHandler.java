package com.example.apierrormonitor.exception;

import com.example.apierrormonitor.event.ErrorEvent;
import com.example.apierrormonitor.kafka.KafkaErrorProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final KafkaErrorProducer producer;

    public GlobalExceptionHandler(KafkaErrorProducer producer) {
        this.producer = producer;
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFound(
            OrderNotFoundException ex,
            HttpServletRequest request) {
        String requestId = UUID.randomUUID().toString();

        ErrorEvent event = new ErrorEvent(
                LocalDateTime.now().toString(),
                "api-error-monitor",
                request.getRequestURI(),
                request.getMethod(),
                404,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                requestId
        );

        producer.sendError(event);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request) {

        String requestId = UUID.randomUUID().toString();

        ErrorEvent event = new ErrorEvent(
                LocalDateTime.now().toString(),
                "api-error-monitor",
                request.getRequestURI(),
                request.getMethod(),
                500,
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                requestId
        );

        producer.sendError(event);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error");
    }
}
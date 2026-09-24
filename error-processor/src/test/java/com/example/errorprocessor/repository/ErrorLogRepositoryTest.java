package com.example.errorprocessor.repository;

import com.example.errorprocessor.config.RetrySchedulerConfig;
import com.example.errorprocessor.entity.ErrorLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(RetrySchedulerConfig.class)
class ErrorLogRepositoryTest {

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Test
    void shouldFindErrorsByStatusCode() {

        ErrorLog error = new ErrorLog(
                "2026-09-24T16:00:00",
                "api-error-monitor",
                "/api/orders/999",
                "GET",
                404,
                "OrderNotFoundException",
                "Order with id 999 not found",
                "request-123"
        );

        errorLogRepository.save(error);

        List<ErrorLog> results =
                errorLogRepository.findByStatusCode(404);

        assertEquals(1, results.size());
        assertEquals(404, results.get(0).getStatusCode());
    }

    @Test
    void shouldFindErrorsByErrorType() {

        ErrorLog error = new ErrorLog(
                "2026-09-24T16:00:00",
                "api-error-monitor",
                "/api/orders/999",
                "GET",
                404,
                "OrderNotFoundException",
                "Order with id 999 not found",
                "request-123"
        );

        errorLogRepository.save(error);

        List<ErrorLog> results =
                errorLogRepository.findByErrorType(
                        "OrderNotFoundException"
                );

        assertEquals(1, results.size());
        assertEquals(
                "OrderNotFoundException",
                results.get(0).getErrorType()
        );
    }
}
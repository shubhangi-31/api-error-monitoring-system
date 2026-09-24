package com.example.errorprocessor.controller;

import com.example.errorprocessor.config.RetrySchedulerConfig;
import com.example.errorprocessor.entity.ErrorLog;
import com.example.errorprocessor.exception.GlobalExceptionHandler;
import com.example.errorprocessor.repository.ErrorLogRepository;
import com.example.errorprocessor.service.ErrorStatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ErrorLogController.class)
@Import({
        GlobalExceptionHandler.class,
        RetrySchedulerConfig.class
})
class ErrorLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ErrorLogRepository errorLogRepository;

    @MockitoBean
    private ErrorStatisticsService errorStatisticsService;

    @Test
    void shouldReturnAllErrors() throws Exception {

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

        when(errorLogRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(error)));

        mockMvc.perform(get("/api/errors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].endpoint")
                        .value("/api/orders/999"))
                .andExpect(jsonPath("$.content[0].statusCode")
                        .value(404));
    }

    @Test
    void shouldReturnErrorById() throws Exception {

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

        when(errorLogRepository.findById(1L))
                .thenReturn(Optional.of(error));

        mockMvc.perform(get("/api/errors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoint")
                        .value("/api/orders/999"))
                .andExpect(jsonPath("$.statusCode")
                        .value(404));
    }

    @Test
    void shouldReturn404WhenErrorDoesNotExist() throws Exception {

        when(errorLogRepository.findById(99999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/errors/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.message")
                        .value("Error not found with id: 99999"));
    }

    @Test
    void shouldReturnErrorsByStatusCode() throws Exception {

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

        when(errorLogRepository.findByStatusCode(404))
                .thenReturn(List.of(error));

        mockMvc.perform(get("/api/errors/status/404"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].statusCode")
                        .value(404));
    }

    @Test
    void shouldReturnErrorsByType() throws Exception {

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

        when(errorLogRepository.findByErrorType(
                "OrderNotFoundException"
        )).thenReturn(List.of(error));

        mockMvc.perform(
                        get("/api/errors/type/OrderNotFoundException")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].errorType")
                        .value("OrderNotFoundException"));
    }
    @Test
    void shouldReturnErrorsBetweenTimestamps() throws Exception {

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

        when(errorLogRepository.findByTimestampBetween(
                "2026-09-24T00:00:00",
                "2026-09-24T23:59:59"
        )).thenReturn(List.of(error));

        mockMvc.perform(
                        get("/api/errors/filter")
                                .param("from", "2026-09-24T00:00:00")
                                .param("to", "2026-09-24T23:59:59")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].endpoint")
                        .value("/api/orders/999"))
                .andExpect(jsonPath("$[0].statusCode")
                        .value(404));
    }
}
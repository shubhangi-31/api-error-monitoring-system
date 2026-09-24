package com.example.apierrormonitor.controller;

import com.example.apierrormonitor.exception.GlobalExceptionHandler;
import com.example.apierrormonitor.exception.OrderNotFoundException;
import com.example.apierrormonitor.kafka.KafkaErrorProducer;
import com.example.apierrormonitor.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private KafkaErrorProducer kafkaErrorProducer;

    @Test
    void shouldReturnOrder() throws Exception {

        when(orderService.getOrder(1L))
                .thenReturn("Order 1");

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order 1"));
    }

    @Test
    void shouldReturn404WhenOrderNotFound() throws Exception {

        when(orderService.getOrder(999L))
                .thenThrow(new OrderNotFoundException(
                        "Order with id 999 not found"
                ));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(
                        "Order with id 999 not found"
                ));
    }
    @Test
    void shouldReturn500ForUnexpectedError() throws Exception {

        when(orderService.getOrder(500L))
                .thenThrow(new RuntimeException(
                        "Unexpected error while processing order"
                ));

        mockMvc.perform(get("/api/orders/500"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }
}
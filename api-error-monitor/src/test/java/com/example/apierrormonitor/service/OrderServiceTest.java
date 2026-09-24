package com.example.apierrormonitor.service;

import com.example.apierrormonitor.exception.OrderNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private final OrderService orderService = new OrderService();

    @Test
    void shouldReturnOrderWhenIdExists() {

        String result = orderService.getOrder(1L);

        assertEquals("Order 1", result);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        assertThrows(
                OrderNotFoundException.class,
                () -> orderService.getOrder(999L)
        );
    }
    @Test
    void shouldThrowRuntimeExceptionForOrder500() {

        OrderService service = new OrderService();

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.getOrder(500L)
        );

        assertEquals(
                "Unexpected error while processing order",
                exception.getMessage()
        );
    }
}
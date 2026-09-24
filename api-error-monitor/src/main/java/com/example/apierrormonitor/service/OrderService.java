package com.example.apierrormonitor.service;

import com.example.apierrormonitor.exception.OrderNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public String getOrder(Long id) {

        if (id == 999) {
            throw new OrderNotFoundException(
                    "Order with id " + id + " not found"
            );
        }

        if (id == 500) {
            throw new RuntimeException(
                    "Unexpected error while processing order"
            );
        }

        return "Order " + id;
    }
}
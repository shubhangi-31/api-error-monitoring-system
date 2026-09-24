package com.example.apierrormonitor.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message){
        super(message);
    }
}

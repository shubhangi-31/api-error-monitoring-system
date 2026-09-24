package com.example.apierrormonitor.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorEvent {

    private String timestamp;
    private String serviceName;
    private String endpoint;
    private String httpMethod;
    private int statusCode;
    private String errorType;
    private String message;
    private String requestId;}
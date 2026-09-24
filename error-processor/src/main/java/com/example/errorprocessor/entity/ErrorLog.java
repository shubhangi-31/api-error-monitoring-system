package com.example.errorprocessor.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "error_logs")
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String timestamp;

    private String serviceName;

    private String endpoint;

    private String httpMethod;

    private int statusCode;

    private String errorType;

    @Column(length = 1000)
    private String message;

    private String requestId;

    public ErrorLog() {
    }

    public ErrorLog(
            String timestamp,
            String serviceName,
            String endpoint,
            String httpMethod,
            int statusCode,
            String errorType,
            String message,
            String requestId) {

        this.timestamp = timestamp;
        this.serviceName = serviceName;
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.statusCode = statusCode;
        this.errorType = errorType;
        this.message = message;
        this.requestId=requestId;
    }

    public Long getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getMessage() {
        return message;
    }
    public String getRequestId() {
        return requestId;
    }
}
package com.example.errorprocessor.exception;

public class ErrorNotFoundException extends RuntimeException{
    public ErrorNotFoundException(String message){
        super(message);
    }
}

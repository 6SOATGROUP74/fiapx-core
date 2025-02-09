package com.example.demo.exceptions;

public class PagamentoNotFoundException extends RuntimeException {

    public PagamentoNotFoundException(String message) {
        super(message);
    }

    public PagamentoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

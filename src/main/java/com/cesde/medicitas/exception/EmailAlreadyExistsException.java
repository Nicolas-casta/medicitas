package com.cesde.medicitas.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String msg) { super(msg); }
}

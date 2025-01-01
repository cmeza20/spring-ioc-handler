package com.cmeza.spring.ioc.handler.exceptions;

public class IocException extends RuntimeException {
    public IocException() {
    }

    public IocException(String message) {
        super(message);
    }

    public IocException(Throwable cause) {
        super(cause);
    }
}

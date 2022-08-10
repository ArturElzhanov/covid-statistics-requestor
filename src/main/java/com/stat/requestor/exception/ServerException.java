package com.stat.requestor.exception;

public class ServerException extends RuntimeException {
    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}

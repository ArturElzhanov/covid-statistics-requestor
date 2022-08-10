package com.stat.requestor.exception;


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String country) {
        super(String.format("Resource: %s not found for country: %s", resourceName, country));
    }
}

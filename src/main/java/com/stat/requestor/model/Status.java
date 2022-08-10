package com.stat.requestor.model;

public enum Status {
    CONFIRMED("confirmed"),
    DEATHS("deaths");

    private final String value;
    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

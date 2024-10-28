package com.example.quokka_event.controllers.dbutil;

public abstract class DbResponse {
    // Must add a result of some sort here as well in the children class
    private Exception exception;

    public DbResponse() {
        this.exception = null;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

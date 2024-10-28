package com.example.quokka_event.controllers.dbutil;

public interface DbCallback {
    void onSuccess(Object result);
    void onError(Exception exception);
}

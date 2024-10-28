package com.example.quokka_event.controllers.dbutil;

import java.util.Map;

// When we expect a single item in the response
public class SingleResponse extends DbResponse {
    private Map<String, Object> response;

    public SingleResponse() {
        this.response = null;
    }

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(Map<String, Object> response) {
        this.response = response;
    }
}

package com.example.quokka_event.controllers.dbutil;

import java.util.List;
import java.util.Map;

public class MultipleResponse {
    private List<Map<String, Object>> response;

    public MultipleResponse() {
        this.response = null;
    }

    public List<Map<String, Object>> getResponse() {
        return response;
    }

    public void setResponse(List<Map<String, Object>> response) {
        this.response = response;
    }
}

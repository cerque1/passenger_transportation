package com.example.demo.model.entity;

public class RouteHash {
    private String from;
    private String to;
    private String transport_type;

    public RouteHash(String from, String to, String transport_type){
        this.from = from;
        this.to = to;
        this.transport_type = transport_type;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTransport_type() {
        return transport_type;
    }
}

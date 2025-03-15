package com.example.demo.model.entity;

public class RouteInfo {
    String from;
    String to;
    Integer route_id;
    String transport_type;

    public RouteInfo(String from, String to, Integer route_id, String transport_type){
        this.from = from;
        this.to = to;
        this.route_id = route_id;
        this.transport_type = transport_type;
    }

    public String getFrom() {
        return from;
    }

    public Integer getRoute_id() {
        return route_id;
    }

    public String getTo() {
        return to;
    }

    public String getTransport_type() {
        return transport_type;
    }
}

package com.example.demo.model.entity;

import javax.swing.*;

public class Timetable {
    private Integer id;
    private Integer route_id;
    private Integer transport_type;
    private String departure_time;
    private String arrival_time;

    public Timetable(Integer id, Integer route_id, Integer transport_type, String departure_time, String arrival_time){
        this.id = id;
        this.route_id = route_id;
        this.transport_type = transport_type;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
    }

    public Integer getId() {
        return id;
    }

    public Integer getRoute_id() {
        return route_id;
    }

    public Integer getTransport_type() {
        return transport_type;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public String getArrival_time() {
        return arrival_time;
    }
}

package com.example.demo.model.entity;

public class Route {
    Integer id;
    Integer station_from;
    Integer station_to;
    String transport_type;

    public Route(Integer id,Integer station_from, Integer station_to, String transport_type){
        this.id = id;
        this.station_from = station_from;
        this.station_to = station_to;
        this.transport_type = transport_type;
    }

    public Integer getId() {
        return id;
    }

    public Integer getStation_from() {
        return station_from;
    }

    public Integer getStation_to() {
        return station_to;
    }

    public String getTransport_type() {
        return transport_type;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStation_from(Integer station_from) {
        this.station_from = station_from;
    }

    public void setStation_to(Integer station_to) {
        this.station_to = station_to;
    }

    public void setTransport_type(String transport_type) {
        this.transport_type = transport_type;
    }
}

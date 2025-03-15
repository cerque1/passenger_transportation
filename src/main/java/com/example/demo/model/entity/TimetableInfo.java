package com.example.demo.model.entity;

import java.util.List;

public class TimetableInfo {
    private String from;
    private String to;
    private String transport_type;
    private String departure_time;
    private String arrival_time;
    private Integer capacity;
    private List<Integer> timetables_sequence;

    public TimetableInfo(String from, String to, String transport_type, String departure_time, String arrival_time, Integer capacity, List<Integer> timetables_sequence){
        this.from = from;
        this.to = to;
        this.transport_type = transport_type;
        this.departure_time = departure_time;
        this.arrival_time = arrival_time;
        this.capacity = capacity;
        this.timetables_sequence = timetables_sequence;
    }

    public List<Integer> getTransport_sequence() {
        return timetables_sequence;
    }

    public Integer getCapacity() {
        return capacity;
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

    public String getDeparture_time() {
        return departure_time;
    }

    public String getArrival_time() {
        return arrival_time;
    }
}

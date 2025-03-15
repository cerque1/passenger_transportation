package com.example.demo.database;


import com.example.demo.model.entity.Route;
import com.example.demo.repository.*;

import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBase {

    private String url;
    private StationRepository stationRepository;
    private RouteRepository routeRepository;
    private TransportRepository transportRepository;
    private TimetableRepository timetableRepository;
    private UserRepository userRepository;
    private ReservationRepository reservationRepository;

    public DataBase(){
        url = "jdbc:sqlite:passenger_transportation_db.db";
        stationRepository = new StationRepository(url);
        routeRepository = new RouteRepository(url);
        transportRepository = new TransportRepository(url);
        timetableRepository = new TimetableRepository(url);
        userRepository = new UserRepository(url);
        reservationRepository = new ReservationRepository(url);
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public StationRepository getStationRepository(){
        return stationRepository;
    }

    public RouteRepository getRouteRepository() {
        return routeRepository;
    }

    public TransportRepository getTransportRepository() {
        return transportRepository;
    }

    public TimetableRepository getTimetableRepository() {
        return timetableRepository;
    }
}

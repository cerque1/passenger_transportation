package com.example.demo.service;

import com.example.demo.database.DataBase;
import com.example.demo.model.entity.*;
import com.example.demo.repository.StationRepository;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Time;
import java.util.*;

public class UserService {
    private final DataBase db_ = new DataBase();
    private final HashMap<RouteHash, List<List<Route>>> hash_routes = new HashMap<>();
    private final HashMap<Integer, Integer> token_to_user_id = new HashMap<>();
    private Integer last_user_id = -1;

    public Integer Registration(String user_name, String password){
        if(db_.getUserRepository().Insert(user_name, password)){
            Integer token = generateToken();
            token_to_user_id.put(token, ++last_user_id);
            return token;
        }

        return -1;
    }

    public Integer Entry(String user_name, String password){
        if(db_.getUserRepository().IsValidUser(user_name, password)){
            Integer token = generateToken();
            token_to_user_id.put(token, ++last_user_id);
            return token;
        }

        return -1;
    }

    private Integer generateToken() {
        Random random = new Random();
        return 10000000 + random.nextInt(90000000);
    }

    private Boolean IsAuthorized(Integer token){
        return token_to_user_id.containsKey(token);
    }

    public Boolean Reserve(List<Integer> timetables, Integer token){
        if(!CanReserve(timetables, token)){
            return false;
        }

        Integer user_id = token_to_user_id.get(token);
        for(Integer timetable_id : timetables){
            db_.getReservationRepository().Insert(user_id, timetable_id);
        }

        return true;
    }

    public Boolean IsReserve(List<Integer> timetables, Integer token){
        List<Integer> user_reserve = db_.getReservationRepository().GetReservesByUser(token_to_user_id.get(token));

        for(Integer timetables_id : timetables){
            if(!user_reserve.contains(timetables_id)){
                return false;
            }
        }

        return true;
    }

    private Boolean CanReserve(List<Integer> timetables, Integer token){
        if(!IsAuthorized(token)){
            return false;
        }

        List<Integer> user_reserve = db_.getReservationRepository().GetReservesByUser(token_to_user_id.get(token));

        for(Integer timetable_id : timetables){
            Timetable timetable = db_.getTimetableRepository().GetTimetableById(timetable_id);
            if(timetable == null
                || user_reserve.contains(timetable_id)
                || db_.getReservationRepository().GetReservationCount(timetable_id) >= db_.getTransportRepository().getCapacity(timetable.getTransport_type())){
                return false;
            }
        }

        return true;
    }

    public Boolean CancelReserve(List<Integer> timetables, Integer token){
        if(!IsAuthorized(token)){
            return false;
        }

        List<Integer> user_reserve = db_.getReservationRepository().GetReservesByUser(token_to_user_id.get(token));
        for(Integer timetable_id : timetables){
            if(!user_reserve.contains(timetable_id)){
                return false;
            }
        }

        Integer user_id = token_to_user_id.get(token);
        for(Integer timetable_id : timetables){
            db_.getReservationRepository().DeleteOne(user_id, timetable_id);
        }

        return true;
    }

    public List<List<Route>> GetRoutes(String from, String to, String transport_type){
        if(hash_routes.containsKey(new RouteHash(from, to, transport_type))){
            return hash_routes.get(new RouteHash(from, to, transport_type));
        }

        List<List<Route>> all_routes = new ArrayList<>();
        StationRepository stationRepository = db_.getStationRepository();

        try {
            Integer from_int = stationRepository.GetIdByAddress(from);
            Integer to_int = stationRepository.GetIdByAddress(to);
            GetRoute(from_int, to_int, transport_type, new ArrayList<>(), new ArrayList<>(), all_routes);
        }catch (Exception e){
            System.out.println("error: " + e.toString());
        }

        hash_routes.put(new RouteHash(from, to, transport_type), all_routes);
        return all_routes;
    }

    private void GetRoute(Integer from, Integer to, String transport_type, List<Integer> visited_ids, List<Route> current_route, List<List<Route>> result){
        if(Objects.equals(from, to)){
            result.add(new ArrayList<>(current_route));
            return;
        }

        List<Route> routes = db_.getRouteRepository().GetRoutes(from, transport_type);

        for(Route next_route : routes){
            if(visited_ids.contains(next_route.getStation_to())){
                continue;
            }

            current_route.add(next_route);
            visited_ids.add(next_route.getStation_from());
            GetRoute(next_route.getStation_to(), to, transport_type, visited_ids, current_route, result);
            visited_ids.remove(next_route.getStation_from());
            current_route.remove(next_route);
        }
    }

    public List<TimetableInfo> GetTimetables(String from, String to, String transport_type, String departure_time) throws Exception {
        List<List<Route>> all_routes_info = GetRoutes(from, to, transport_type);
        List<TimetableInfo> timetables_info = new ArrayList<>();

        for(List<Route> routes : all_routes_info){
            List<TimetableInfo> timetableInfos = new ArrayList<>();
            String new_departure_time = departure_time;
            Integer min_capacity = Integer.MAX_VALUE;
            List<Integer> timetables_on_route = new ArrayList<>();
            for(Route route : routes){
                List<Timetable> timetable = db_.getTimetableRepository().GetTimetableByRouteIdAfterTime(route.getId(), new_departure_time, true);
                if(timetable == null){
                    timetableInfos.clear();
                    break;
                }
                min_capacity = Math.min(min_capacity, db_.getTransportRepository().getCapacity(timetable.get(0).getTransport_type())
                        - db_.getReservationRepository().GetReservationCount(timetable.get(0).getId()));
                new_departure_time = timetable.get(0).getArrival_time();
                timetableInfos.add(new TimetableInfo(db_.getStationRepository().GetById(route.getStation_from()).getName()
                                                   , db_.getStationRepository().GetById(route.getStation_to()).getName()
                                                   , route.getTransport_type(), timetable.get(0).getDeparture_time(), timetable.get(0).getArrival_time(), timetable.get(0).getTransport_type(), null));
                timetables_on_route.add(timetable.get(0).getId());
            }
            if(!timetableInfos.isEmpty()){
                timetables_info.add(new TimetableInfo(timetableInfos.get(0).getFrom(), timetableInfos.get(timetableInfos.size() - 1).getTo()
                                                    , transport_type, timetableInfos.get(0).getDeparture_time(), timetableInfos.get(timetableInfos.size() - 1).getArrival_time(), min_capacity, timetables_on_route));
            }
        }

        return timetables_info;
    }

    public List<TimetableInfo> GetAllTimetables(String from, String to, String transport_type, String departure_time) throws Exception {
        List<List<Route>> all_routes_info = GetRoutes(from, to, transport_type);
        List<TimetableInfo> timetables_info = new ArrayList<>();

        for(List<Route> routes : all_routes_info){
            List<TimetableInfo> timetableInfos = new ArrayList<>();
            List<Timetable> timetables_for_first_route = db_.getTimetableRepository().GetTimetableByRouteIdAfterTime(routes.get(0).getId(), departure_time, false);
            List<Integer> timetables_on_route = new ArrayList<>();
            if(timetables_for_first_route == null){
                continue;
            }
            for(Timetable timetable_first_route : timetables_for_first_route){
                String new_departure_time = timetable_first_route.getDeparture_time();
                timetableInfos.add(new TimetableInfo(db_.getStationRepository().GetById(routes.get(0).getStation_from()).getName()
                            , db_.getStationRepository().GetById(routes.get(0).getStation_to()).getName()
                            , routes.get(0).getTransport_type(), timetable_first_route.getDeparture_time(), timetable_first_route.getArrival_time(), timetable_first_route.getTransport_type(), null));
                timetables_on_route.add(timetable_first_route.getId());

                Integer min_capacity = db_.getTransportRepository().getCapacity(timetable_first_route.getTransport_type())
                        - db_.getReservationRepository().GetReservationCount(timetable_first_route.getId());
                for(Route route : routes){
                    if(route == routes.get(0)){
                        continue;
                    }

                    List<Timetable> timetable = db_.getTimetableRepository().GetTimetableByRouteIdAfterTime(route.getId(), new_departure_time, true);
                    if(timetable == null){
                        timetableInfos.clear();
                        break;
                    }

                    new_departure_time = timetable.get(0).getArrival_time();
                    timetableInfos.add(new TimetableInfo(db_.getStationRepository().GetById(route.getStation_from()).getName()
                            , db_.getStationRepository().GetById(route.getStation_to()).getName()
                            , route.getTransport_type(), timetable.get(0).getDeparture_time(), timetable.get(0).getArrival_time(), timetable.get(0).getTransport_type(), null));
                    min_capacity = Math.min(min_capacity, db_.getTransportRepository().getCapacity(timetable.get(0).getTransport_type())
                        - db_.getReservationRepository().GetReservationCount(timetable.get(0).getId()));
                    timetables_on_route.add(timetable.get(0).getId());
                }

                if(!timetableInfos.isEmpty()){
                    timetables_info.add(new TimetableInfo(timetableInfos.get(0).getFrom(), timetableInfos.get(timetableInfos.size() - 1).getTo()
                            , transport_type, timetableInfos.get(0).getDeparture_time(), timetableInfos.get(timetableInfos.size() - 1).getArrival_time(), min_capacity, timetables_on_route));
                    timetableInfos.clear();
                }
            }
        }

        return timetables_info;
    }

    public Integer GetMinCapacity(List<Integer> timetablesIds) {
        Integer min_capacity = Integer.MAX_VALUE;
        for(Integer id : timetablesIds){
            min_capacity = Math.min(min_capacity, db_.getTransportRepository().getCapacity(db_.getTimetableRepository().GetTimetableById(id).getTransport_type()) -
                db_.getReservationRepository().GetReservationCount(id));
        }

        return min_capacity;
    }
}

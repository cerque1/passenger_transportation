package com.example.demo.controller;

import com.example.demo.database.DataBase;
import com.example.demo.model.entity.Route;
import com.example.demo.model.entity.RouteInfo;
import com.example.demo.model.entity.Timetable;
import com.example.demo.model.entity.TimetableInfo;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.json.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserApiController {
    private UserService userService;

    UserApiController(){
        userService = new UserService();
    }

    @PostMapping(value = "/get_routes")
    public ResponseEntity<List<List<Route>>> GetInfo(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        String from = obj.getString("from");
        String to = obj.getString("to");
        String transport_type = obj.getString("transport_type");

        List<List<Route>> routes = userService.GetRoutes(from, to, transport_type);
        return new ResponseEntity<>(routes, HttpStatus.OK);
    }

    @PostMapping(value = "/get_timetable")
    public ResponseEntity<List<TimetableInfo>> GetTimeTable(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        String from = obj.getString("from");
        String to = obj.getString("to");
        String departure_time = obj.getString("departure_time");
        String transport_type = obj.getString("transport_type");
        Boolean is_limit = obj.getBoolean("is_limit");

        List<TimetableInfo> timetables = new ArrayList<>();
        try {
            if(is_limit){
                timetables = userService.GetTimetables(from, to, transport_type, departure_time);
            }
            else{
                timetables = userService.GetAllTimetables(from, to, transport_type, departure_time);
            }
        }catch (Exception e){
            System.out.println("error timetable get: " + e.toString());
        }
        return new ResponseEntity<>(timetables, HttpStatus.OK);
    }

    @PostMapping(value = "/registration")
    public ResponseEntity<Integer> Registration(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        String user_name = obj.getString("user_name");
        String password = obj.getString("password");

        Integer res = userService.Registration(user_name, password);
        if(res == -1){
            return new ResponseEntity<>(-1, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(value = "/entry")
    public ResponseEntity<Integer> Entry(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        String user_name = obj.getString("user_name");
        String password = obj.getString("password");

        Integer res = userService.Entry(user_name, password);
        if(res == -1){
            return new ResponseEntity<>(-1, HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping(value = "/reserve")
    public ResponseEntity<Boolean> Reserve(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        Integer token = obj.getInt("token");
        List<Integer> timetables_ids = new ArrayList<>();
        JSONArray array = obj.getJSONArray("timetables_ids");
        for(int i = 0; i < array.length(); ++i){
            timetables_ids.add(array.getInt(i));
        }

        if(!userService.Reserve(timetables_ids, token)){
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(value = "/cancel")
    public ResponseEntity<Boolean> Cancel(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        Integer token = obj.getInt("token");
        List<Integer> timetables_ids = new ArrayList<>();
        JSONArray array = obj.getJSONArray("timetables_ids");
        for(int i = 0; i < array.length(); ++i){
            timetables_ids.add(array.getInt(i));
        }

        if(!userService.CancelReserve(timetables_ids, token)){
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(value = "/is_reserve")
    public ResponseEntity<Boolean> IsReserve(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        Integer token = obj.getInt("token");
        List<Integer> timetables_ids = new ArrayList<>();
        JSONArray array = obj.getJSONArray("timetables_ids");
        for(int i = 0; i < array.length(); ++i){
            timetables_ids.add(array.getInt(i));
        }

        if(!userService.IsReserve(timetables_ids, token)){
            return new ResponseEntity<>(false, HttpStatus.OK);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(value = "/get_min_capacity")
    public ResponseEntity<Integer> GetMinCapacity(@RequestBody String json){
        JSONObject obj = new JSONObject(json);
        List<Integer> timetables_ids = new ArrayList<>();
        JSONArray array = obj.getJSONArray("timetables_ids");
        for(int i = 0; i < array.length(); ++i){
            timetables_ids.add(array.getInt(i));
        }

        return new ResponseEntity<>(userService.GetMinCapacity(timetables_ids), HttpStatus.OK);
    }
}

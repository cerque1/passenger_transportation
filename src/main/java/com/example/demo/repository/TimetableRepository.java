package com.example.demo.repository;

import com.example.demo.model.entity.Timetable;
import com.example.demo.model.entity.TimetableInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableRepository {

    private final String url;

    public TimetableRepository(String url){
        this.url = url;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                Statement statement = conn.createStatement();

                String createTimetableRequest = "CREATE TABLE IF NOT EXISTS timetable(" +
                        "    id SERIAL PRIMARY KEY," +
                        "    route INT NOT NULL REFERENCES routes(id)," +
                        "    transport INT NOT NULL REFERENCES transport(id)," +
                        "    departure_time INT NOT NULL," +
                        "    arrival_time INT NOT NULL" +
                        ");";

                statement.execute(createTimetableRequest);
                System.out.println("Timetable create - OK");
                statement.close();

                Delete(conn);
                Insert(conn);

                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error create timetable: " + e.toString());
        }
    }

    private void Insert(Connection conn){
        try{
            String insertTimetable = "INSERT INTO timetable(id, route, transport, departure_time, arrival_time)" +
                    "VALUES (1, 8, 5, strftime('%s', '2025-03-09 12:00:00'), strftime('%s', '2025-03-09 16:30:00'))," +
                    "(2, 7, 3, strftime('%s', '2025-03-09 17:00:00'), strftime('%s', '2025-03-09 20:00:00'))," +
                    "(3, 5, 5, strftime('%s', '2025-03-09 18:00:00'), strftime('%s', '2025-03-09 23:30:00'))," +
                    "(4, 15, 5, strftime('%s', '2025-03-10 00:00:00'), strftime('%s', '2025-03-10 06:30:00'))," +
                    "(5, 8, 5, strftime('%s', '2025-03-11 12:00:00'), strftime('%s', '2025-03-11 16:00:00'))," +
                    "(6, 8, 5, strftime('%s', '2025-03-09 11:00:00'), strftime('%s', '2025-03-09 15:30:00'));";
            PreparedStatement statement_timetable = conn.prepareStatement(insertTimetable);
            statement_timetable.executeUpdate();
            System.out.println("insert timetables - OK");
            statement_timetable.close();
        } catch (SQLException e){
            System.out.println("error transport insert: " + e.toString());
        }
    }

    private void Delete(Connection conn){
        try{
            String delete_time = "DELETE FROM timetable;";
            PreparedStatement statement_time = conn.prepareStatement(delete_time);
            statement_time.executeUpdate();
            statement_time.close();
        }catch (SQLException e){
            System.out.println("error timetable delete: " + e.toString());
        }
    }

    public List<Timetable> GetTimetableByRouteIdAfterTime(Integer route_id, String departure_time, Boolean is_limit){
        List<Timetable> timetables = new ArrayList<>();

        try{
            Connection conn = DriverManager.getConnection(url);
            if(conn != null){
                String selectRequest = getSelectRequest(is_limit);
                PreparedStatement statement = conn.prepareStatement(selectRequest);
                statement.setInt(1, route_id);
                statement.setString(2, departure_time);
                if(!is_limit){
                    statement.setString(3, departure_time);
                }
                ResultSet res = statement.executeQuery();

                if(res.isClosed()){
                    statement.close();
                    conn.close();
                    return null;
                }

                while(res.next()) {
                     timetables.add(new Timetable(res.getInt("id")
                            , res.getInt("route")
                            , res.getInt("transport")
                            , res.getString("departure_time")
                            , res.getString("arrival_time")));
                }

                statement.close();
                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error timetable get: " + e.toString());
        }

        return timetables;
    }

    private String getSelectRequest(Boolean is_limit) {
        String selectRequest;
        if(is_limit){
            selectRequest = "SELECT id, route, transport, " +
                    "datetime(departure_time, 'unixepoch') AS departure_time, " +
                    "datetime(arrival_time, 'unixepoch') AS arrival_time " +
                    "FROM timetable " +
                    "WHERE route = ? AND departure_time > strftime('%s', ?) " +
                    "ORDER BY departure_time " +
                    "LIMIT 1;";
        }
        else{
            selectRequest = "SELECT id, route, transport, " +
                    "datetime(departure_time, 'unixepoch') AS departure_time, " +
                    "datetime(arrival_time, 'unixepoch') AS arrival_time " +
                    "FROM timetable " +
                    "WHERE route = ? AND departure_time > strftime('%s', ?) " +
                    "AND departure_time < strftime('%s', ?, '+1 day') " +
                    "ORDER BY departure_time;";
        }
        return selectRequest;
    }

    public Timetable GetTimetableById(Integer id){
        Timetable timetable = null;

        try{
            Connection conn = DriverManager.getConnection(url);
            if(conn != null){
                String selectTimetable = "SELECT * FROM timetable " +
                        "WHERE id = ?;";
                PreparedStatement statement = conn.prepareStatement(selectTimetable);
                statement.setInt(1, id);
                ResultSet res = statement.executeQuery();

                if(res.isClosed()){
                    statement.close();
                    conn.close();
                    return null;
                }

                timetable = new Timetable(res.getInt("id")
                            , res.getInt("route")
                            , res.getInt("transport")
                            , res.getString("departure_time")
                            , res.getString("arrival_time"));

                statement.close();
                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error timetable get: " + e.toString());
        }

        return timetable;
    }

}

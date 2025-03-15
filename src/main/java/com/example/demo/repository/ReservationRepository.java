package com.example.demo.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {
    private final String url;

    public ReservationRepository(String url){
        this.url = url;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                Statement statement = conn.createStatement();

                String createTransportRequest = "CREATE TABLE IF NOT EXISTS reservation(" +
                        "    user_id INTEGER NOT NULL," +
                        "    timetable_id INTEGER NOT NULL," +
                        "    PRIMARY KEY(user_id, timetable_id)" +
                        ");";

                statement.execute(createTransportRequest);
                System.out.println("Transport table create - OK");
                statement.close();

                DeleteALL(conn);

                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error create users: " + e.toString());
        }
    }

    private void DeleteALL(Connection conn){
        try{
            String delete_reservation = "DELETE FROM reservation;";
            PreparedStatement statement_reservation = conn.prepareStatement(delete_reservation);
            statement_reservation.executeUpdate();
            statement_reservation.close();
        }catch (SQLException e){
            System.out.println("error reservation delete: " + e.toString());
        }
    }

    public void DeleteOne(Integer user_id, Integer timetable_id){
        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null) {
                String delete_reservation = "DELETE FROM reservation " +
                        "WHERE user_id = ? AND timetable_id = ?;";
                PreparedStatement statement_reservation = conn.prepareStatement(delete_reservation);
                statement_reservation.setInt(1, user_id);
                statement_reservation.setInt(2, timetable_id);
                statement_reservation.executeUpdate();
                statement_reservation.close();
            }
        }catch (SQLException e){
            System.out.println("error reservation delete: " + e.toString());
        }
    }

    public Boolean Insert(Integer user_id, Integer timetable_id){
        try {
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String insert_reservation = "INSERT INTO reservation " +
                        "VALUES (?, ?);";

                PreparedStatement statement_reservation = conn.prepareStatement(insert_reservation);
                statement_reservation.setInt(1, user_id);
                statement_reservation.setInt(2, timetable_id);
                statement_reservation.executeUpdate();
                System.out.println("insert reservation - OK");
                statement_reservation.close();
            }

        } catch (SQLException e){
            System.out.println("user insert error: " + e.toString());
            return false;
        }

        return true;
    }

    public Integer GetReservationCount(Integer timetable_id){
        Integer count = 0;

        try {
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String insert_user = "SELECT count(*) AS count FROM reservation " +
                        "WHERE timetable_id = ?;";

                PreparedStatement statement_user = conn.prepareStatement(insert_user);
                statement_user.setInt(1, timetable_id);
                ResultSet res = statement_user.executeQuery();
                count = res.getInt("count");
                statement_user.close();
                conn.close();
            }

        } catch (SQLException e){
            System.out.println("reservation get error: " + e.toString());
        }

        return count;
    }

    public List<Integer> GetReservesByUser(Integer user_id){
        List<Integer> timetables_ids = new ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String insert_user = "SELECT timetable_id FROM reservation " +
                        "WHERE user_id = ?;";

                PreparedStatement statement_user = conn.prepareStatement(insert_user);
                statement_user.setInt(1, user_id);
                ResultSet res = statement_user.executeQuery();

                while(res.next()){
                    timetables_ids.add(res.getInt("timetable_id"));
                }

                statement_user.close();
                conn.close();
            }

        } catch (SQLException e){
            System.out.println("reservation get error: " + e.toString());
        }

        return timetables_ids;
    }
}

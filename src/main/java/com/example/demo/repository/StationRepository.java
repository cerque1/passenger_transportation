package com.example.demo.repository;

import com.example.demo.model.entity.Station;

import java.sql.*;

public class StationRepository {
    private final String url;

    public StationRepository(String url) {
        this.url = url;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                Statement statement = conn.createStatement();

                String createStationRequest = "CREATE TABLE IF NOT EXISTS stations(" +
                        "id SERIAL PRIMARY KEY," +
                        "name TEXT NOT NULL," +
                        "address TEXT NOT NULL" +
                        ");";

                statement.execute(createStationRequest);
                System.out.println("Stations table create - OK");
                statement.close();

                Delete(conn);
                Insert(conn);

                conn.close();
            }

        }catch (SQLException e){
            System.out.println("error create station: " + e.toString());
        }
    }

    private void Insert(Connection conn){
        try {
            String insertStations = "INSERT INTO stations " +
                    "VALUES (1, 'Москва', 'г. Москва')," +
                    "(2, 'Питер', 'г. Питер')," +
                    "(3, 'Новгород', 'г. Новгород')," +
                    "(4, 'Краснодар', 'г. Краснодар')," +
                    "(5, 'Тюмень', 'г. Тюмень');";

            PreparedStatement statement_stations = conn.prepareStatement(insertStations);
            statement_stations.executeUpdate();
            System.out.println("insert stations - OK");
            statement_stations.close();

        }catch (SQLException e){
            System.out.printf("error insert station");
        }
    }

    public Station GetById(int id) throws Exception {
        Station station = null;
        try {
            Connection conn = DriverManager.getConnection(url);
            if(conn != null) {
                String selectStation = "SELECT * FROM stations " +
                        "WHERE id = ?;";
                PreparedStatement statement = conn.prepareStatement(selectStation);
                statement.setInt(1, id);
                ResultSet res = statement.executeQuery();
                station = new Station(res.getInt("id"), res.getString("name"), res.getString("address"));
                statement.close();
                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error get");
        }

        if(station == null){
            throw new Exception("not found");
        }

        return station;
    }

    public Integer GetIdByAddress(String address) throws Exception {
        Integer id = null;
        try {
            Connection conn = DriverManager.getConnection(url);
            if(conn != null) {
                String selectStation = "SELECT id FROM stations " +
                        "WHERE address = ?;";
                PreparedStatement statement = conn.prepareStatement(selectStation);
                statement.setString(1, address);
                ResultSet res = statement.executeQuery();
                id = res.getInt("id");
                statement.close();
                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error get");
        }

        if(id == null){
            throw new Exception("not found");
        }

        return id;
    }

    private void Delete(Connection conn){
        try{
            String delete_station = "DELETE FROM stations;";
            PreparedStatement statement_stations = conn.prepareStatement(delete_station);
            statement_stations.executeUpdate();
            statement_stations.close();
        }catch (SQLException e){
            System.out.println("error delete station: " + e.toString());
        }
    }

}

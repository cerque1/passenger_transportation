package com.example.demo.repository;

import java.sql.*;

public class TransportRepository {

    private final String url;

    public TransportRepository(String url){
        this.url = url;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                Statement statement = conn.createStatement();

                String createTransportRequest = "CREATE TABLE IF NOT EXISTS transport(" +
                        "    id SERIAL PRIMARY KEY," +
                        "    model TEXT NOT NULL," +
                        "    capacity INT NOT NULL," +
                        "    type TEXT NOT NULL" +
                        ");";

                statement.execute(createTransportRequest);
                System.out.println("Transport table create - OK");
                statement.close();

                Delete(conn);
                Insert(conn);

                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error create transport: " + e.toString());
        }
    }

    private void Insert(Connection conn){
        try{
            String insertTransport = "INSERT INTO transport(id, model, capacity, type)" +
                    "VALUES (1, 'A-123', '100', 'BUS')," +
                    "(2, 'B-231', '150', 'BUS')," +
                    "(3, 'Boing-3', '500', 'AIR')," +
                    "(4, 'Ласточка', '250', 'TRAIN')," +
                    "(5, 'Сапсан', '200', 'TRAIN')," +
                    "(6, 'Airline-342', '750', 'AIR');";

            PreparedStatement statement_transport = conn.prepareStatement(insertTransport);
            statement_transport.executeUpdate();
            System.out.println("insert transports - OK");
            statement_transport.close();
        } catch (SQLException e){
            System.out.println("error transport insert: " + e.toString());
        }
    }

    private void Delete(Connection conn){
        try{
            String delete_transport = "DELETE FROM transport;";
            PreparedStatement statement_transport = conn.prepareStatement(delete_transport);
            statement_transport.executeUpdate();
            statement_transport.close();
        }catch (SQLException e){
            System.out.println("error transport delete: " + e.toString());
        }
    }

    public Integer getCapacity(Integer transport_id){
        Integer capacity = 0;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String selectRoute = "SELECT capacity FROM transport " +
                        "WHERE id = ?;";

                PreparedStatement statement = conn.prepareStatement(selectRoute);
                statement.setInt(1, transport_id);
                ResultSet res = statement.executeQuery();
                capacity = res.getInt("capacity");
                statement.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error get: " + e.toString());
        }

        return capacity;
    }
}

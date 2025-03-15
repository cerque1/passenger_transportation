package com.example.demo.repository;

import com.example.demo.model.entity.Route;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouteRepository {

    private final String url;

    public RouteRepository(String url){
        this.url = url;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                Statement statement = conn.createStatement();

                String createRoutesRequest = "CREATE TABLE IF NOT EXISTS routes(" +
                        "id SERIAL PRIMARY KEY," +
                        "station_from INT NOT NULL REFERENCES stations(id)," +
                        "station_to INT NOT NULL REFERENCES stations(id)," +
                        "transport_type TEXT NOT NULL" +
                        ");";

                statement.execute(createRoutesRequest);
                System.out.println("Routes table create - OK");
                statement.close();

                Delete(conn);
                Insert(conn);

                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error create route: " + e.toString());
        }
    }

    private void Insert(Connection conn){
        try{
            String insertRoutes = "INSERT INTO routes(id, station_from, station_to, transport_type) " +
                    "VALUES (1, 1, 2, 'TRAIN')," +
                    "(2, 1, 2, 'BUS')," +
                    "(3, 1, 2, 'AIR')," +
                    "(4, 1, 4, 'TRAIN')," +
                    "(5, 1, 3, 'TRAIN')," +
                    "(6, 1, 3, 'AIR')," +
                    "(7, 1, 5, 'AIR')," +
                    "(8, 2, 1, 'TRAIN')," +
                    "(9, 2, 1, 'AIR')," +
                    "(10, 2, 1, 'BUS')," +
                    "(11, 4, 1, 'TRAIN')," +
                    "(12, 3, 1, 'TRAIN')," +
                    "(13, 3, 1, 'AIR')," +
                    "(14, 5, 1, 'AIR')," +
                    "(15, 3, 5, 'TRAIN')," +
                    "(16, 1, 5, 'TRAIN')," +
                    "(17, 5, 4, 'TRAIN');";

            PreparedStatement statement_routes = conn.prepareStatement(insertRoutes);
            statement_routes.executeUpdate();
            System.out.println("insert routes - OK");
            statement_routes.close();
        }catch (SQLException e){
            System.out.println("error insert: " + e.toString());
        }
    }

    private void Delete(Connection conn){
        try{
            String delete_routes = "DELETE FROM routes;";
            PreparedStatement statement_routes = conn.prepareStatement(delete_routes);
            statement_routes.executeUpdate();
            statement_routes.close();
        }catch (SQLException e){
            System.out.println("error delete: " + e.toString());
        }
    }

    public List<Route> GetRoutes(Integer from, String transport_type){
        List<Route> routes = new ArrayList<>();

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String selectRoutes = "SELECT * FROM routes " +
                        "WHERE station_from = ?;";
                if(!Objects.equals(transport_type, "MIXED")){
                    selectRoutes = "SELECT * FROM routes " +
                            "WHERE station_from = ? AND transport_type = ?;";
                }

                PreparedStatement statement = conn.prepareStatement(selectRoutes);
                statement.setInt(1, from);
                if(!Objects.equals(transport_type, "MIXED")) {
                    statement.setString(2, transport_type);
                }
                ResultSet res = statement.executeQuery();

                while(res.next()){
                    routes.add(new Route(res.getInt("id")
                            , res.getInt("station_from")
                            , res.getInt("station_to")
                            , res.getString("transport_type")));
                }
                statement.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error get: " + e.toString());
        }

        return routes;
    }

    public Route GetRouteById(Integer id) {
        Route route = null;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String selectRoute = "SELECT * FROM routes " +
                        "WHERE id = ?;";

                PreparedStatement statement = conn.prepareStatement(selectRoute);
                statement.setInt(1, id);
                ResultSet res = statement.executeQuery();

                while(res.next()){
                    route = new Route(res.getInt("id")
                            , res.getInt("station_from")
                            , res.getInt("station_to")
                            , res.getString("transport_type"));
                }
                statement.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error get: " + e.toString());
        }

        return route;
    }

    public Integer GetRouteSize(Integer route_id){
        Integer size = 0;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String selectRoute = "SELECT count(*) AS size FROM routes " +
                        "WHERE parent_route_id = ?;";

                PreparedStatement statement = conn.prepareStatement(selectRoute);
                statement.setInt(1, route_id);
                ResultSet res = statement.executeQuery();
                size = res.getInt("size");
                statement.close();
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error get: " + e.toString());
        }

        return size;
    }
}

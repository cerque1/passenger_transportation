package com.example.demo.repository;

import java.sql.*;

public class UserRepository {
    private final String url;

    public UserRepository(String url){
        this.url = url;

        try{
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                Statement statement = conn.createStatement();

                String createTransportRequest = "CREATE TABLE IF NOT EXISTS users(" +
                        "    id INTEGER PRIMARY KEY," +
                        "    user_name TEXT UNIQUE NOT NULL," +
                        "    password TEXT NOT NULL" +
                        ");";

                statement.execute(createTransportRequest);
                System.out.println("Transport table create - OK");
                statement.close();

                Delete(conn);

                conn.close();
            }
        }catch (SQLException e){
            System.out.println("error create users: " + e.toString());
        }
    }

    private void Delete(Connection conn){
        try{
            String delete_user = "DELETE FROM users;";
            PreparedStatement statement_user = conn.prepareStatement(delete_user);
            statement_user.executeUpdate();
            statement_user.close();
        }catch (SQLException e){
            System.out.println("error users delete: " + e.toString());
        }
    }

    public Boolean Insert(String user_name, String password){
        try {
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String insert_user = "INSERT INTO users(user_name, password) " +
                        "VALUES (?, ?);";

                PreparedStatement statement_user = conn.prepareStatement(insert_user);
                statement_user.setString(1, user_name);
                statement_user.setString(2, password);
                statement_user.executeUpdate();
                System.out.println("insert user - OK");
                statement_user.close();
                conn.close();
            }

        } catch (SQLException e){
            System.out.println("user insert error: " + e.toString());
            return false;
        }

        return true;
    }

    public Boolean IsValidUser(String user_name, String password){
        try {
            Connection conn = DriverManager.getConnection(url);

            if(conn != null){
                String insertUser = "SELECT count(*) AS user FROM users " +
                        "WHERE user_name = ? AND password = ?;";

                PreparedStatement statement_user = conn.prepareStatement(insertUser);
                statement_user.setString(1, user_name);
                statement_user.setString(2, password);
                ResultSet res = statement_user.executeQuery();
                int users_count = res.getInt("user");
                if(users_count == 1){
                    return true;
                }
                statement_user.close();
                return false;
            }

        } catch (SQLException e){
            System.out.println("user valid error: " + e.toString());
        }

        return false;
    }
}

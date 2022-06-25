package ru.geekbrains.velvetbox.global;

import java.sql.*;

public class Database {
    Connection connection = null;

    public Database() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:vbdb.db");
            connection.setAutoCommit(false);
            System.out.println("Connected to DB successfully");
        }
    }

    private void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public AuthorizationResult authorization(User user) {
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM USERS WHERE LOGIN='%s' and PASSWORD='%s'",
                    user.getLogin(),user.getPassword()));
            while (rs.next()) {
                User foundUser = new User(rs.getString("NAME"),rs.getString("LOGIN"),rs.getString("PASSWORD"));
                disconnect();
                return new AuthorizationResult(true,foundUser,1);
            }
            disconnect();
            return new AuthorizationResult(true,null,0);
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthorizationResult(false,null,-1);
        }
    }

    public RegisterResult register(User user) {
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(String.format("SELECT COUNT(1) as total FROM USERS WHERE LOGIN='%s'",
                    user.getLogin()));
            if (rs.getInt("total") > 0 ) {
                return new RegisterResult(true,"Невозможно зарегистрироваться, такой пользователь уже зарегистрирован!",0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegisterResult(false,e.getMessage(),-1);
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(String.format("INSERT INTO USERS (NAME,LOGIN,PASSWORD) VALUES ('%s','%s','%s')",
                    user.getName(), user.getLogin(), user.getPassword()));
            connection.commit();
            disconnect();
            return new RegisterResult(true,"Успешно",1);
        } catch (SQLException e) {
            e.printStackTrace();
            return new RegisterResult(false,e.getMessage(),-1);
        }
    }
}

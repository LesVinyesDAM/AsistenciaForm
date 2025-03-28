package org.vinyes.asistencia.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://172.27.148.146:3306/vin_fichaje";
    private static final String USER = "user0";
    private static final String PASSWORD = "vin1234+";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
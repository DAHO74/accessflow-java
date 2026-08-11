package com.accessflow.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL      = "jdbc:mysql://localhost:3306/accessflow_db";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "";

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}

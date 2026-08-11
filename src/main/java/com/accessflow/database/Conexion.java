package com.accessflow.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Conexion {

    private static final String URL      = "jdbc:mysql://localhost:3306/accessflow_db";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "";

    public static Connection obtener() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    // Ejecuta migraciones de columnas faltantes para compatibilidad entre equipos
    public static void migrar() {
        String[] migraciones = {
            "ALTER TABLE Tutor ADD COLUMN IF NOT EXISTS parentesco VARCHAR(50)"
        };
        try (Connection con = obtener(); Statement st = con.createStatement()) {
            for (String sql : migraciones) {
                try { st.execute(sql); } catch (SQLException ignored) {}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

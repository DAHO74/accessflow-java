package com.accessflow.dao;

import com.accessflow.database.Conexion;

import java.sql.*;

public class ConfiguracionDAO {

    public String obtener(String clave) {
        String sql = "SELECT valor FROM Configuracion WHERE clave = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, clave);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("valor");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean guardar(String clave, String valor) {
        // INSERT ... ON DUPLICATE KEY UPDATE — actualiza si ya existe
        String sql = "INSERT INTO Configuracion (clave, valor) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE valor = VALUES(valor)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, clave);
            ps.setString(2, valor);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

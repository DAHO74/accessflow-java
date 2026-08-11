package com.accessflow.dao;

import com.accessflow.database.Conexion;
import com.accessflow.model.Tutor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TutorDAO {

    public List<Tutor> listarTodos() {
        List<Tutor> lista = new ArrayList<>();
        String sql = "SELECT * FROM Tutor ORDER BY nombre";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Tutor buscarPorId(int id) {
        String sql = "SELECT * FROM Tutor WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertar(Tutor t) {
        return insertarConId(t) > 0;
    }

    public int insertarConId(Tutor t) {
        String sql = "INSERT INTO Tutor (nombre, email, telefono) VALUES (?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getTelefono());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                t.setId(id);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean actualizar(Tutor t) {
        String sql = "UPDATE Tutor SET nombre = ?, email = ?, telefono = ? WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getEmail());
            ps.setString(3, t.getTelefono());
            ps.setInt(4, t.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Tutor WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Tutor";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Tutor mapear(ResultSet rs) throws SQLException {
        Tutor t = new Tutor();
        t.setId(rs.getInt("id"));
        t.setNombre(rs.getString("nombre"));
        t.setEmail(rs.getString("email"));
        t.setTelefono(rs.getString("telefono"));
        return t;
    }
}

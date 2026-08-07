package com.accessflow.dao;

import com.accessflow.database.Conexion;
import com.accessflow.model.Mensaje;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensajeDAO {

    public boolean insertar(Mensaje m) {
        String sql = "INSERT INTO Mensaje (usuario_id, tutor_id, asunto, cuerpo, enviado_en) " +
                     "VALUES (?, ?, ?, ?, NOW())";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getUsuarioId());
            ps.setInt(2, m.getTutorId());
            ps.setString(3, m.getAsunto());
            ps.setString(4, m.getCuerpo());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Mensaje> listarTodos() {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT m.*, t.nombre AS tutor_nombre " +
                     "FROM Mensaje m LEFT JOIN Tutor t ON m.tutor_id = t.id " +
                     "ORDER BY m.enviado_en DESC";
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

    private Mensaje mapear(ResultSet rs) throws SQLException {
        Mensaje m = new Mensaje();
        m.setId(rs.getInt("id"));
        m.setUsuarioId(rs.getInt("usuario_id"));
        m.setTutorId(rs.getInt("tutor_id"));
        m.setTutorNombre(rs.getString("tutor_nombre"));
        m.setAsunto(rs.getString("asunto"));
        m.setCuerpo(rs.getString("cuerpo"));
        Timestamp ts = rs.getTimestamp("enviado_en");
        if (ts != null) m.setEnviadoEn(ts.toLocalDateTime());
        return m;
    }
}

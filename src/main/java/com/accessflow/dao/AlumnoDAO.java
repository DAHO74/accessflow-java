package com.accessflow.dao;

import com.accessflow.database.Conexion;
import com.accessflow.model.Alumno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlumnoDAO {

    private static final String SELECT_BASE =
        "SELECT a.*, g.nombre AS grupo_nombre, t.nombre AS tutor_nombre " +
        "FROM Alumno a " +
        "LEFT JOIN Grupo g ON a.grupo_id = g.id " +
        "LEFT JOIN Tutor t ON a.tutor_id = t.id ";

    public List<Alumno> listarTodos() {
        List<Alumno> lista = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY a.nombre";
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

    public Alumno buscarPorId(int id) {
        String sql = SELECT_BASE + "WHERE a.id = ?";
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

    public Alumno buscarPorRfid(String rfid) {
        String sql = SELECT_BASE + "WHERE a.rfid_uid = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rfid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Alumno buscarPorTutorId(int tutorId) {
        String sql = SELECT_BASE + "WHERE a.tutor_id = ? LIMIT 1";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tutorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Alumno> listarPorTutorId(int tutorId) {
        List<Alumno> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE a.tutor_id = ? ORDER BY a.nombre";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, tutorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void asignarAlumnos(int tutorId, java.util.List<Integer> alumnoIds) {
        try (Connection con = Conexion.obtener()) {
            // Quitar asignación previa de este tutor
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE Alumno SET tutor_id = NULL WHERE tutor_id = ?")) {
                ps.setInt(1, tutorId);
                ps.executeUpdate();
            }
            // Asignar los seleccionados
            if (!alumnoIds.isEmpty()) {
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE Alumno SET tutor_id = ? WHERE id = ?")) {
                    for (int alumnoId : alumnoIds) {
                        ps.setInt(1, tutorId);
                        ps.setInt(2, alumnoId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insertar(Alumno a) {
        String sql = "INSERT INTO Alumno (nombre, rfid_uid, grupo_id, tutor_id) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            if (a.getRfidUid() != null && !a.getRfidUid().isEmpty())
                ps.setString(2, a.getRfidUid());
            else
                ps.setNull(2, Types.VARCHAR);
            if (a.getGrupoId() > 0) ps.setInt(3, a.getGrupoId());
            else                    ps.setNull(3, Types.INTEGER);
            if (a.getTutorId() > 0) ps.setInt(4, a.getTutorId());
            else                    ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Alumno a) {
        String sql = "UPDATE Alumno SET nombre = ?, rfid_uid = ?, grupo_id = ?, tutor_id = ? WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            if (a.getRfidUid() != null && !a.getRfidUid().isEmpty())
                ps.setString(2, a.getRfidUid());
            else
                ps.setNull(2, Types.VARCHAR);
            if (a.getGrupoId() > 0) ps.setInt(3, a.getGrupoId());
            else                    ps.setNull(3, Types.INTEGER);
            if (a.getTutorId() > 0) ps.setInt(4, a.getTutorId());
            else                    ps.setNull(4, Types.INTEGER);
            ps.setInt(5, a.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM Alumno WHERE id = ?";
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
        String sql = "SELECT COUNT(*) FROM Alumno";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Alumno mapear(ResultSet rs) throws SQLException {
        Alumno a = new Alumno();
        a.setId(rs.getInt("id"));
        a.setNombre(rs.getString("nombre"));
        a.setRfidUid(rs.getString("rfid_uid"));
        a.setGrupoId(rs.getInt("grupo_id"));
        a.setTutorId(rs.getInt("tutor_id"));
        a.setGrupoNombre(rs.getString("grupo_nombre"));
        a.setTutorNombre(rs.getString("tutor_nombre"));
        return a;
    }
}

package com.accessflow.dao;

import com.accessflow.database.Conexion;
import com.accessflow.model.Asistencia;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {

    public List<Asistencia> listarHoy() {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT a.*, al.nombre AS alumno_nombre, g.nombre AS grupo_nombre, g.grado AS grupo_grado " +
                     "FROM Asistencia a " +
                     "LEFT JOIN Alumno al ON a.alumno_id = al.id " +
                     "LEFT JOIN Grupo g ON al.grupo_id = g.id " +
                     "WHERE DATE(a.fecha_entrada) = CURDATE() " +
                     "ORDER BY a.fecha_entrada DESC";
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

    // Busca si el alumno ya tiene una asistencia registrada hoy
    public Asistencia buscarAsistenciaHoy(int alumnoId) {
        String sql = "SELECT * FROM Asistencia WHERE alumno_id = ? AND DATE(fecha_entrada) = CURDATE()";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setAlumnoId(rs.getInt("alumno_id"));
                Timestamp tsEntrada = rs.getTimestamp("fecha_entrada");
                if (tsEntrada != null) a.setFechaEntrada(tsEntrada.toLocalDateTime());
                Timestamp tsSalida = rs.getTimestamp("fecha_salida");
                if (tsSalida != null) a.setFechaSalida(tsSalida.toLocalDateTime());
                return a;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Asistencia buscarSesionActiva(int alumnoId) {
        String sql = "SELECT * FROM Asistencia WHERE alumno_id = ? AND fecha_salida IS NULL " +
                     "ORDER BY fecha_entrada DESC LIMIT 1";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Asistencia a = new Asistencia();
                a.setId(rs.getInt("id"));
                a.setAlumnoId(rs.getInt("alumno_id"));
                Timestamp ts = rs.getTimestamp("fecha_entrada");
                if (ts != null) a.setFechaEntrada(ts.toLocalDateTime());
                return a;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean registrarEntrada(int alumnoId) {
        String sql = "INSERT INTO Asistencia (alumno_id, fecha_entrada) VALUES (?, NOW())";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarSalida(int asistenciaId) {
        String sql = "UPDATE Asistencia SET fecha_salida = NOW() WHERE id = ?";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, asistenciaId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Asistencia> buscarPorFiltros(LocalDate desde, LocalDate hasta,
                                              Integer grupoId, Integer alumnoId) {
        List<Asistencia> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT a.*, al.nombre AS alumno_nombre, g.nombre AS grupo_nombre, g.grado AS grupo_grado " +
            "FROM Asistencia a " +
            "LEFT JOIN Alumno al ON a.alumno_id = al.id " +
            "LEFT JOIN Grupo g ON al.grupo_id = g.id " +
            "WHERE a.fecha_entrada BETWEEN ? AND ?"
        );
        if (grupoId  != null) sql.append(" AND al.grupo_id = ?");
        if (alumnoId != null) sql.append(" AND a.alumno_id = ?");
        sql.append(" ORDER BY a.fecha_entrada DESC");

        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(desde.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(hasta.atTime(LocalTime.MAX)));

            int i = 3;
            if (grupoId  != null) { ps.setInt(i, grupoId);  i++; }
            if (alumnoId != null) { ps.setInt(i, alumnoId); }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public int contarPresentesHoy() {
        String sql = "SELECT COUNT(DISTINCT alumno_id) FROM Asistencia WHERE DATE(fecha_entrada) = CURDATE()";
        try (Connection con = Conexion.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Asistencia mapear(ResultSet rs) throws SQLException {
        Asistencia a = new Asistencia();
        a.setId(rs.getInt("id"));
        a.setAlumnoId(rs.getInt("alumno_id"));
        a.setAlumnoNombre(rs.getString("alumno_nombre"));
        a.setGrupoNombre(rs.getString("grupo_nombre"));
        a.setGrupoGrado(rs.getString("grupo_grado"));
        Timestamp tsEntrada = rs.getTimestamp("fecha_entrada");
        if (tsEntrada != null) a.setFechaEntrada(tsEntrada.toLocalDateTime());
        Timestamp tsSalida = rs.getTimestamp("fecha_salida");
        if (tsSalida != null) a.setFechaSalida(tsSalida.toLocalDateTime());
        return a;
    }
}

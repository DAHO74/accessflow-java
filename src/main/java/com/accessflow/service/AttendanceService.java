package com.accessflow.service;

import com.accessflow.model.Alumno;
import com.accessflow.model.Asistencia;
import com.accessflow.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AttendanceService {

    public static Alumno buscarPorRfid(String rfid) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Alumno WHERE rfidUid = :rfid", Alumno.class)
                    .setParameter("rfid", rfid).uniqueResult();
        }
    }

    public static Alumno buscarPorId(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Alumno.class, id);
        }
    }

    public static String registrarAsistencia(Alumno alumno) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            LocalDateTime inicioDelDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
            Asistencia existente = session.createQuery(
                "FROM Asistencia WHERE alumno.id = :id AND fechaEntrada >= :inicio", Asistencia.class)
                .setParameter("id", alumno.getId())
                .setParameter("inicio", inicioDelDia)
                .setMaxResults(1).uniqueResult();

            String resultado;
            if (existente == null) {
                Asistencia nueva = new Asistencia(alumno, LocalDateTime.now());
                session.persist(nueva);
                resultado = "ENTRADA";
            } else if (existente.getFechaSalida() == null) {
                existente.setFechaSalida(LocalDateTime.now());
                resultado = "SALIDA";
            } else {
                resultado = "YA_REGISTRADO";
            }

            tx.commit();
            return resultado;
        }
    }

    public static List<Asistencia> getAsistenciasHoy() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDateTime inicio = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
            return session.createQuery(
                "FROM Asistencia WHERE fechaEntrada >= :inicio ORDER BY fechaEntrada DESC", Asistencia.class)
                .setParameter("inicio", inicio).list();
        }
    }

    public static List<Asistencia> getHistorial(LocalDate desde, LocalDate hasta, Long grupoId, Long alumnoId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder(
                "FROM Asistencia a WHERE a.fechaEntrada BETWEEN :desde AND :hasta");
            if (grupoId != null) hql.append(" AND a.alumno.grupo.id = :grupoId");
            if (alumnoId != null) hql.append(" AND a.alumno.id = :alumnoId");
            hql.append(" ORDER BY a.fechaEntrada DESC");

            var query = session.createQuery(hql.toString(), Asistencia.class)
                .setParameter("desde", desde.atStartOfDay())
                .setParameter("hasta", hasta.atTime(LocalTime.MAX));
            if (grupoId != null) query.setParameter("grupoId", grupoId);
            if (alumnoId != null) query.setParameter("alumnoId", alumnoId);

            return query.list();
        }
    }

    public static long countPresentes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDateTime inicio = LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT);
            Long count = session.createQuery(
                "SELECT COUNT(DISTINCT a.alumno.id) FROM Asistencia a WHERE a.fechaEntrada >= :inicio", Long.class)
                .setParameter("inicio", inicio).uniqueResult();
            return count != null ? count : 0;
        }
    }

    public static long countTotalAlumnos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(a) FROM Alumno a", Long.class).uniqueResult();
            return count != null ? count : 0;
        }
    }

    public static long countTotalGrupos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(g) FROM Grupo g", Long.class).uniqueResult();
            return count != null ? count : 0;
        }
    }
}

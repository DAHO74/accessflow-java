package com.accessflow.service;

import com.accessflow.model.*;
import com.accessflow.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class CrudService {

    // ── ALUMNOS ──────────────────────────────────────────────────────────────

    public static List<Alumno> listarAlumnos() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Alumno ORDER BY nombre", Alumno.class).list();
        }
    }

    public static List<Alumno> listarAlumnosPorGrupo(Long grupoId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Alumno WHERE grupo.id = :g ORDER BY nombre", Alumno.class)
                    .setParameter("g", grupoId).list();
        }
    }

    public static void guardarAlumno(Alumno alumno) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.merge(alumno);
            tx.commit();
        }
    }

    public static void eliminarAlumno(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Alumno a = s.get(Alumno.class, id);
            if (a != null) s.remove(a);
            tx.commit();
        }
    }

    // ── GRUPOS ───────────────────────────────────────────────────────────────

    public static List<Grupo> listarGrupos() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Grupo ORDER BY grado, nombre", Grupo.class).list();
        }
    }

    public static void guardarGrupo(Grupo grupo) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.merge(grupo);
            tx.commit();
        }
    }

    public static void eliminarGrupo(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Grupo g = s.get(Grupo.class, id);
            if (g != null) s.remove(g);
            tx.commit();
        }
    }

    // ── TUTORES ──────────────────────────────────────────────────────────────

    public static List<Tutor> listarTutores() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Tutor ORDER BY nombre", Tutor.class).list();
        }
    }

    public static void guardarTutor(Tutor tutor) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.merge(tutor);
            tx.commit();
        }
    }

    public static void eliminarTutor(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Tutor t = s.get(Tutor.class, id);
            if (t != null) s.remove(t);
            tx.commit();
        }
    }

    public static void vincularTutor(Alumno alumno, Tutor tutor, String parentesco) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            AlumnoTutor at = new AlumnoTutor(alumno, tutor, parentesco);
            s.persist(at);
            tx.commit();
        }
    }

    // ── CONFIGURACIÓN ─────────────────────────────────────────────────────────

    public static String getConfig(String clave) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Configuracion c = s.get(Configuracion.class, clave);
            return c != null ? c.getValor() : "";
        }
    }

    public static void setConfig(String clave, String valor) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            Configuracion c = s.get(Configuracion.class, clave);
            if (c == null) c = new Configuracion(clave, valor);
            else c.setValor(valor);
            s.merge(c);
            tx.commit();
        }
    }

    // ── MENSAJERÍA ───────────────────────────────────────────────────────────

    public static List<Mensaje> listarMensajes() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Mensaje ORDER BY enviadoEn DESC", Mensaje.class).list();
        }
    }

    public static void guardarMensaje(Mensaje mensaje) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = s.beginTransaction();
            s.persist(mensaje);
            tx.commit();
        }
    }
}

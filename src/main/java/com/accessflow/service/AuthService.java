package com.accessflow.service;

import com.accessflow.model.Admin;
import com.accessflow.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class AuthService {

    public static Admin login(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Admin admin = session.createQuery(
                "FROM Admin WHERE email = :email", Admin.class)
                .setParameter("email", email)
                .uniqueResult();
            if (admin != null && BCrypt.checkpw(password, admin.getPasswordHash())) {
                return admin;
            }
        }
        return null;
    }

    public static boolean registrar(String nombre, String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            Admin admin = new Admin(nombre, email, hash);
            session.persist(admin);
            tx.commit();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean actualizarAdmin(Admin admin, String nuevoNombre, String nuevoEmail, String nuevaPassword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Admin managed = session.get(Admin.class, admin.getId());
            managed.setNombre(nuevoNombre);
            managed.setEmail(nuevoEmail);
            if (nuevaPassword != null && !nuevaPassword.isBlank()) {
                managed.setPasswordHash(BCrypt.hashpw(nuevaPassword, BCrypt.gensalt()));
            }
            tx.commit();
            admin.setNombre(nuevoNombre);
            admin.setEmail(nuevoEmail);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean existeAdmin() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery("SELECT COUNT(a) FROM Admin a", Long.class).uniqueResult();
            return count != null && count > 0;
        }
    }

    public static List<Admin> listarAdmins() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Admin", Admin.class).list();
        }
    }
}

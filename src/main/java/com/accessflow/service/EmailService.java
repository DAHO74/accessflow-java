package com.accessflow.service;

import com.accessflow.model.Configuracion;
import com.accessflow.util.HibernateUtil;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class EmailService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    private static String getCfg(Session session, String clave, String defecto) {
        Configuracion c = session.get(Configuracion.class, clave);
        return c != null ? c.getValor() : defecto;
    }

    public static boolean enviarCorreo(String destinatario, String asunto, String cuerpo) {
        try (Session hSession = HibernateUtil.getSessionFactory().openSession()) {
            String host = getCfg(hSession, "smtp.host", "smtp.gmail.com");
            String port = getCfg(hSession, "smtp.port", "587");
            String email = getCfg(hSession, "smtp.email", "");
            String pass  = getCfg(hSession, "smtp.password", "");

            if (email.isBlank() || pass.isBlank()) return false;

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);

            jakarta.mail.Session mailSession = jakarta.mail.Session.getInstance(props,
                new Authenticator() {
                    @Override protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email, pass);
                    }
                });

            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(email));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject(asunto);
            msg.setText(cuerpo, "utf-8", "html");
            Transport.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean notificarEntrada(String tutorEmail, String alumnoNombre, LocalDateTime hora) {
        String asunto = "AccessFlow — Entrada de " + alumnoNombre;
        String cuerpo = "<p>El alumno <strong>" + alumnoNombre +
            "</strong> registró su <strong>entrada</strong> a las " +
            hora.format(FMT) + ".</p>";
        return enviarCorreo(tutorEmail, asunto, cuerpo);
    }

    public static boolean notificarSalida(String tutorEmail, String alumnoNombre, LocalDateTime hora) {
        String asunto = "AccessFlow — Salida de " + alumnoNombre;
        String cuerpo = "<p>El alumno <strong>" + alumnoNombre +
            "</strong> registró su <strong>salida</strong> a las " +
            hora.format(FMT) + ".</p>";
        return enviarCorreo(tutorEmail, asunto, cuerpo);
    }
}

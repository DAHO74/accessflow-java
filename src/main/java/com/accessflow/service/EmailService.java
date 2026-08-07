package com.accessflow.service;

import com.accessflow.dao.ConfiguracionDAO;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    private static final ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();

    /**
     * Envía un correo electrónico usando la configuración SMTP guardada en BD.
     * Devuelve null si el envío fue exitoso, o un mensaje de error en caso contrario.
     */
    public static String enviar(String destinatario, String asunto, String cuerpo) {
        String host     = configuracionDAO.obtener("smtp_host");
        String puerto   = configuracionDAO.obtener("smtp_puerto");
        String usuario  = configuracionDAO.obtener("smtp_usuario");
        String password = configuracionDAO.obtener("smtp_password");

        if (usuario.isEmpty() || password.isEmpty()) {
            return "SMTP no configurado. Ve a Configuración para ingresar las credenciales.";
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host",            host);
        props.put("mail.smtp.port",            puerto);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(usuario));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            Transport.send(msg);
            return null; // sin error
        } catch (MessagingException e) {
            return "Error al enviar: " + e.getMessage();
        }
    }
}

package com.accessflow.service;

import com.accessflow.dao.ConfiguracionDAO;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;

public class EmailService {

    private static final ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();

    public static String enviar(String destinatario, String asunto, String cuerpo) {
        return enviarConAdjunto(destinatario, asunto, cuerpo, null);
    }

    public static String enviarConAdjunto(String destinatario, String asunto, String cuerpo, File adjunto) {
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

            if (adjunto != null && adjunto.exists()) {
                MimeMultipart multipart = new MimeMultipart();

                MimeBodyPart textoParte = new MimeBodyPart();
                textoParte.setText(cuerpo, "utf-8");
                multipart.addBodyPart(textoParte);

                MimeBodyPart adjuntoParte = new MimeBodyPart();
                adjuntoParte.attachFile(adjunto);
                multipart.addBodyPart(adjuntoParte);

                msg.setContent(multipart);
            } else {
                msg.setText(cuerpo);
            }

            Transport.send(msg);
            return null;
        } catch (Exception e) {
            return "Error al enviar: " + e.getMessage();
        }
    }
}

package com.accessflow.controller;

import com.accessflow.service.CrudService;
import com.accessflow.service.EmailService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML private TextField smtpHostField;
    @FXML private TextField smtpPortField;
    @FXML private TextField smtpEmailField;
    @FXML private PasswordField smtpPasswordField;
    @FXML private Label statusLabel;

    @FXML private TextField dbHostField;
    @FXML private TextField dbPortField;
    @FXML private TextField dbNameField;
    @FXML private TextField dbUserField;
    @FXML private PasswordField dbPassField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        smtpHostField.setText(CrudService.getConfig("smtp.host"));
        smtpPortField.setText(CrudService.getConfig("smtp.port"));
        smtpEmailField.setText(CrudService.getConfig("smtp.email"));
        smtpPasswordField.setText(CrudService.getConfig("smtp.password"));
    }

    @FXML
    private void handleGuardarSMTP() {
        CrudService.setConfig("smtp.host",     smtpHostField.getText().trim());
        CrudService.setConfig("smtp.port",     smtpPortField.getText().trim());
        CrudService.setConfig("smtp.email",    smtpEmailField.getText().trim());
        CrudService.setConfig("smtp.password", smtpPasswordField.getText());
        statusLabel.setText("Configuracion SMTP guardada.");
    }

    @FXML
    private void handleProbarEmail() {
        statusLabel.setText("Enviando correo de prueba...");
        String dest = smtpEmailField.getText().trim();
        if (dest.isBlank()) { statusLabel.setText("Ingresa un correo SMTP."); return; }

        new Thread(() -> {
            boolean ok = EmailService.enviarCorreo(dest,
                "AccessFlow — Prueba de conexion",
                "<p>La configuracion SMTP funciona correctamente.</p>");
            javafx.application.Platform.runLater(() ->
                statusLabel.setText(ok ? "Correo enviado correctamente." : "Error al enviar. Verifica la configuracion."));
        }).start();
    }
}

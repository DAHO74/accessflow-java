package com.accessflow.controller;

import com.accessflow.model.Admin;
import com.accessflow.service.AuthService;
import com.accessflow.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private Label inicialLabel;
    @FXML private Label nombreActualLabel;
    @FXML private Label emailActualLabel;
    @FXML private Label creadoLabel;

    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private PasswordField passActualField;
    @FXML private PasswordField passNuevaField;
    @FXML private PasswordField passConfField;
    @FXML private Label statusLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatos();
    }

    private void cargarDatos() {
        Admin admin = SessionManager.getAdmin();
        String inicial = admin.getNombre().length() > 0
            ? String.valueOf(admin.getNombre().charAt(0)).toUpperCase() : "A";
        inicialLabel.setText(inicial);
        nombreActualLabel.setText(admin.getNombre());
        emailActualLabel.setText(admin.getEmail());
        if (admin.getCreadoEn() != null)
            creadoLabel.setText("Cuenta creada: " +
                admin.getCreadoEn().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        nombreField.setText(admin.getNombre());
        emailField.setText(admin.getEmail());
    }

    @FXML
    private void handleGuardar() {
        String nombre = nombreField.getText().trim();
        String email  = emailField.getText().trim();
        String passActual = passActualField.getText();
        String passNueva  = passNuevaField.getText();
        String passConf   = passConfField.getText();
        statusLabel.setText("");

        if (nombre.isBlank() || email.isBlank()) {
            statusLabel.setText("Nombre y email son requeridos."); return;
        }

        String nuevaPassword = null;
        if (!passNueva.isBlank()) {
            Admin admin = SessionManager.getAdmin();
            Admin check = AuthService.login(admin.getEmail(), passActual);
            if (check == null) { statusLabel.setText("Contraseña actual incorrecta."); return; }
            if (!passNueva.equals(passConf)) { statusLabel.setText("Las contraseñas no coinciden."); return; }
            if (passNueva.length() < 6) { statusLabel.setText("Mínimo 6 caracteres."); return; }
            nuevaPassword = passNueva;
        }

        boolean ok = AuthService.actualizarAdmin(SessionManager.getAdmin(), nombre, email, nuevaPassword);
        if (ok) {
            statusLabel.setText("Perfil actualizado correctamente.");
            passActualField.clear(); passNuevaField.clear(); passConfField.clear();
            cargarDatos();
        } else {
            statusLabel.setText("Error al guardar. El correo ya existe.");
        }
    }
}

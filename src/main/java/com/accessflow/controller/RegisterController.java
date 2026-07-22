package com.accessflow.controller;

import com.accessflow.service.AuthService;
import com.accessflow.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label errorLabel;
    @FXML private Label loginLink;

    @FXML
    private void handleRegistrar() {
        String nombre = nombreField.getText().trim();
        String email  = emailField.getText().trim();
        String pass   = passwordField.getText();
        String conf   = confirmField.getText();
        errorLabel.setText("");

        if (nombre.isBlank() || email.isBlank() || pass.isBlank()) {
            errorLabel.setText("Completa todos los campos.");
            return;
        }
        if (!pass.equals(conf)) {
            errorLabel.setText("Las contraseñas no coinciden.");
            return;
        }
        if (pass.length() < 6) {
            errorLabel.setText("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        boolean ok = AuthService.registrar(nombre, email, pass);
        if (ok) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Administrador registrado. Ahora puedes iniciar sesión.");
            alert.showAndWait();
            try { Navigator.showLogin(); } catch (Exception e) { e.printStackTrace(); }
        } else {
            errorLabel.setText("Error al registrar. El correo ya existe.");
        }
    }

    @FXML
    private void handleIrALogin() {
        try { Navigator.showLogin(); } catch (Exception e) { e.printStackTrace(); }
    }
}

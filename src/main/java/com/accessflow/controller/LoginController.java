package com.accessflow.controller;

import com.accessflow.model.Admin;
import com.accessflow.service.AuthService;
import com.accessflow.util.Navigator;
import com.accessflow.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginBtn;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        errorLabel.setText("");

        if (email.isBlank() || password.isBlank()) {
            errorLabel.setText("Completa todos los campos.");
            return;
        }

        loginBtn.setDisable(true);
        Admin admin = AuthService.login(email, password);

        if (admin != null) {
            SessionManager.setAdmin(admin);
            try { Navigator.showMain(); }
            catch (Exception e) { e.printStackTrace(); }
        } else {
            errorLabel.setText("Correo o contraseña incorrectos.");
            loginBtn.setDisable(false);
        }
    }

    @FXML
    private void handleRegistro() {
        try { Navigator.showRegister(); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleEnter(javafx.scene.input.KeyEvent e) {
        if (e.getCode() == javafx.scene.input.KeyCode.ENTER) handleLogin();
    }
}

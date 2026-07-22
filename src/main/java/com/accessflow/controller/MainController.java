package com.accessflow.controller;

import com.accessflow.util.Navigator;
import com.accessflow.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private Label adminNombreLabel;
    @FXML private Label adminEmailLabel;
    @FXML private VBox sidebarNav;

    private Button activeBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Navigator.setMainRoot(rootPane);
        adminNombreLabel.setText(SessionManager.getAdmin().getNombre());
        adminEmailLabel.setText(SessionManager.getAdmin().getEmail());
        handleDashboard();
    }

    private void setActive(Button btn) {
        if (activeBtn != null) activeBtn.getStyleClass().remove("nav-active");
        activeBtn = btn;
        btn.getStyleClass().add("nav-active");
    }

    @FXML private Button btnDashboard;
    @FXML private Button btnAsistencia;
    @FXML private Button btnHistorial;
    @FXML private Button btnAlumnos;
    @FXML private Button btnGrupos;
    @FXML private Button btnTutores;
    @FXML private Button btnMensajeria;
    @FXML private Button btnReportes;
    @FXML private Button btnPerfil;
    @FXML private Button btnConfiguracion;

    @FXML public void handleDashboard()    { setActive(btnDashboard);     Navigator.navigateTo("Dashboard.fxml"); }
    @FXML public void handleAsistencia()   { setActive(btnAsistencia);    Navigator.navigateTo("Attendance.fxml"); }
    @FXML public void handleHistorial()    { setActive(btnHistorial);     Navigator.navigateTo("AttendanceHistory.fxml"); }
    @FXML public void handleAlumnos()      { setActive(btnAlumnos);       Navigator.navigateTo("Students.fxml"); }
    @FXML public void handleGrupos()       { setActive(btnGrupos);        Navigator.navigateTo("Groups.fxml"); }
    @FXML public void handleTutores()      { setActive(btnTutores);       Navigator.navigateTo("Tutors.fxml"); }
    @FXML public void handleMensajeria()   { setActive(btnMensajeria);    Navigator.navigateTo("Messaging.fxml"); }
    @FXML public void handleReportes()     { setActive(btnReportes);      Navigator.navigateTo("Reports.fxml"); }
    @FXML public void handlePerfil()       { setActive(btnPerfil);        Navigator.navigateTo("Profile.fxml"); }
    @FXML public void handleConfiguracion(){ setActive(btnConfiguracion); Navigator.navigateTo("Settings.fxml"); }

    @FXML
    public void handleLogout() {
        SessionManager.logout();
        try { Navigator.showLogin(); }
        catch (Exception e) { e.printStackTrace(); }
    }
}

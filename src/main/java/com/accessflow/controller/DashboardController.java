package com.accessflow.controller;

import com.accessflow.model.Asistencia;
import com.accessflow.service.AttendanceService;
import com.accessflow.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Label bienvenidaLabel;
    @FXML private Label totalAlumnosLabel;
    @FXML private Label presentesLabel;
    @FXML private Label ausentesLabel;
    @FXML private Label totalGruposLabel;

    @FXML private TableView<Asistencia> tablaHoy;
    @FXML private TableColumn<Asistencia, String> colNombre;
    @FXML private TableColumn<Asistencia, String> colGrupo;
    @FXML private TableColumn<Asistencia, String> colEntrada;
    @FXML private TableColumn<Asistencia, String> colSalida;
    @FXML private TableColumn<Asistencia, String> colEstado;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bienvenidaLabel.setText("Bienvenido, " + SessionManager.getAdmin().getNombre());
        cargarEstadisticas();
        configurarTabla();
        cargarRegistrosHoy();
    }

    private void cargarEstadisticas() {
        long total    = AttendanceService.countTotalAlumnos();
        long presentes = AttendanceService.countPresentes();
        long grupos   = AttendanceService.countTotalGrupos();

        totalAlumnosLabel.setText(String.valueOf(total));
        presentesLabel.setText(String.valueOf(presentes));
        ausentesLabel.setText(String.valueOf(total - presentes));
        totalGruposLabel.setText(String.valueOf(grupos));
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getAlumno().getNombre()));
        colGrupo.setCellValueFactory(c -> {
            var grupo = c.getValue().getAlumno().getGrupo();
            return new javafx.beans.property.SimpleStringProperty(
                grupo != null ? grupo.getNombre() : "-");
        });
        colEntrada.setCellValueFactory(c -> {
            var v = c.getValue().getFechaEntrada();
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.format(FMT) : "-");
        });
        colSalida.setCellValueFactory(c -> {
            var v = c.getValue().getFechaSalida();
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.format(FMT) : "-");
        });
        colEstado.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
    }

    private void cargarRegistrosHoy() {
        List<Asistencia> lista = AttendanceService.getAsistenciasHoy();
        tablaHoy.setItems(FXCollections.observableArrayList(lista));
    }

    @FXML
    private void handleRefrescar() {
        cargarEstadisticas();
        cargarRegistrosHoy();
    }
}

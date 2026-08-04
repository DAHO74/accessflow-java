package com.accessflow.controller;

import com.accessflow.model.Alumno;
import com.accessflow.model.Asistencia;
import com.accessflow.model.AlumnoTutor;
import com.accessflow.service.AttendanceService;
import com.accessflow.service.EmailService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AttendanceController implements Initializable {

    @FXML private TextField inputField;
    @FXML private Label resultNombreLabel;
    @FXML private Label resultGrupoLabel;
    @FXML private Label resultHoraLabel;
    @FXML private Label resultTipoLabel;
    @FXML private Label resultPanel;

    @FXML private TableView<Asistencia> tablaHoy;
    @FXML private TableColumn<Asistencia, String> colNombre;
    @FXML private TableColumn<Asistencia, String> colGrupo;
    @FXML private TableColumn<Asistencia, String> colEntrada;
    @FXML private TableColumn<Asistencia, String> colSalida;
    @FXML private TableColumn<Asistencia, String> colEstado;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarTabla();
        inputField.requestFocus();
    }

    private void configurarTabla() {
        tablaHoy.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colNombre.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getAlumno().getNombre()));
        colGrupo.setCellValueFactory(c -> {
            var g = c.getValue().getAlumno().getGrupo();
            return new javafx.beans.property.SimpleStringProperty(g != null ? g.getNombre() : "-");
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

    @FXML
    private void handleRegistrar() {
        String input = inputField.getText().trim();
        if (input.isBlank()) return;

        Alumno alumno;
        try {
            alumno = AttendanceService.buscarPorRfid(input);
            if (alumno == null) alumno = AttendanceService.buscarPorId(Long.parseLong(input));
        } catch (NumberFormatException e) {
            alumno = AttendanceService.buscarPorRfid(input);
        }

        if (alumno == null) {
            mostrarResultado(null, "ALUMNO_NO_ENCONTRADO");
        } else {
            String tipo = AttendanceService.registrarAsistencia(alumno);
            mostrarResultado(alumno, tipo);

            final Alumno finalAlumno = alumno;
            final String finalTipo = tipo;
            new Thread(() -> {
                if (finalTipo.equals("ENTRADA") || finalTipo.equals("SALIDA")) {
                    for (AlumnoTutor at : finalAlumno.getTutores()) {
                        String email = at.getTutor().getEmail();
                        if (email != null && !email.isBlank()) {
                            if (finalTipo.equals("ENTRADA"))
                                EmailService.notificarEntrada(email, finalAlumno.getNombre(), LocalDateTime.now());
                            else
                                EmailService.notificarSalida(email, finalAlumno.getNombre(), LocalDateTime.now());
                        }
                    }
                }
            }).start();

            cargarTabla();
        }
        inputField.clear();
        inputField.requestFocus();
    }

    private void mostrarResultado(Alumno alumno, String tipo) {
        if ("ALUMNO_NO_ENCONTRADO".equals(tipo)) {
            resultNombreLabel.setText("Alumno no encontrado");
            resultGrupoLabel.setText("");
            resultHoraLabel.setText("");
            resultTipoLabel.setText("Sin registro");
            resultTipoLabel.getStyleClass().setAll("result-tipo", "tipo-error");
        } else if ("YA_REGISTRADO".equals(tipo)) {
            resultNombreLabel.setText(alumno.getNombre());
            resultGrupoLabel.setText(alumno.getGrupo() != null ? alumno.getGrupo().getNombre() : "");
            resultHoraLabel.setText(LocalDateTime.now().format(FMT));
            resultTipoLabel.setText("Ya registrado hoy");
            resultTipoLabel.getStyleClass().setAll("result-tipo", "tipo-warning");
        } else {
            resultNombreLabel.setText(alumno.getNombre());
            resultGrupoLabel.setText(alumno.getGrupo() != null ? alumno.getGrupo().getNombre() : "");
            resultHoraLabel.setText(LocalDateTime.now().format(FMT));
            String texto = tipo.equals("ENTRADA") ? "ENTRADA" : "SALIDA";
            resultTipoLabel.setText(texto);
            resultTipoLabel.getStyleClass().setAll("result-tipo",
                tipo.equals("ENTRADA") ? "tipo-entrada" : "tipo-salida");
        }
    }

    @FXML
    private void handleEnter(javafx.scene.input.KeyEvent e) {
        if (e.getCode() == javafx.scene.input.KeyCode.ENTER) handleRegistrar();
    }

    private void cargarTabla() {
        tablaHoy.setItems(FXCollections.observableArrayList(AttendanceService.getAsistenciasHoy()));
    }
}

package com.accessflow.controller;

import com.accessflow.model.Alumno;
import com.accessflow.model.Asistencia;
import com.accessflow.model.Grupo;
import com.accessflow.service.AttendanceService;
import com.accessflow.service.CrudService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AttendanceHistoryController implements Initializable {

    @FXML private DatePicker desdeField;
    @FXML private DatePicker hastaField;
    @FXML private ComboBox<Grupo> grupoCombo;
    @FXML private ComboBox<Alumno> alumnoCombo;
    @FXML private Label totalLabel;

    @FXML private TableView<Asistencia> tabla;
    @FXML private TableColumn<Asistencia, String> colNombre;
    @FXML private TableColumn<Asistencia, String> colGrupo;
    @FXML private TableColumn<Asistencia, String> colFecha;
    @FXML private TableColumn<Asistencia, String> colEntrada;
    @FXML private TableColumn<Asistencia, String> colSalida;
    @FXML private TableColumn<Asistencia, String> colEstado;

    private static final DateTimeFormatter FMT_DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_T  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_D  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        desdeField.setValue(LocalDate.now().minusDays(7));
        hastaField.setValue(LocalDate.now());

        Grupo todos = new Grupo("Todos los grupos", "", "");
        grupoCombo.getItems().add(todos);
        grupoCombo.getItems().addAll(CrudService.listarGrupos());
        grupoCombo.setValue(todos);

        Alumno todosA = new Alumno("Todos los alumnos", null, null);
        alumnoCombo.getItems().add(todosA);
        alumnoCombo.getItems().addAll(CrudService.listarAlumnos());
        alumnoCombo.setValue(todosA);

        grupoCombo.setOnAction(e -> {
            Grupo g = grupoCombo.getValue();
            alumnoCombo.getItems().clear();
            alumnoCombo.getItems().add(todosA);
            if (g != null && g.getId() != null) {
                alumnoCombo.getItems().addAll(CrudService.listarAlumnosPorGrupo(g.getId()));
            } else {
                alumnoCombo.getItems().addAll(CrudService.listarAlumnos());
            }
            alumnoCombo.setValue(todosA);
        });

        configurarTabla();
        buscar();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getAlumno().getNombre()));
        colGrupo.setCellValueFactory(c -> {
            var g = c.getValue().getAlumno().getGrupo();
            return new javafx.beans.property.SimpleStringProperty(g != null ? g.getNombre() : "-");
        });
        colFecha.setCellValueFactory(c -> {
            var v = c.getValue().getFechaEntrada();
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.format(FMT_D) : "-");
        });
        colEntrada.setCellValueFactory(c -> {
            var v = c.getValue().getFechaEntrada();
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.format(FMT_T) : "-");
        });
        colSalida.setCellValueFactory(c -> {
            var v = c.getValue().getFechaSalida();
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.format(FMT_T) : "-");
        });
        colEstado.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
    }

    @FXML
    private void buscar() {
        LocalDate desde = desdeField.getValue();
        LocalDate hasta = hastaField.getValue();
        Grupo g = grupoCombo.getValue();
        Alumno a = alumnoCombo.getValue();

        Long grupoId  = (g != null && g.getId() != null) ? g.getId() : null;
        Long alumnoId = (a != null && a.getId() != null) ? a.getId() : null;

        List<Asistencia> resultados = AttendanceService.getHistorial(desde, hasta, grupoId, alumnoId);
        tabla.setItems(FXCollections.observableArrayList(resultados));
        totalLabel.setText("Total: " + resultados.size() + " registros");
    }

    @FXML
    private void limpiarFiltros() {
        desdeField.setValue(LocalDate.now().minusDays(7));
        hastaField.setValue(LocalDate.now());
        grupoCombo.getSelectionModel().selectFirst();
        alumnoCombo.getSelectionModel().selectFirst();
        buscar();
    }
}

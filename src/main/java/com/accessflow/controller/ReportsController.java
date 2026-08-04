package com.accessflow.controller;

import com.accessflow.model.Asistencia;
import com.accessflow.model.Grupo;
import com.accessflow.service.AttendanceService;
import com.accessflow.service.CrudService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML private DatePicker desdeField;
    @FXML private DatePicker hastaField;
    @FXML private ComboBox<Grupo> grupoCombo;
    @FXML private Label totalLabel;
    @FXML private Label presentesLabel;
    @FXML private Label promedioLabel;

    @FXML private TableView<Asistencia> tabla;
    @FXML private TableColumn<Asistencia, String> colNombre;
    @FXML private TableColumn<Asistencia, String> colGrupo;
    @FXML private TableColumn<Asistencia, String> colFecha;
    @FXML private TableColumn<Asistencia, String> colEntrada;
    @FXML private TableColumn<Asistencia, String> colSalida;

    private static final DateTimeFormatter FMT_T = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_D = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<Asistencia> currentData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        desdeField.setValue(LocalDate.now().minusDays(30));
        hastaField.setValue(LocalDate.now());

        Grupo todos = new Grupo("Todos los grupos", "", "");
        grupoCombo.getItems().add(todos);
        grupoCombo.getItems().addAll(CrudService.listarGrupos());
        grupoCombo.setValue(todos);

        configurarTabla();
        generarReporte();
    }

    private void configurarTabla() {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.format(FMT_T) : "En escuela");
        });
    }

    @FXML
    private void generarReporte() {
        LocalDate desde = desdeField.getValue();
        LocalDate hasta = hastaField.getValue();
        Grupo g = grupoCombo.getValue();
        Long grupoId = (g != null && g.getId() != null) ? g.getId() : null;

        currentData = AttendanceService.getHistorial(desde, hasta, grupoId, null);
        tabla.setItems(FXCollections.observableArrayList(currentData));

        long dias = java.time.temporal.ChronoUnit.DAYS.between(desde, hasta) + 1;
        long total = currentData.size();
        double promedio = dias > 0 ? (double) total / dias : 0;

        totalLabel.setText(String.valueOf(total));
        promedioLabel.setText(String.format("%.1f / día", promedio));
    }

    @FXML
    private void exportarCSV() {
        if (currentData == null || currentData.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No hay datos para exportar.", ButtonType.OK).showAndWait();
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar reporte CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("reporte_asistencias.csv");
        File file = fc.showSaveDialog(tabla.getScene().getWindow());

        if (file != null) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
                pw.println("Alumno,Grupo,Fecha,Entrada,Salida,Estado");
                for (Asistencia a : currentData) {
                    String grupo = a.getAlumno().getGrupo() != null ? a.getAlumno().getGrupo().getNombre() : "";
                    String entrada = a.getFechaEntrada() != null ? a.getFechaEntrada().format(FMT_T) : "";
                    String salida  = a.getFechaSalida()  != null ? a.getFechaSalida().format(FMT_T)  : "";
                    String fecha   = a.getFechaEntrada() != null ? a.getFechaEntrada().format(FMT_D)  : "";
                    pw.printf("%s,%s,%s,%s,%s,%s%n",
                        a.getAlumno().getNombre(), grupo, fecha, entrada, salida, a.getEstado());
                }
                new Alert(Alert.AlertType.INFORMATION,
                    "Reporte exportado a:\n" + file.getAbsolutePath(), ButtonType.OK).showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Error al exportar: " + e.getMessage(), ButtonType.OK).showAndWait();
            }
        }
    }
}

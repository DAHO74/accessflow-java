package com.accessflow.controller;

import com.accessflow.model.Tutor;
import com.accessflow.service.CrudService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class TutorsController implements Initializable {

    @FXML private TextField buscarField;
    @FXML private TableView<Tutor> tabla;
    @FXML private TableColumn<Tutor, Long>   colId;
    @FXML private TableColumn<Tutor, String> colNombre;
    @FXML private TableColumn<Tutor, String> colEmail;
    @FXML private TableColumn<Tutor, String> colTelefono;
    @FXML private TableColumn<Tutor, String> colAlumnos;
    @FXML private Label totalLabel;

    private ObservableList<Tutor> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(c ->
            new javafx.beans.property.SimpleLongProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colEmail.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colTelefono.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefono()));
        colAlumnos.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getAlumnos().size())));

        tabla.setItems(data);
        cargar();
    }

    private void cargar() {
        String buscar = buscarField != null ? buscarField.getText().trim().toLowerCase() : "";
        var lista = CrudService.listarTutores();
        if (!buscar.isBlank()) {
            lista = lista.stream()
                .filter(t -> t.getNombre().toLowerCase().contains(buscar))
                .toList();
        }
        data.setAll(lista);
        totalLabel.setText("Total: " + data.size() + " tutores");
    }

    @FXML private void handleBuscar() { cargar(); }
    @FXML private void handleAgregar() { abrirFormulario(null); }

    @FXML
    private void handleEditar() {
        Tutor sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alertInfo("Selecciona un tutor."); return; }
        abrirFormulario(sel);
    }

    @FXML
    private void handleEliminar() {
        Tutor sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alertInfo("Selecciona un tutor."); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar tutor " + sel.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        conf.setHeaderText(null);
        Optional<ButtonType> r = conf.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.YES) {
            CrudService.eliminarTutor(sel.getId());
            cargar();
        }
    }

    private void abrirFormulario(Tutor tutor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TutorForm.fxml"));
            DialogPane pane = loader.load();
            TutorFormController ctrl = loader.getController();
            ctrl.setTutor(tutor);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(tutor == null ? "Nuevo Tutor" : "Editar Tutor");
            dialog.setDialogPane(pane);
            dialog.showAndWait();
            cargar();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void alertInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}

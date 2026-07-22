package com.accessflow.controller;

import com.accessflow.model.Grupo;
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

public class GroupsController implements Initializable {

    @FXML private TableView<Grupo> tabla;
    @FXML private TableColumn<Grupo, Long>   colId;
    @FXML private TableColumn<Grupo, String> colNombre;
    @FXML private TableColumn<Grupo, String> colGrado;
    @FXML private TableColumn<Grupo, String> colTurno;
    @FXML private TableColumn<Grupo, String> colAlumnos;
    @FXML private Label totalLabel;

    private ObservableList<Grupo> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(c ->
            new javafx.beans.property.SimpleLongProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colGrado.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getGrado()));
        colTurno.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getTurno()));
        colAlumnos.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getAlumnos().size())));

        tabla.setItems(data);
        cargar();
    }

    private void cargar() {
        data.setAll(CrudService.listarGrupos());
        totalLabel.setText("Total: " + data.size() + " grupos");
    }

    @FXML
    private void handleAgregar() { abrirFormulario(null); }

    @FXML
    private void handleEditar() {
        Grupo sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alertInfo("Selecciona un grupo."); return; }
        abrirFormulario(sel);
    }

    @FXML
    private void handleEliminar() {
        Grupo sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alertInfo("Selecciona un grupo."); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar grupo " + sel.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        conf.setHeaderText(null);
        Optional<ButtonType> r = conf.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.YES) {
            CrudService.eliminarGrupo(sel.getId());
            cargar();
        }
    }

    private void abrirFormulario(Grupo grupo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GroupForm.fxml"));
            DialogPane pane = loader.load();
            GroupFormController ctrl = loader.getController();
            ctrl.setGrupo(grupo);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(grupo == null ? "Nuevo Grupo" : "Editar Grupo");
            dialog.setDialogPane(pane);
            dialog.showAndWait();
            cargar();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void alertInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}

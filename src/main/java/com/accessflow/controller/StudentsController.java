package com.accessflow.controller;

import com.accessflow.model.Alumno;
import com.accessflow.model.Grupo;
import com.accessflow.service.CrudService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class StudentsController implements Initializable {

    @FXML private TextField buscarField;
    @FXML private ComboBox<Grupo> grupoFiltro;
    @FXML private TableView<Alumno> tabla;
    @FXML private TableColumn<Alumno, Long>   colId;
    @FXML private TableColumn<Alumno, String> colNombre;
    @FXML private TableColumn<Alumno, String> colRfid;
    @FXML private TableColumn<Alumno, String> colGrupo;
    @FXML private Label  totalLabel;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    private ObservableList<Alumno> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Grupo todos = new Grupo("Todos", "", "");
        grupoFiltro.getItems().add(todos);
        grupoFiltro.getItems().addAll(CrudService.listarGrupos());
        grupoFiltro.setValue(todos);

        colId.setCellValueFactory(c ->
            new javafx.beans.property.SimpleLongProperty(c.getValue().getId()).asObject());
        colNombre.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colRfid.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getRfidUid() != null ? c.getValue().getRfidUid() : "-"));
        colGrupo.setCellValueFactory(c -> {
            var g = c.getValue().getGrupo();
            return new javafx.beans.property.SimpleStringProperty(g != null ? g.getNombre() : "-");
        });

        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setItems(data);

        var seleccion = tabla.getSelectionModel().selectedItemProperty();
        btnEditar.disableProperty().bind(seleccion.isNull());
        btnEliminar.disableProperty().bind(seleccion.isNull());

        tabla.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && tabla.getSelectionModel().getSelectedItem() != null)
                abrirFormulario(tabla.getSelectionModel().getSelectedItem());
        });

        cargar();
    }

    private void cargar() {
        Grupo g = grupoFiltro.getValue();
        List<Alumno> lista;
        if (g == null || g.getId() == null) {
            lista = CrudService.listarAlumnos();
        } else {
            lista = CrudService.listarAlumnosPorGrupo(g.getId());
        }
        String buscar = buscarField.getText().trim().toLowerCase();
        if (!buscar.isBlank()) {
            lista = lista.stream()
                .filter(a -> a.getNombre().toLowerCase().contains(buscar))
                .toList();
        }
        data.setAll(lista);
        totalLabel.setText("Total: " + data.size());
    }

    @FXML private void handleBuscar() { cargar(); }
    @FXML private void handleFiltroGrupo() { cargar(); }

    @FXML
    private void handleAgregar() {
        abrirFormulario(null);
    }

    @FXML
    private void handleEditar() {
        Alumno sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alertInfo("Selecciona un alumno."); return; }
        abrirFormulario(sel);
    }

    @FXML
    private void handleEliminar() {
        Alumno sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alertInfo("Selecciona un alumno."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar a " + sel.getNombre() + "?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            CrudService.eliminarAlumno(sel.getId());
            cargar();
        }
    }

    private void abrirFormulario(Alumno alumno) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StudentForm.fxml"));
            DialogPane pane = loader.load();
            StudentFormController ctrl = loader.getController();
            ctrl.setAlumno(alumno);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle(alumno == null ? "Nuevo Alumno" : "Editar Alumno");
            dialog.setDialogPane(pane);
            dialog.showAndWait();
            cargar();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void alertInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}

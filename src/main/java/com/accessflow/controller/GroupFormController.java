package com.accessflow.controller;

import com.accessflow.model.Grupo;
import com.accessflow.service.CrudService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class GroupFormController implements Initializable {

    @FXML private TextField nombreField;
    @FXML private TextField gradoField;
    @FXML private ComboBox<String> turnoCombo;
    @FXML private Label errorLabel;
    @FXML private Button guardarBtn;

    private Grupo grupo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        turnoCombo.getItems().addAll("Matutino", "Vespertino");
        turnoCombo.setValue("Matutino");
        guardarBtn.setOnAction(e -> handleGuardar());
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
        if (grupo != null) {
            nombreField.setText(grupo.getNombre());
            gradoField.setText(grupo.getGrado());
            turnoCombo.setValue(grupo.getTurno());
        }
    }

    @FXML
    private void handleGuardar() {
        String nombre = nombreField.getText().trim();
        String grado  = gradoField.getText().trim();
        String turno  = turnoCombo.getValue();

        if (nombre.isBlank() || grado.isBlank()) {
            errorLabel.setText("Nombre y grado son requeridos."); return;
        }

        if (grupo == null) grupo = new Grupo();
        grupo.setNombre(nombre);
        grupo.setGrado(grado);
        grupo.setTurno(turno);

        CrudService.guardarGrupo(grupo);
        guardarBtn.getScene().getWindow().hide();
    }
}

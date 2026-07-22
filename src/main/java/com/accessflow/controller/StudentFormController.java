package com.accessflow.controller;

import com.accessflow.model.Alumno;
import com.accessflow.model.Grupo;
import com.accessflow.service.CrudService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentFormController implements Initializable {

    @FXML private TextField nombreField;
    @FXML private TextField rfidField;
    @FXML private ComboBox<Grupo> grupoCombo;
    @FXML private Label errorLabel;
    @FXML private Button guardarBtn;

    private Alumno alumno;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        grupoCombo.getItems().addAll(CrudService.listarGrupos());

        guardarBtn.setOnAction(e -> handleGuardar());
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
        if (alumno != null) {
            nombreField.setText(alumno.getNombre());
            rfidField.setText(alumno.getRfidUid() != null ? alumno.getRfidUid() : "");
            grupoCombo.setValue(alumno.getGrupo());
        }
    }

    @FXML
    private void handleGuardar() {
        String nombre = nombreField.getText().trim();
        String rfid   = rfidField.getText().trim();
        Grupo grupo   = grupoCombo.getValue();

        if (nombre.isBlank()) { errorLabel.setText("El nombre es requerido."); return; }

        if (alumno == null) alumno = new Alumno();
        alumno.setNombre(nombre);
        alumno.setRfidUid(rfid.isBlank() ? null : rfid);
        alumno.setGrupo(grupo);

        CrudService.guardarAlumno(alumno);
        guardarBtn.getScene().getWindow().hide();
    }
}

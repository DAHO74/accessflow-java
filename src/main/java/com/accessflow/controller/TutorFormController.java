package com.accessflow.controller;

import com.accessflow.model.Alumno;
import com.accessflow.model.Tutor;
import com.accessflow.service.CrudService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class TutorFormController implements Initializable {

    @FXML private TextField nombreField;
    @FXML private TextField emailField;
    @FXML private TextField telefonoField;
    @FXML private ComboBox<Alumno> alumnoCombo;
    @FXML private ComboBox<String> parentescoCombo;
    @FXML private Label errorLabel;
    @FXML private Button guardarBtn;

    private Tutor tutor;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        parentescoCombo.getItems().addAll("Padre", "Madre", "Tutor", "Abuelo/a", "Otro");
        parentescoCombo.setValue("Padre");
        alumnoCombo.getItems().add(new Alumno("-- Sin asignar --", null, null));
        alumnoCombo.getItems().addAll(CrudService.listarAlumnos());
        alumnoCombo.getSelectionModel().selectFirst();
        guardarBtn.setOnAction(e -> handleGuardar());
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
        if (tutor != null) {
            nombreField.setText(tutor.getNombre());
            emailField.setText(tutor.getEmail() != null ? tutor.getEmail() : "");
            telefonoField.setText(tutor.getTelefono() != null ? tutor.getTelefono() : "");
        }
    }

    @FXML
    private void handleGuardar() {
        String nombre    = nombreField.getText().trim();
        String email     = emailField.getText().trim();
        String telefono  = telefonoField.getText().trim();
        Alumno alumno    = alumnoCombo.getValue();
        String parentesco = parentescoCombo.getValue();

        if (nombre.isBlank()) { errorLabel.setText("El nombre es requerido."); return; }

        if (tutor == null) tutor = new Tutor();
        tutor.setNombre(nombre);
        tutor.setEmail(email.isBlank() ? null : email);
        tutor.setTelefono(telefono.isBlank() ? null : telefono);

        CrudService.guardarTutor(tutor);

        if (alumno != null && alumno.getId() != null) {
            CrudService.vincularTutor(alumno, tutor, parentesco);
        }

        guardarBtn.getScene().getWindow().hide();
    }
}

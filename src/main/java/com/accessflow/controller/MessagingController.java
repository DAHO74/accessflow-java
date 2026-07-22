package com.accessflow.controller;

import com.accessflow.model.*;
import com.accessflow.service.CrudService;
import com.accessflow.service.EmailService;
import com.accessflow.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MessagingController implements Initializable {

    @FXML private ToggleButton btnIndividual;
    @FXML private ToggleButton btnGrupal;
    @FXML private ComboBox<Tutor> tutorCombo;
    @FXML private ComboBox<Grupo> grupoCombo;
    @FXML private Label tutorLabel;
    @FXML private Label grupoLabel;
    @FXML private TextField asuntoField;
    @FXML private TextArea cuerpoArea;
    @FXML private Label statusLabel;

    @FXML private TableView<Mensaje> historialTabla;
    @FXML private TableColumn<Mensaje, String> colFecha;
    @FXML private TableColumn<Mensaje, String> colTipo;
    @FXML private TableColumn<Mensaje, String> colAsunto;
    @FXML private TableColumn<Mensaje, String> colDestinatarios;

    private ToggleGroup tipoGroup = new ToggleGroup();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnIndividual.setToggleGroup(tipoGroup);
        btnGrupal.setToggleGroup(tipoGroup);
        btnIndividual.setSelected(true);

        tutorCombo.getItems().addAll(CrudService.listarTutores());
        grupoCombo.getItems().addAll(CrudService.listarGrupos());

        tipoGroup.selectedToggleProperty().addListener((obs, o, n) -> actualizarVista());
        actualizarVista();

        configurarHistorial();
        cargarHistorial();
    }

    private void actualizarVista() {
        boolean individual = btnIndividual.isSelected();
        tutorCombo.setVisible(individual);
        tutorLabel.setVisible(individual);
        grupoCombo.setVisible(!individual);
        grupoLabel.setVisible(!individual);
    }

    private void configurarHistorial() {
        colFecha.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getEnviadoEn().format(FMT)));
        colTipo.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getTipo()));
        colAsunto.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getAsunto()));
        colDestinatarios.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(c.getValue().getDestinatarios().size())));
    }

    private void cargarHistorial() {
        historialTabla.setItems(FXCollections.observableArrayList(CrudService.listarMensajes()));
    }

    @FXML
    private void handleEnviar() {
        String asunto = asuntoField.getText().trim();
        String cuerpo = cuerpoArea.getText().trim();
        statusLabel.setText("");

        if (asunto.isBlank() || cuerpo.isBlank()) {
            statusLabel.setText("Completa asunto y mensaje.");
            return;
        }

        boolean individual = btnIndividual.isSelected();
        List<Tutor> destinatarios = new ArrayList<>();

        if (individual) {
            Tutor t = tutorCombo.getValue();
            if (t == null) { statusLabel.setText("Selecciona un tutor."); return; }
            destinatarios.add(t);
        } else {
            Grupo g = grupoCombo.getValue();
            if (g == null) { statusLabel.setText("Selecciona un grupo."); return; }
            CrudService.listarAlumnosPorGrupo(g.getId()).forEach(a ->
                a.getTutores().forEach(at -> {
                    if (!destinatarios.contains(at.getTutor()))
                        destinatarios.add(at.getTutor());
                }));
        }

        Admin admin = SessionManager.getAdmin();
        Mensaje msg = new Mensaje(admin, asunto, cuerpo, individual ? "individual" : "grupal");
        CrudService.guardarMensaje(msg);

        int enviados = 0;
        for (Tutor t : destinatarios) {
            if (t.getEmail() != null && !t.getEmail().isBlank()) {
                MensajeDestinatario md = new MensajeDestinatario(msg, t);
                if (EmailService.enviarCorreo(t.getEmail(), asunto, cuerpo)) {
                    enviados++;
                }
            }
        }

        asuntoField.clear();
        cuerpoArea.clear();
        statusLabel.setText("Enviado a " + enviados + " / " + destinatarios.size() + " destinatarios.");
        cargarHistorial();
    }
}

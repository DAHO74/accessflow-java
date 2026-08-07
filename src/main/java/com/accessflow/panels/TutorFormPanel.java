package com.accessflow.panels;

import com.accessflow.dao.TutorDAO;
import com.accessflow.model.Tutor;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TutorFormPanel extends JPanel {

    private final MainFrame mainFrame;
    private final Tutor     tutor;     // null cuando se crea un nuevo tutor

    private final TutorDAO tutorDAO = new TutorDAO();

    private JTextField campoNombre;
    private JTextField campoEmail;
    private JTextField campoTelefono;
    private JLabel     lblError;

    public TutorFormPanel(MainFrame mainFrame, Tutor tutor) {
        this.mainFrame = mainFrame;
        this.tutor     = tutor;

        setLayout(new BorderLayout(0, 0));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);

        if (tutor != null) {
            llenarFormulario();
        }
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ
    // ═══════════════════════════════════════════════

    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colores.FONDO_OSCURO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        String textoTitulo = (tutor == null) ? "Nuevo tutor" : "Editar tutor";
        JLabel titulo = new JLabel(textoTitulo);
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);

        JButton btnVolver = Componentes.botonSecundario("← Volver");
        btnVolver.addActionListener(e -> mainFrame.irATutores());

        header.add(titulo,    BorderLayout.WEST);
        header.add(btnVolver, BorderLayout.EAST);
        return header;
    }

    private JPanel construirCentro() {
        JPanel exterior = new JPanel(new GridBagLayout());
        exterior.setBackground(Colores.FONDO_OSCURO);
        exterior.add(construirCard());
        return exterior;
    }

    private JPanel construirCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Colores.FONDO_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(32, 32, 32, 32)
        ));

        campoNombre    = new JTextField();
        campoEmail     = new JTextField();
        campoTelefono  = new JTextField();
        Componentes.estilizarCampo(campoNombre);
        Componentes.estilizarCampo(campoEmail);
        Componentes.estilizarCampo(campoTelefono);

        lblError = new JLabel(" ");
        lblError.setFont(Colores.FUENTE_PEQUEÑA);
        lblError.setForeground(Colores.ROJO);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblError.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JButton btnCancelar = Componentes.botonSecundario("Cancelar");
        JButton btnGuardar  = Componentes.botonPrimario("Guardar");
        btnCancelar.addActionListener(e -> mainFrame.irATutores());
        btnGuardar.addActionListener(e  -> guardar());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setBackground(Colores.FONDO_PANEL);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        card.add(crearEtiqueta("Nombre completo *"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoNombre);
        card.add(Box.createVerticalStrut(16));
        card.add(crearEtiqueta("Correo electrónico  (se usará para enviar notificaciones)"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoEmail);
        card.add(Box.createVerticalStrut(16));
        card.add(crearEtiqueta("Teléfono"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoTelefono);
        card.add(Box.createVerticalStrut(14));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        card.add(panelBotones);

        return card;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void llenarFormulario() {
        campoNombre.setText(tutor.getNombre());
        campoEmail.setText(tutor.getEmail()       != null ? tutor.getEmail()       : "");
        campoTelefono.setText(tutor.getTelefono() != null ? tutor.getTelefono()    : "");
    }

    private void guardar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            lblError.setText("El nombre es obligatorio.");
            return;
        }

        String email    = campoEmail.getText().trim();
        String telefono = campoTelefono.getText().trim();
        if (email.isEmpty())    email    = null;
        if (telefono.isEmpty()) telefono = null;

        boolean exito;
        if (tutor == null) {
            Tutor nuevo = new Tutor(nombre, email, telefono);
            exito = tutorDAO.insertar(nuevo);
        } else {
            tutor.setNombre(nombre);
            tutor.setEmail(email);
            tutor.setTelefono(telefono);
            exito = tutorDAO.actualizar(tutor);
        }

        if (exito) {
            mainFrame.irATutores();
        } else {
            lblError.setText("Error al guardar el tutor. Intenta de nuevo.");
        }
    }

    // ── Auxiliar de estilo ───────────────────────────

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }
}

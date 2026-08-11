package com.accessflow.panels;

import com.accessflow.dao.AlumnoDAO;
import com.accessflow.dao.TutorDAO;
import com.accessflow.model.Alumno;
import com.accessflow.model.Tutor;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TutorFormPanel extends JPanel {

    private final MainFrame mainFrame;
    private final Tutor     tutor;

    private final TutorDAO  tutorDAO  = new TutorDAO();
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();

    private JTextField campoNombre;
    private JTextField campoEmail;
    private JTextField campoTelefono;
    private JTextField campoBuscar;
    private JLabel     lblError;
    private JLabel     lblConteo;

    private JPanel panelCheckboxes;

    private final List<Alumno>    todosLosAlumnos   = new ArrayList<>();
    private final List<JCheckBox> checkboxesAlumnos = new ArrayList<>();

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
        JPanel centro = new JPanel(new BorderLayout(16, 0));
        centro.setBackground(Colores.FONDO_OSCURO);

        centro.add(construirPanelDatos(),   BorderLayout.WEST);
        centro.add(construirPanelAlumnos(), BorderLayout.CENTER);

        return centro;
    }

    // ── Panel izquierdo: datos del tutor ─────────────

    private JPanel construirPanelDatos() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colores.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(28, 28, 28, 28)
        ));
        panel.setPreferredSize(new Dimension(360, 0));

        campoNombre   = new JTextField();
        campoEmail    = new JTextField();
        campoTelefono = new JTextField();
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

        panel.add(crearEtiqueta("Nombre completo *"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(campoNombre);
        panel.add(Box.createVerticalStrut(16));
        panel.add(crearEtiqueta("Correo electrónico"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(campoEmail);
        panel.add(Box.createVerticalStrut(16));
        panel.add(crearEtiqueta("Teléfono"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(campoTelefono);
        panel.add(Box.createVerticalGlue());
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(8));
        panel.add(panelBotones);

        return panel;
    }

    // ── Panel derecho: lista de alumnos con buscador ─

    private JPanel construirPanelAlumnos() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Colores.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(28, 28, 28, 28)
        ));

        // ── Cabecera ──────────────────────────────────
        JPanel cabecera = new JPanel(new BorderLayout(0, 6));
        cabecera.setBackground(Colores.FONDO_PANEL);

        JLabel lblSeccion = new JLabel("Alumnos asignados a este tutor");
        lblSeccion.setFont(Colores.FUENTE_BOLD);
        lblSeccion.setForeground(Colores.TEXTO_CLARO);

        lblConteo = new JLabel(" ");
        lblConteo.setFont(Colores.FUENTE_PEQUEÑA);
        lblConteo.setForeground(Colores.TEXTO_GRIS);
        lblConteo.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel filaTitulo = new JPanel(new BorderLayout());
        filaTitulo.setBackground(Colores.FONDO_PANEL);
        filaTitulo.add(lblSeccion, BorderLayout.WEST);
        filaTitulo.add(lblConteo,  BorderLayout.EAST);

        // Campo de búsqueda
        campoBuscar = new JTextField();
        campoBuscar.setBackground(Colores.FONDO_OSCURO);
        campoBuscar.setForeground(Colores.TEXTO_CLARO);
        campoBuscar.setCaretColor(Colores.TEXTO_CLARO);
        campoBuscar.setFont(Colores.FUENTE_NORMAL);
        campoBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
        campoBuscar.putClientProperty("JTextField.placeholderText", "Buscar alumno por nombre...");
        campoBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrar(); }
            public void removeUpdate(DocumentEvent e)  { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        cabecera.add(filaTitulo,  BorderLayout.NORTH);
        cabecera.add(campoBuscar, BorderLayout.SOUTH);

        // ── Lista de checkboxes ───────────────────────
        panelCheckboxes = new JPanel();
        panelCheckboxes.setLayout(new BoxLayout(panelCheckboxes, BoxLayout.Y_AXIS));
        panelCheckboxes.setBackground(Colores.FONDO_OSCURO);
        panelCheckboxes.setBorder(new EmptyBorder(8, 8, 8, 8));

        cargarCheckboxes();

        JScrollPane scroll = new JScrollPane(panelCheckboxes);
        scroll.setBackground(Colores.FONDO_OSCURO);
        scroll.getViewport().setBackground(Colores.FONDO_OSCURO);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);

        return panel;
    }

    private void cargarCheckboxes() {
        todosLosAlumnos.clear();
        checkboxesAlumnos.clear();
        panelCheckboxes.removeAll();

        List<Alumno> alumnos = alumnoDAO.listarTodos();
        int tutorIdActual = (tutor != null) ? tutor.getId() : -1;

        if (alumnos.isEmpty()) {
            JLabel lbl = new JLabel("No hay alumnos registrados aún.");
            lbl.setFont(Colores.FUENTE_PEQUEÑA);
            lbl.setForeground(Colores.TEXTO_GRIS);
            panelCheckboxes.add(lbl);
        } else {
            for (Alumno a : alumnos) {
                todosLosAlumnos.add(a);
                boolean asignado = (a.getTutorId() == tutorIdActual && tutorIdActual > 0);
                JCheckBox cb = new JCheckBox(a.getNombre(), asignado);
                cb.setFont(Colores.FUENTE_NORMAL);
                cb.setForeground(Colores.TEXTO_CLARO);
                cb.setBackground(Colores.FONDO_OSCURO);
                cb.setFocusPainted(false);
                cb.setAlignmentX(Component.LEFT_ALIGNMENT);
                checkboxesAlumnos.add(cb);
                panelCheckboxes.add(cb);
                panelCheckboxes.add(Box.createVerticalStrut(2));
            }
        }
        actualizarConteo();
    }

    private void filtrar() {
        String texto = campoBuscar.getText().trim().toLowerCase();
        int visibles = 0;

        for (int i = 0; i < checkboxesAlumnos.size(); i++) {
            JCheckBox cb = checkboxesAlumnos.get(i);
            boolean coincide = texto.isEmpty()
                || todosLosAlumnos.get(i).getNombre().toLowerCase().contains(texto);
            cb.setVisible(coincide);
            if (coincide) visibles++;
        }

        panelCheckboxes.revalidate();
        panelCheckboxes.repaint();
        actualizarConteo(visibles);
    }

    private void actualizarConteo() {
        long seleccionados = checkboxesAlumnos.stream().filter(JCheckBox::isSelected).count();
        lblConteo.setText(seleccionados + " seleccionado(s) · " + todosLosAlumnos.size() + " total");
    }

    private void actualizarConteo(int visibles) {
        long seleccionados = checkboxesAlumnos.stream().filter(JCheckBox::isSelected).count();
        lblConteo.setText(seleccionados + " seleccionado(s) · " + visibles + " visible(s) de " + todosLosAlumnos.size());
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

        int tutorId;
        boolean exito;

        if (tutor == null) {
            Tutor nuevo = new Tutor(nombre, email, telefono);
            tutorId = tutorDAO.insertarConId(nuevo);
            exito   = tutorId > 0;
        } else {
            tutor.setNombre(nombre);
            tutor.setEmail(email);
            tutor.setTelefono(telefono);
            exito   = tutorDAO.actualizar(tutor);
            tutorId = tutor.getId();
        }

        if (!exito) {
            lblError.setText("Error al guardar el tutor. Intenta de nuevo.");
            return;
        }

        List<Integer> seleccionados = new ArrayList<>();
        for (int i = 0; i < checkboxesAlumnos.size(); i++) {
            if (checkboxesAlumnos.get(i).isSelected()) {
                seleccionados.add(todosLosAlumnos.get(i).getId());
            }
        }
        alumnoDAO.asignarAlumnos(tutorId, seleccionados);

        mainFrame.irATutores();
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

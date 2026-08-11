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
    private JLabel     lblError;

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

        // ── Separador + sección de alumnos ──────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(Colores.BORDE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSeccion = new JLabel("Alumnos asignados a este tutor");
        lblSeccion.setFont(Colores.FUENTE_BOLD);
        lblSeccion.setForeground(Colores.TEXTO_CLARO);
        lblSeccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNota = new JLabel("Selecciona uno o más alumnos a cargo de este tutor.");
        lblNota.setFont(Colores.FUENTE_PEQUEÑA);
        lblNota.setForeground(Colores.TEXTO_GRIS);
        lblNota.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane scrollAlumnos = construirListaAlumnos();

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
        card.add(Box.createVerticalStrut(20));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));
        card.add(lblSeccion);
        card.add(Box.createVerticalStrut(4));
        card.add(lblNota);
        card.add(Box.createVerticalStrut(10));
        card.add(scrollAlumnos);
        card.add(Box.createVerticalStrut(14));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        card.add(panelBotones);

        return card;
    }

    private JScrollPane construirListaAlumnos() {
        JPanel panelCheckboxes = new JPanel();
        panelCheckboxes.setLayout(new BoxLayout(panelCheckboxes, BoxLayout.Y_AXIS));
        panelCheckboxes.setBackground(Colores.FONDO_OSCURO);
        panelCheckboxes.setBorder(new EmptyBorder(6, 8, 6, 8));

        todosLosAlumnos.clear();
        checkboxesAlumnos.clear();

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

        JScrollPane scroll = new JScrollPane(panelCheckboxes);
        scroll.setBackground(Colores.FONDO_OSCURO);
        scroll.getViewport().setBackground(Colores.FONDO_OSCURO);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        return scroll;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void llenarFormulario() {
        campoNombre.setText(tutor.getNombre());
        campoEmail.setText(tutor.getEmail()       != null ? tutor.getEmail()       : "");
        campoTelefono.setText(tutor.getTelefono() != null ? tutor.getTelefono()    : "");
        // Los checkboxes ya se pre-marcan en construirListaAlumnos()
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

        // Aplicar asignación de alumnos
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

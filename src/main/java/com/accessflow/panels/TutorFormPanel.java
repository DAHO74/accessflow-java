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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TutorFormPanel extends JPanel {

    private static final int MAX_ALUMNOS = 3;
    private static final String[] PARENTESCOS = {"Madre", "Padre", "Otro"};

    private final MainFrame mainFrame;
    private final Tutor     tutor;

    private final TutorDAO  tutorDAO  = new TutorDAO();
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();

    // Datos del tutor
    private JTextField     campoNombre;
    private JTextField     campoEmail;
    private JTextField     campoTelefono;
    private JComboBox<String> cmbParentesco;
    private JTextField     campoOtroParentesco;
    private JPanel         panelOtroParentesco;
    private JLabel         lblError;

    // Lista de alumnos
    private JTextField     campoBuscar;
    private JLabel         lblConteo;
    private JList<Alumno>  listaAlumnos;
    private DefaultListModel<Alumno> modeloLista;

    private final List<Alumno> todosLosAlumnos = new ArrayList<>();
    private final Set<Integer> seleccionados   = new HashSet<>(); // IDs seleccionados

    public TutorFormPanel(MainFrame mainFrame, Tutor tutor) {
        this.mainFrame = mainFrame;
        this.tutor     = tutor;

        setLayout(new BorderLayout(0, 0));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);

        cargarAlumnos();

        if (tutor != null) llenarFormulario();
        actualizarConteo();
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ — Header
    // ═══════════════════════════════════════════════

    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colores.FONDO_OSCURO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titulo = new JLabel(tutor == null ? "Nuevo tutor" : "Editar tutor");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);

        JButton btnVolver = Componentes.botonSecundario("← Volver");
        btnVolver.addActionListener(e -> mainFrame.irATutores());

        header.add(titulo,    BorderLayout.WEST);
        header.add(btnVolver, BorderLayout.EAST);
        return header;
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ — Centro (dos columnas)
    // ═══════════════════════════════════════════════

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
        panel.setPreferredSize(new Dimension(420, 0));

        campoNombre   = new JTextField();
        campoEmail    = new JTextField();
        campoTelefono = new JTextField();
        Componentes.estilizarCampo(campoNombre);
        Componentes.estilizarCampo(campoEmail);
        Componentes.estilizarCampo(campoTelefono);
        aplicarFiltroTelefono(campoTelefono);

        // ── Parentesco ────────────────────────────────
        cmbParentesco = new JComboBox<>(PARENTESCOS);
        estilizarCombo(cmbParentesco);

        campoOtroParentesco = new JTextField();
        Componentes.estilizarCampo(campoOtroParentesco);

        panelOtroParentesco = new JPanel(new BorderLayout(0, 4));
        panelOtroParentesco.setBackground(Colores.FONDO_PANEL);
        panelOtroParentesco.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelOtroParentesco.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JLabel lblOtro = new JLabel("Especificar parentesco *");
        lblOtro.setFont(Colores.FUENTE_PEQUEÑA);
        lblOtro.setForeground(Colores.TEXTO_GRIS);
        panelOtroParentesco.add(lblOtro,               BorderLayout.NORTH);
        panelOtroParentesco.add(campoOtroParentesco,   BorderLayout.CENTER);
        panelOtroParentesco.setVisible(false);

        cmbParentesco.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean esOtro = "Otro".equals(e.getItem());
                panelOtroParentesco.setVisible(esOtro);
                panel.invalidate();
                panel.validate();
                panel.repaint();
            }
        });

        // ── Error y botones ───────────────────────────
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
        panel.add(Box.createVerticalStrut(14));
        panel.add(crearEtiqueta("Correo electrónico"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(campoEmail);
        panel.add(Box.createVerticalStrut(14));
        panel.add(crearEtiqueta("Teléfono  (10 dígitos, se permiten guiones)"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(campoTelefono);
        panel.add(Box.createVerticalStrut(14));
        panel.add(crearEtiqueta("Parentesco con el/los alumno(s) *"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(cmbParentesco);
        panel.add(Box.createVerticalStrut(10));
        panel.add(panelOtroParentesco);
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
        JPanel cabecera = new JPanel(new BorderLayout(0, 8));
        cabecera.setBackground(Colores.FONDO_PANEL);

        JLabel lblSeccion = new JLabel("Alumnos asignados  (máx. " + MAX_ALUMNOS + ")");
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

        campoBuscar = new JTextField();
        campoBuscar.setBackground(Colores.FONDO_OSCURO);
        campoBuscar.setForeground(Colores.TEXTO_CLARO);
        campoBuscar.setCaretColor(Colores.TEXTO_CLARO);
        campoBuscar.setFont(Colores.FUENTE_NORMAL);
        campoBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
        campoBuscar.setToolTipText("Buscar por nombre, grupo o grado...");
        campoBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrar(); }
            public void removeUpdate(DocumentEvent e)  { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        cabecera.add(filaTitulo,  BorderLayout.NORTH);
        cabecera.add(campoBuscar, BorderLayout.SOUTH);

        // ── JList virtualizada ────────────────────────
        modeloLista = new DefaultListModel<>();
        listaAlumnos = new JList<>(modeloLista);
        listaAlumnos.setBackground(Colores.FONDO_OSCURO);
        listaAlumnos.setForeground(Colores.TEXTO_CLARO);
        listaAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaAlumnos.setCellRenderer(new AlumnoCellRenderer());
        listaAlumnos.setFixedCellHeight(48);

        listaAlumnos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = listaAlumnos.locationToIndex(e.getPoint());
                if (idx < 0) return;
                Alumno a = modeloLista.getElementAt(idx);
                toggleSeleccion(a);
                listaAlumnos.repaint();
                actualizarConteo();
            }
        });

        JScrollPane scroll = new JScrollPane(listaAlumnos);
        scroll.setBackground(Colores.FONDO_OSCURO);
        scroll.getViewport().setBackground(Colores.FONDO_OSCURO);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        return panel;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA — Carga y filtrado
    // ═══════════════════════════════════════════════

    private void cargarAlumnos() {
        todosLosAlumnos.clear();
        todosLosAlumnos.addAll(alumnoDAO.listarTodos());

        // Pre-seleccionar los ya asignados a este tutor
        if (tutor != null) {
            for (Alumno a : todosLosAlumnos) {
                if (a.getTutorId() == tutor.getId()) {
                    seleccionados.add(a.getId());
                }
            }
        }
        filtrar();
    }

    private void filtrar() {
        String texto = campoBuscar == null ? "" : campoBuscar.getText().trim().toLowerCase();
        modeloLista.clear();
        for (Alumno a : todosLosAlumnos) {
            if (texto.isEmpty() || coincide(a, texto)) {
                modeloLista.addElement(a);
            }
        }
        actualizarConteo();
    }

    private boolean coincide(Alumno a, String texto) {
        if (a.getNombre()     != null && a.getNombre().toLowerCase().contains(texto))      return true;
        if (a.getGrupoNombre()!= null && a.getGrupoNombre().toLowerCase().contains(texto)) return true;
        if (a.getGrupoGrado() != null && a.getGrupoGrado().toLowerCase().contains(texto))  return true;
        if (a.getRfidUid()    != null && a.getRfidUid().toLowerCase().contains(texto))     return true;
        return false;
    }

    private void toggleSeleccion(Alumno a) {
        if (seleccionados.contains(a.getId())) {
            seleccionados.remove(a.getId());
            lblError.setText(" ");
        } else {
            if (seleccionados.size() >= MAX_ALUMNOS) {
                lblError.setText("Máximo " + MAX_ALUMNOS + " alumnos por tutor.");
                lblError.setForeground(Colores.ROJO);
            } else {
                seleccionados.add(a.getId());
                lblError.setText(" ");
            }
        }
    }

    private void actualizarConteo() {
        int total    = todosLosAlumnos.size();
        int visible  = modeloLista.getSize();
        int sel      = seleccionados.size();
        String txt   = sel + "/" + MAX_ALUMNOS + " seleccionado(s)";
        if (visible < total) txt += "  ·  " + visible + " visible(s) de " + total;
        if (lblConteo != null) {
            lblConteo.setText(txt);
            lblConteo.setForeground(sel == MAX_ALUMNOS ? Colores.AMARILLO : Colores.TEXTO_GRIS);
        }
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA — Formulario
    // ═══════════════════════════════════════════════

    private void llenarFormulario() {
        campoNombre.setText(tutor.getNombre());
        campoEmail.setText(tutor.getEmail()       != null ? tutor.getEmail()       : "");
        campoTelefono.setText(tutor.getTelefono() != null ? tutor.getTelefono()    : "");

        String p = tutor.getParentesco();
        if (p != null) {
            boolean esPredefinido = false;
            for (String op : PARENTESCOS) {
                if (op.equals(p)) { cmbParentesco.setSelectedItem(op); esPredefinido = true; break; }
            }
            if (!esPredefinido) {
                cmbParentesco.setSelectedItem("Otro");
                campoOtroParentesco.setText(p);
                panelOtroParentesco.setVisible(true);
            }
        }
    }

    private void guardar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) { mostrarError("El nombre es obligatorio."); return; }

        String telefono = campoTelefono.getText().trim();
        if (!telefono.isEmpty()) {
            String soloDigitos = telefono.replaceAll("-", "");
            if (!soloDigitos.matches("\\d{10}")) {
                mostrarError("El teléfono debe tener exactamente 10 dígitos (se permiten guiones).");
                return;
            }
        }

        String parentesco = (String) cmbParentesco.getSelectedItem();
        if ("Otro".equals(parentesco)) {
            parentesco = campoOtroParentesco.getText().trim();
            if (parentesco.isEmpty()) { mostrarError("Especifica el parentesco."); return; }
        }

        String email = campoEmail.getText().trim();
        if (email.isEmpty())    email    = null;
        if (telefono.isEmpty()) telefono = null;

        int tutorId;
        boolean exito;

        if (tutor == null) {
            Tutor nuevo = new Tutor(nombre, email, telefono);
            nuevo.setParentesco(parentesco);
            tutorId = tutorDAO.insertarConId(nuevo);
            exito   = tutorId > 0;
        } else {
            tutor.setNombre(nombre);
            tutor.setEmail(email);
            tutor.setTelefono(telefono);
            tutor.setParentesco(parentesco);
            exito   = tutorDAO.actualizar(tutor);
            tutorId = tutor.getId();
        }

        if (!exito) { mostrarError("Error al guardar el tutor. Intenta de nuevo."); return; }

        alumnoDAO.asignarAlumnos(tutorId, new ArrayList<>(seleccionados));
        mainFrame.irATutores();
    }

    private void mostrarError(String msg) {
        lblError.setText(msg);
        lblError.setForeground(Colores.ROJO);
    }

    // ═══════════════════════════════════════════════
    //  RENDERER — celda de la lista
    // ═══════════════════════════════════════════════

    private class AlumnoCellRenderer implements ListCellRenderer<Alumno> {

        private final JPanel    celda   = new JPanel(new BorderLayout(12, 0));
        private final JCheckBox check   = new JCheckBox();
        private final JLabel    lblNom  = new JLabel();
        private final JLabel    lblInfo = new JLabel();

        AlumnoCellRenderer() {
            JPanel textos = new JPanel();
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            textos.setOpaque(false);
            textos.add(lblNom);
            textos.add(lblInfo);

            check.setOpaque(false);
            check.setFocusPainted(false);

            celda.add(check,  BorderLayout.WEST);
            celda.add(textos, BorderLayout.CENTER);
            celda.setBorder(new EmptyBorder(6, 10, 6, 10));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Alumno> list,
                Alumno a, int index, boolean isSelected, boolean cellHasFocus) {

            boolean marcado = seleccionados.contains(a.getId());
            check.setSelected(marcado);

            lblNom.setText(a.getNombre());
            lblNom.setFont(Colores.FUENTE_BOLD);

            String rfid  = (a.getRfidUid()    != null && !a.getRfidUid().isEmpty())    ? a.getRfidUid()    : "Sin RFID";
            String grado = (a.getGrupoGrado() != null && !a.getGrupoGrado().isEmpty()) ? a.getGrupoGrado() : "—";
            String grupo = (a.getGrupoNombre()!= null && !a.getGrupoNombre().isEmpty())? a.getGrupoNombre(): "Sin grupo";
            lblInfo.setText("RFID: " + rfid + "   ·   " + grado + "   ·   " + grupo);
            lblInfo.setFont(Colores.FUENTE_PEQUEÑA);

            Color fondo = isSelected ? new Color(0x1E, 0x3A, 0x5A)
                        : (index % 2 == 0 ? Colores.FONDO_OSCURO : new Color(0x16, 0x20, 0x2C));

            celda.setBackground(fondo);
            check.setBackground(fondo);
            lblNom.setForeground(marcado ? Colores.AZUL_PRIMARIO : Colores.TEXTO_CLARO);
            lblInfo.setForeground(Colores.TEXTO_GRIS);

            return celda;
        }
    }

    // ── Auxiliares de estilo ─────────────────────────

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void aplicarFiltroTelefono(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text == null) return;
                String limpio    = text.replaceAll("[^0-9\\-]", "");
                String resultado = resultante(fb, offset, 0, limpio);
                if (valido(resultado))
                    super.insertString(fb, offset, limpio, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String limpio    = (text == null) ? "" : text.replaceAll("[^0-9\\-]", "");
                String resultado = resultante(fb, offset, length, limpio);
                if (valido(resultado))
                    super.replace(fb, offset, length, limpio, attrs);
            }
            private String resultante(FilterBypass fb, int offset, int length, String nuevo)
                    throws BadLocationException {
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                return actual.substring(0, offset) + nuevo + actual.substring(offset + length);
            }
            private boolean valido(String s) {
                long digitos = s.chars().filter(Character::isDigit).count();
                return digitos <= 10 && s.length() <= 12;
            }
        });
    }

    private void estilizarCombo(JComboBox<String> combo) {
        combo.setBackground(Colores.FONDO_OSCURO);
        combo.setForeground(Colores.TEXTO_CLARO);
        combo.setFont(Colores.FUENTE_NORMAL);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(0, 4, 0, 4)
        ));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? Colores.AZUL_PRIMARIO : Colores.FONDO_OSCURO);
                setForeground(Colores.TEXTO_CLARO);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }
}

package com.accessflow.panels;

import com.accessflow.dao.AlumnoDAO;
import com.accessflow.dao.GrupoDAO;
import com.accessflow.dao.MensajeDAO;
import com.accessflow.dao.TutorDAO;
import com.accessflow.model.Alumno;
import com.accessflow.model.Grupo;
import com.accessflow.model.Mensaje;
import com.accessflow.model.Tutor;
import com.accessflow.service.EmailService;
import com.accessflow.service.SessionManager;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MessagingPanel extends JPanel {

    private final MainFrame  mainFrame;
    private final TutorDAO   tutorDAO   = new TutorDAO();
    private final AlumnoDAO  alumnoDAO  = new AlumnoDAO();
    private final GrupoDAO   grupoDAO   = new GrupoDAO();
    private final MensajeDAO mensajeDAO = new MensajeDAO();

    // ── Pestaña Redactar ─────────────────────────────
    private JTextField               campoBuscarTutor;
    private JList<Object>            listaTutores;
    private DefaultListModel<Object> modeloTutores;
    private JLabel                   lblTutorSel;
    private JTextField               campoAsunto;
    private JTextArea                areaCuerpo;
    private JLabel                   lblEstado;
    private JLabel                   lblAdjunto;
    private File                     archivoAdjunto;
    private Tutor                    tutorResolto;

    // ── Pestaña Historial ────────────────────────────
    private JTextField        campoBuscarHistorial;
    private JTable            tabla;
    private DefaultTableModel modeloTabla;
    private List<Mensaje>     todosMensajes    = new ArrayList<>();
    private List<Mensaje>     mensajesVisibles = new ArrayList<>();

    // ── Pestaña Grupal ───────────────────────────────
    private JComboBox<Grupo>        cmbGrupo;
    private JList<Alumno>           listaAlumnosGrupal;
    private DefaultListModel<Alumno> modeloAlumnosGrupal;
    private JTextField              campoAsuntoGrupal;
    private JTextArea               areaCuerpoGrupal;
    private JLabel                  lblEstadoGrupal;
    private JTextField              campoBuscarAlumno;
    private File                    archivoAdjuntoGrupal;
    private JLabel                  lblAdjuntoGrupal;

    // ── Tutores ──────────────────────────────────────
    private final List<Tutor> todosTutores = new ArrayList<>();

    public MessagingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 14));
        setBackground(Colores.FONDO_OSCURO);

        JLabel titulo = new JLabel("Mensajería");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        titulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        estilizarTabs(tabs);
        tabs.addTab("  Redactar  ", construirTabRedactar());
        tabs.addTab("  Grupal  ",   construirTabGrupal());
        tabs.addTab("  Historial  ", construirTabHistorial());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 2) cargarHistorial();
        });
        add(tabs, BorderLayout.CENTER);

        cargarTutores();
    }

    // ═══════════════════════════════════════════════
    //  TAB: REDACTAR
    // ═══════════════════════════════════════════════

    private JPanel construirTabRedactar() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(Colores.FONDO_OSCURO);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));
        panel.add(construirPanelBusquedaTutor(), BorderLayout.WEST);
        panel.add(construirPanelMensaje(),        BorderLayout.CENTER);
        return panel;
    }

    // ── Búsqueda de destinatario (tutor por nombre/correo o por alumno RFID/nombre) ──

    private JPanel construirPanelBusquedaTutor() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Colores.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(16, 16, 16, 16)
        ));
        panel.setPreferredSize(new Dimension(320, 0));

        JLabel lblTit = new JLabel("Tutor destinatario");
        lblTit.setFont(Colores.FUENTE_BOLD);
        lblTit.setForeground(Colores.TEXTO_CLARO);

        lblTutorSel = new JLabel("Ninguno seleccionado");
        lblTutorSel.setFont(Colores.FUENTE_PEQUEÑA);
        lblTutorSel.setForeground(Colores.TEXTO_GRIS);

        JLabel lblHint = new JLabel("Buscar por tutor o por nombre/RFID del alumno:");
        lblHint.setFont(Colores.FUENTE_PEQUEÑA);
        lblHint.setForeground(Colores.TEXTO_GRIS);

        campoBuscarTutor = crearCampoBusqueda("Nombre, correo, o RFID del alumno...");
        campoBuscarTutor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrarTutores(); }
            public void removeUpdate(DocumentEvent e)  { filtrarTutores(); }
            public void changedUpdate(DocumentEvent e) { filtrarTutores(); }
        });

        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.setBackground(Colores.FONDO_PANEL);
        cabecera.add(lblTit);
        cabecera.add(Box.createVerticalStrut(4));
        cabecera.add(lblTutorSel);
        cabecera.add(Box.createVerticalStrut(10));
        cabecera.add(lblHint);
        cabecera.add(Box.createVerticalStrut(4));
        cabecera.add(campoBuscarTutor);

        modeloTutores = new DefaultListModel<>();
        listaTutores  = new JList<>(modeloTutores);
        listaTutores.setBackground(Colores.FONDO_OSCURO);
        listaTutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTutores.setFixedCellHeight(68);
        listaTutores.setCellRenderer(new ResultadoBusquedaRenderer());
        listaTutores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarTutorSel();
        });

        JScrollPane scroll = new JScrollPane(listaTutores);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scroll.getViewport().setBackground(Colores.FONDO_OSCURO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        return panel;
    }

    // ── Redactor de mensaje ───────────────────────────

    private JPanel construirPanelMensaje() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Colores.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(16, 16, 16, 16)
        ));

        // Asunto
        JPanel panelAsunto = new JPanel(new BorderLayout(0, 6));
        panelAsunto.setBackground(Colores.FONDO_PANEL);
        JLabel lblAsunto = new JLabel("Asunto");
        lblAsunto.setFont(Colores.FUENTE_PEQUEÑA);
        lblAsunto.setForeground(Colores.TEXTO_GRIS);
        campoAsunto = new JTextField();
        Componentes.estilizarCampo(campoAsunto);
        panelAsunto.add(lblAsunto,   BorderLayout.NORTH);
        panelAsunto.add(campoAsunto, BorderLayout.CENTER);

        // Cuerpo
        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 6));
        panelCuerpo.setBackground(Colores.FONDO_PANEL);
        JLabel lblCuerpo = new JLabel("Mensaje");
        lblCuerpo.setFont(Colores.FUENTE_PEQUEÑA);
        lblCuerpo.setForeground(Colores.TEXTO_GRIS);

        areaCuerpo = crearAreaTexto();
        JScrollPane scrollCuerpo = new JScrollPane(areaCuerpo);
        scrollCuerpo.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scrollCuerpo.getViewport().setBackground(Colores.FONDO_OSCURO);
        scrollCuerpo.getVerticalScrollBar().setUnitIncrement(12);
        panelCuerpo.add(lblCuerpo,    BorderLayout.NORTH);
        panelCuerpo.add(scrollCuerpo, BorderLayout.CENTER);

        // Adjunto
        JPanel panelAdjunto = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelAdjunto.setBackground(Colores.FONDO_PANEL);
        JButton btnAdjuntar = Componentes.botonSecundario("Adjuntar archivo");
        btnAdjuntar.addActionListener(e -> seleccionarAdjunto());
        lblAdjunto = new JLabel("Sin adjunto");
        lblAdjunto.setFont(Colores.FUENTE_PEQUEÑA);
        lblAdjunto.setForeground(Colores.TEXTO_GRIS);
        lblAdjunto.setBorder(new EmptyBorder(0, 10, 0, 0));
        panelAdjunto.add(btnAdjuntar);
        panelAdjunto.add(lblAdjunto);

        // Pie
        lblEstado = new JLabel(" ");
        lblEstado.setFont(Colores.FUENTE_PEQUEÑA);
        lblEstado.setForeground(Colores.TEXTO_GRIS);
        JButton btnEnviar = Componentes.botonPrimario("Enviar correo");
        btnEnviar.addActionListener(e -> enviar());

        JPanel pie = new JPanel(new BorderLayout(8, 0));
        pie.setBackground(Colores.FONDO_PANEL);
        pie.add(lblEstado, BorderLayout.CENTER);
        pie.add(btnEnviar, BorderLayout.EAST);

        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setBackground(Colores.FONDO_PANEL);
        sur.add(panelAdjunto, BorderLayout.NORTH);
        sur.add(pie,          BorderLayout.SOUTH);

        panel.add(panelAsunto, BorderLayout.NORTH);
        panel.add(panelCuerpo, BorderLayout.CENTER);
        panel.add(sur,         BorderLayout.SOUTH);
        return panel;
    }

    // ═══════════════════════════════════════════════
    //  TAB: GRUPAL
    // ═══════════════════════════════════════════════

    private JPanel construirTabGrupal() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(Colores.FONDO_OSCURO);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));
        panel.add(construirPanelSelectorGrupo(), BorderLayout.WEST);
        panel.add(construirPanelMensajeGrupal(), BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirPanelSelectorGrupo() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Colores.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(16, 16, 16, 16)
        ));
        panel.setPreferredSize(new Dimension(320, 0));

        JLabel lblTit = new JLabel("Destinatarios");
        lblTit.setFont(Colores.FUENTE_BOLD);
        lblTit.setForeground(Colores.TEXTO_CLARO);

        JLabel lblHint = new JLabel("Selecciona grupo o busca alumno por nombre/RFID:");
        lblHint.setFont(Colores.FUENTE_PEQUEÑA);
        lblHint.setForeground(Colores.TEXTO_GRIS);

        // Selector de grupo
        cmbGrupo = new JComboBox<>();
        cmbGrupo.addItem(null);
        for (Grupo g : grupoDAO.listarTodos()) cmbGrupo.addItem(g);
        cmbGrupo.setBackground(Colores.FONDO_OSCURO);
        cmbGrupo.setForeground(Colores.TEXTO_CLARO);
        cmbGrupo.setFont(Colores.FUENTE_NORMAL);
        cmbGrupo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cmbGrupo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, value, idx, sel, focus);
                setText(value == null ? "— Seleccionar grupo —" : value.toString());
                setBackground(sel ? Colores.AZUL_PRIMARIO : Colores.FONDO_OSCURO);
                setForeground(Colores.TEXTO_CLARO);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        cmbGrupo.addActionListener(e -> cargarAlumnosDelGrupo());

        // Búsqueda individual de alumno
        campoBuscarAlumno = crearCampoBusqueda("Nombre o RFID del alumno...");
        campoBuscarAlumno.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campoBuscarAlumno.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { buscarAlumnoIndividual(); }
            public void removeUpdate(DocumentEvent e)  { buscarAlumnoIndividual(); }
            public void changedUpdate(DocumentEvent e) { buscarAlumnoIndividual(); }
        });

        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.setBackground(Colores.FONDO_PANEL);
        cabecera.add(lblTit);
        cabecera.add(Box.createVerticalStrut(4));
        cabecera.add(lblHint);
        cabecera.add(Box.createVerticalStrut(8));
        cabecera.add(cmbGrupo);
        cabecera.add(Box.createVerticalStrut(8));
        cabecera.add(campoBuscarAlumno);

        modeloAlumnosGrupal = new DefaultListModel<>();
        listaAlumnosGrupal  = new JList<>(modeloAlumnosGrupal);
        listaAlumnosGrupal.setBackground(Colores.FONDO_OSCURO);
        listaAlumnosGrupal.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listaAlumnosGrupal.setFixedCellHeight(64);
        listaAlumnosGrupal.setCellRenderer(new AlumnoCellRenderer());

        JScrollPane scroll = new JScrollPane(listaAlumnosGrupal);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scroll.getViewport().setBackground(Colores.FONDO_OSCURO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JLabel lblInfo = new JLabel("Solo alumnos con tutor y correo registrado recibirán el mensaje.");
        lblInfo.setFont(Colores.FUENTE_PEQUEÑA);
        lblInfo.setForeground(new Color(0x5A, 0x74, 0x90));

        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        panel.add(lblInfo,  BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelMensajeGrupal() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Colores.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel panelAsunto = new JPanel(new BorderLayout(0, 6));
        panelAsunto.setBackground(Colores.FONDO_PANEL);
        JLabel lblAsunto = new JLabel("Asunto");
        lblAsunto.setFont(Colores.FUENTE_PEQUEÑA);
        lblAsunto.setForeground(Colores.TEXTO_GRIS);
        campoAsuntoGrupal = new JTextField();
        Componentes.estilizarCampo(campoAsuntoGrupal);
        panelAsunto.add(lblAsunto,          BorderLayout.NORTH);
        panelAsunto.add(campoAsuntoGrupal,  BorderLayout.CENTER);

        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 6));
        panelCuerpo.setBackground(Colores.FONDO_PANEL);
        JLabel lblCuerpo = new JLabel("Mensaje");
        lblCuerpo.setFont(Colores.FUENTE_PEQUEÑA);
        lblCuerpo.setForeground(Colores.TEXTO_GRIS);
        areaCuerpoGrupal = crearAreaTexto();
        JScrollPane scrollCuerpo = new JScrollPane(areaCuerpoGrupal);
        scrollCuerpo.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scrollCuerpo.getViewport().setBackground(Colores.FONDO_OSCURO);
        scrollCuerpo.getVerticalScrollBar().setUnitIncrement(12);
        panelCuerpo.add(lblCuerpo,    BorderLayout.NORTH);
        panelCuerpo.add(scrollCuerpo, BorderLayout.CENTER);

        // Adjunto
        JPanel panelAdjuntoGrupal = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelAdjuntoGrupal.setBackground(Colores.FONDO_PANEL);
        JButton btnAdjuntarGrupal = Componentes.botonSecundario("Adjuntar archivo");
        btnAdjuntarGrupal.addActionListener(e -> seleccionarAdjuntoGrupal());
        lblAdjuntoGrupal = new JLabel("Sin adjunto");
        lblAdjuntoGrupal.setFont(Colores.FUENTE_PEQUEÑA);
        lblAdjuntoGrupal.setForeground(Colores.TEXTO_GRIS);
        lblAdjuntoGrupal.setBorder(new EmptyBorder(0, 10, 0, 0));
        panelAdjuntoGrupal.add(btnAdjuntarGrupal);
        panelAdjuntoGrupal.add(lblAdjuntoGrupal);

        lblEstadoGrupal = new JLabel(" ");
        lblEstadoGrupal.setFont(Colores.FUENTE_PEQUEÑA);
        lblEstadoGrupal.setForeground(Colores.TEXTO_GRIS);
        JButton btnEnviarGrupal = Componentes.botonPrimario("Enviar a todos");
        btnEnviarGrupal.addActionListener(e -> enviarGrupal());

        JPanel pie = new JPanel(new BorderLayout(8, 0));
        pie.setBackground(Colores.FONDO_PANEL);
        pie.add(lblEstadoGrupal, BorderLayout.CENTER);
        pie.add(btnEnviarGrupal, BorderLayout.EAST);

        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setBackground(Colores.FONDO_PANEL);
        sur.add(panelAdjuntoGrupal, BorderLayout.NORTH);
        sur.add(pie,                BorderLayout.SOUTH);

        panel.add(panelAsunto, BorderLayout.NORTH);
        panel.add(panelCuerpo, BorderLayout.CENTER);
        panel.add(sur,         BorderLayout.SOUTH);
        return panel;
    }

    // ═══════════════════════════════════════════════
    //  TAB: HISTORIAL
    // ═══════════════════════════════════════════════

    private JPanel construirTabHistorial() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Colores.FONDO_OSCURO);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        campoBuscarHistorial = crearCampoBusqueda("Filtrar por tutor o asunto...");
        campoBuscarHistorial.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrarHistorial(); }
            public void removeUpdate(DocumentEvent e)  { filtrarHistorial(); }
            public void changedUpdate(DocumentEvent e) { filtrarHistorial(); }
        });

        String[] columnas = {"Tutor", "Asunto", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(400);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(160);

        JButton btnEliminar = Componentes.botonPeligro("Eliminar seleccionado");
        btnEliminar.addActionListener(e -> eliminarMensaje());

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pie.setBackground(Colores.FONDO_OSCURO);
        pie.add(btnEliminar);

        panel.add(campoBuscarHistorial,             BorderLayout.NORTH);
        panel.add(Componentes.crearScrollTabla(tabla), BorderLayout.CENTER);
        panel.add(pie,                              BorderLayout.SOUTH);
        return panel;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA — Redactar
    // ═══════════════════════════════════════════════

    private void cargarTutores() {
        todosTutores.clear();
        todosTutores.addAll(tutorDAO.listarTodos());
        filtrarTutores();
    }

    private void filtrarTutores() {
        String txt = campoBuscarTutor == null ? "" : campoBuscarTutor.getText().trim().toLowerCase();
        modeloTutores.clear();
        tutorResolto = null;

        if (txt.isEmpty()) {
            // Sin búsqueda: muestra todos los tutores directamente
            todosTutores.forEach(modeloTutores::addElement);
            return;
        }

        // Primero: tutores que coincidan por nombre/correo
        for (Tutor t : todosTutores) {
            boolean porTutor = (t.getNombre() != null && t.getNombre().toLowerCase().contains(txt))
                            || (t.getEmail()  != null && t.getEmail().toLowerCase().contains(txt));
            if (porTutor) modeloTutores.addElement(t);
        }

        // Luego: alumnos que coincidan por nombre o RFID (se muestran como alumno, resuelven al tutor)
        for (Alumno a : alumnoDAO.buscarPorNombreORfid(txt)) {
            if (a.getTutorId() > 0) modeloTutores.addElement(a);
        }
    }

    private void actualizarTutorSel() {
        Object sel = listaTutores.getSelectedValue();
        if (sel == null) {
            tutorResolto = null;
            lblTutorSel.setText("Ninguno seleccionado");
            lblTutorSel.setForeground(Colores.TEXTO_GRIS);
            return;
        }
        if (sel instanceof Tutor) {
            tutorResolto = (Tutor) sel;
        } else if (sel instanceof Alumno) {
            tutorResolto = tutorDAO.buscarPorId(((Alumno) sel).getTutorId());
        }
        if (tutorResolto == null) {
            lblTutorSel.setText("Sin tutor asignado");
            lblTutorSel.setForeground(Colores.AMARILLO);
        } else {
            String email = (tutorResolto.getEmail() != null && !tutorResolto.getEmail().isEmpty())
                         ? tutorResolto.getEmail() : "sin correo";
            lblTutorSel.setText("✓ " + tutorResolto.getNombre() + "  —  " + email);
            lblTutorSel.setForeground(Colores.VERDE);
        }
    }

    private void seleccionarAdjunto() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo adjunto");
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            archivoAdjunto = fc.getSelectedFile();
            lblAdjunto.setText(archivoAdjunto.getName());
            lblAdjunto.setForeground(Colores.VERDE);
        }
    }

    private void seleccionarAdjuntoGrupal() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleccionar archivo adjunto");
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            archivoAdjuntoGrupal = fc.getSelectedFile();
            lblAdjuntoGrupal.setText(archivoAdjuntoGrupal.getName());
            lblAdjuntoGrupal.setForeground(Colores.VERDE);
        }
    }

    private void enviar() {
        if (tutorResolto == null) { mostrarEstado(lblEstado, "Selecciona un destinatario.", Colores.AMARILLO); return; }
        Tutor tutor = tutorResolto;
        if (tutor.getEmail() == null || tutor.getEmail().isEmpty()) {
            mostrarEstado(lblEstado, "El tutor \"" + tutor.getNombre() + "\" no tiene correo registrado.", Colores.AMARILLO);
            return;
        }
        String asunto = campoAsunto.getText().trim();
        String cuerpo = areaCuerpo.getText().trim();
        if (asunto.isEmpty()) { mostrarEstado(lblEstado, "El asunto no puede estar vacío.", Colores.AMARILLO); return; }
        if (cuerpo.isEmpty()) { mostrarEstado(lblEstado, "El mensaje no puede estar vacío.", Colores.AMARILLO); return; }

        mostrarEstado(lblEstado, "Enviando...", Colores.TEXTO_GRIS);
        final File adjunto = archivoAdjunto;
        new Thread(() -> {
            String error = EmailService.enviarConAdjunto(tutor.getEmail(), asunto, cuerpo, adjunto);
            SwingUtilities.invokeLater(() -> {
                if (error != null) { mostrarEstado(lblEstado, error, Colores.ROJO); return; }
                mensajeDAO.insertar(new Mensaje(SessionManager.getUsuario().getId(), tutor.getId(), asunto, cuerpo));
                campoAsunto.setText("");
                areaCuerpo.setText("");
                archivoAdjunto = null;
                lblAdjunto.setText("Sin adjunto");
                lblAdjunto.setForeground(Colores.TEXTO_GRIS);
                listaTutores.clearSelection();
                campoBuscarTutor.setText("");
                mostrarEstado(lblEstado, "Correo enviado a " + tutor.getEmail(), Colores.VERDE);
            });
        }).start();
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA — Grupal
    // ═══════════════════════════════════════════════

    private void cargarAlumnosDelGrupo() {
        campoBuscarAlumno.setText("");
        modeloAlumnosGrupal.clear();
        Grupo grupo = (Grupo) cmbGrupo.getSelectedItem();
        if (grupo == null) return;

        for (Alumno a : alumnoDAO.listarPorGrupoId(grupo.getId())) {
            modeloAlumnosGrupal.addElement(a);
        }
        // Seleccionar todos por defecto
        if (modeloAlumnosGrupal.getSize() > 0) {
            listaAlumnosGrupal.setSelectionInterval(0, modeloAlumnosGrupal.getSize() - 1);
        }
    }

    private void buscarAlumnoIndividual() {
        String txt = campoBuscarAlumno.getText().trim();
        if (txt.isEmpty()) {
            cargarAlumnosDelGrupo();
            return;
        }
        cmbGrupo.setSelectedItem(null);
        modeloAlumnosGrupal.clear();
        for (Alumno a : alumnoDAO.buscarPorNombreORfid(txt)) {
            modeloAlumnosGrupal.addElement(a);
        }
        if (modeloAlumnosGrupal.getSize() > 0) {
            listaAlumnosGrupal.setSelectionInterval(0, modeloAlumnosGrupal.getSize() - 1);
        }
    }

    private void enviarGrupal() {
        List<Alumno> seleccionados = listaAlumnosGrupal.getSelectedValuesList();
        if (seleccionados.isEmpty()) {
            mostrarEstado(lblEstadoGrupal, "Selecciona al menos un alumno.", Colores.AMARILLO); return;
        }
        String asunto = campoAsuntoGrupal.getText().trim();
        String cuerpo = areaCuerpoGrupal.getText().trim();
        if (asunto.isEmpty()) { mostrarEstado(lblEstadoGrupal, "El asunto no puede estar vacío.", Colores.AMARILLO); return; }
        if (cuerpo.isEmpty()) { mostrarEstado(lblEstadoGrupal, "El mensaje no puede estar vacío.", Colores.AMARILLO); return; }

        mostrarEstado(lblEstadoGrupal, "Enviando...", Colores.TEXTO_GRIS);
        final File adjunto = archivoAdjuntoGrupal;
        new Thread(() -> {
            int enviados = 0;
            int sinCorreo = 0;
            for (Alumno alumno : seleccionados) {
                if (alumno.getTutorId() <= 0) { sinCorreo++; continue; }
                Tutor tutor = tutorDAO.buscarPorId(alumno.getTutorId());
                if (tutor == null || tutor.getEmail() == null || tutor.getEmail().isEmpty()) {
                    sinCorreo++; continue;
                }
                String cuerpoPersonalizado = cuerpo.replace("{alumno}", alumno.getNombre());
                String err = EmailService.enviarConAdjunto(tutor.getEmail(), asunto, cuerpoPersonalizado, adjunto);
                if (err == null) {
                    mensajeDAO.insertar(new Mensaje(SessionManager.getUsuario().getId(), tutor.getId(), asunto, cuerpoPersonalizado));
                    enviados++;
                }
            }
            final int env = enviados, sin = sinCorreo;
            SwingUtilities.invokeLater(() -> {
                String msg = "Enviado a " + env + " tutor(es).";
                if (sin > 0) msg += " " + sin + " alumno(s) sin correo de tutor.";
                mostrarEstado(lblEstadoGrupal, msg, env > 0 ? Colores.VERDE : Colores.AMARILLO);
                if (env > 0) {
                    campoAsuntoGrupal.setText("");
                    areaCuerpoGrupal.setText("");
                    archivoAdjuntoGrupal = null;
                    lblAdjuntoGrupal.setText("Sin adjunto");
                    lblAdjuntoGrupal.setForeground(Colores.TEXTO_GRIS);
                }
            });
        }).start();
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA — Historial
    // ═══════════════════════════════════════════════

    private void cargarHistorial() {
        todosMensajes = mensajeDAO.listarTodos();
        filtrarHistorial();
    }

    private void filtrarHistorial() {
        String txt = campoBuscarHistorial == null ? "" : campoBuscarHistorial.getText().trim().toLowerCase();
        modeloTabla.setRowCount(0);
        mensajesVisibles.clear();
        for (Mensaje m : todosMensajes) {
            String tutor  = m.getTutorNombre() != null ? m.getTutorNombre() : "-";
            String asunto = m.getAsunto()      != null ? m.getAsunto()      : "";
            if (txt.isEmpty() || tutor.toLowerCase().contains(txt) || asunto.toLowerCase().contains(txt)) {
                modeloTabla.addRow(new Object[]{tutor, asunto, m.getFechaTexto()});
                mensajesVisibles.add(m);
            }
        }
    }

    private void eliminarMensaje() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un mensaje para eliminar.",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Mensaje m = mensajesVisibles.get(fila);
        String preview = m.getAsunto() != null ? m.getAsunto() : "(sin asunto)";
        int op = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el mensaje \"" + preview + "\"?\nEsta acción no se puede deshacer.",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            if (mensajeDAO.eliminar(m.getId())) cargarHistorial();
            else JOptionPane.showMessageDialog(this, "No se pudo eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarEstado(JLabel lbl, String texto, Color color) {
        lbl.setText(texto);
        lbl.setForeground(color);
    }

    // ═══════════════════════════════════════════════
    //  RENDERERS
    // ═══════════════════════════════════════════════

    // Renderer unificado: muestra Tutor directamente o Alumno (con datos de su tutor)
    private class ResultadoBusquedaRenderer implements ListCellRenderer<Object> {
        private final JPanel celda    = new JPanel(new BorderLayout(0, 2));
        private final JLabel lblNom   = new JLabel();
        private final JLabel lblSub1  = new JLabel();
        private final JLabel lblSub2  = new JLabel();
        private final JLabel lblBadge = new JLabel();

        ResultadoBusquedaRenderer() {
            JPanel textos = new JPanel();
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            textos.setOpaque(false);
            textos.add(lblNom);
            textos.add(lblSub1);
            textos.add(lblSub2);
            celda.add(textos,   BorderLayout.CENTER);
            celda.add(lblBadge, BorderLayout.EAST);
            celda.setBorder(new EmptyBorder(6, 12, 6, 12));
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list,
                Object value, int index, boolean isSelected, boolean cellHasFocus) {

            Color fondo = isSelected ? new Color(0x1E, 0x3A, 0x5A)
                : (index % 2 == 0 ? Colores.FONDO_OSCURO : new Color(0x16, 0x20, 0x2C));
            celda.setBackground(fondo);

            if (value instanceof Tutor) {
                Tutor t = (Tutor) value;
                lblNom.setText(t.getNombre());
                String email  = (t.getEmail()      != null && !t.getEmail().isEmpty())      ? t.getEmail()      : "Sin correo";
                String parent = (t.getParentesco() != null && !t.getParentesco().isEmpty()) ? t.getParentesco() : "";
                lblSub1.setText(email);
                lblSub2.setText(parent);
                lblBadge.setText("Tutor");
                lblBadge.setForeground(new Color(0x60, 0x90, 0xC0));
            } else if (value instanceof Alumno) {
                Alumno a = (Alumno) value;
                lblNom.setText(a.getNombre());
                String rfid   = (a.getRfidUid()    != null && !a.getRfidUid().isEmpty())    ? a.getRfidUid()    : "Sin RFID";
                String grupo  = (a.getGrupoNombre() != null && !a.getGrupoNombre().isEmpty()) ? a.getGrupoGrado() + " " + a.getGrupoNombre() : "Sin grupo";
                String tutor  = (a.getTutorNombre() != null && !a.getTutorNombre().isEmpty())
                              ? "→ Tutor: " + a.getTutorNombre() : "Sin tutor";
                lblSub1.setText("RFID: " + rfid + "   |   " + grupo);
                lblSub2.setText(tutor);
                lblBadge.setText("Alumno");
                lblBadge.setForeground(new Color(0x60, 0xC0, 0x90));
            }

            lblNom.setFont(Colores.FUENTE_BOLD);
            lblSub1.setFont(Colores.FUENTE_PEQUEÑA);
            lblSub2.setFont(Colores.FUENTE_PEQUEÑA);
            lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblNom.setForeground(isSelected ? Colores.AZUL_PRIMARIO : Colores.TEXTO_CLARO);
            lblSub1.setForeground(Colores.TEXTO_GRIS);
            lblSub2.setForeground(new Color(0x5A, 0x74, 0x90));
            return celda;
        }
    }

    private class AlumnoCellRenderer implements ListCellRenderer<Alumno> {
        private final JPanel celda    = new JPanel(new BorderLayout(0, 2));
        private final JLabel lblGrupo = new JLabel();
        private final JLabel lblNom   = new JLabel();
        private final JLabel lblTutor = new JLabel();

        AlumnoCellRenderer() {
            JPanel textos = new JPanel();
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            textos.setOpaque(false);
            textos.add(lblGrupo);
            textos.add(lblNom);
            textos.add(lblTutor);
            celda.add(textos, BorderLayout.CENTER);
            celda.setBorder(new EmptyBorder(6, 12, 6, 12));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Alumno> list,
                Alumno a, int index, boolean isSelected, boolean cellHasFocus) {
            String grupo = (a.getGrupoGrado() != null && !a.getGrupoGrado().isEmpty())
                ? a.getGrupoGrado() + " " + (a.getGrupoNombre() != null ? a.getGrupoNombre() : "")
                : (a.getGrupoNombre() != null ? a.getGrupoNombre() : "Sin grupo");
            lblGrupo.setText(grupo.trim());
            lblGrupo.setFont(Colores.FUENTE_PEQUEÑA);

            lblNom.setText(a.getNombre());
            lblNom.setFont(Colores.FUENTE_BOLD);

            String tutor = (a.getTutorNombre() != null && !a.getTutorNombre().isEmpty())
                ? "Tutor: " + a.getTutorNombre() : "Sin tutor asignado";
            lblTutor.setText(tutor);
            lblTutor.setFont(Colores.FUENTE_PEQUEÑA);

            Color fondo = isSelected ? new Color(0x1E, 0x3A, 0x5A)
                : (index % 2 == 0 ? Colores.FONDO_OSCURO : new Color(0x16, 0x20, 0x2C));
            celda.setBackground(fondo);
            lblGrupo.setForeground(new Color(0x5A, 0x74, 0x90));
            lblNom.setForeground(isSelected ? Colores.AZUL_PRIMARIO : Colores.TEXTO_CLARO);
            lblTutor.setForeground(Colores.TEXTO_GRIS);
            return celda;
        }
    }

    // ═══════════════════════════════════════════════
    //  AUXILIARES
    // ═══════════════════════════════════════════════

    private JTextField crearCampoBusqueda(String tooltip) {
        JTextField campo = new JTextField();
        campo.setBackground(Colores.FONDO_OSCURO);
        campo.setForeground(Colores.TEXTO_CLARO);
        campo.setCaretColor(Colores.TEXTO_CLARO);
        campo.setFont(Colores.FUENTE_NORMAL);
        campo.setToolTipText(tooltip);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return campo;
    }

    private JTextArea crearAreaTexto() {
        JTextArea area = new JTextArea();
        area.setBackground(Colores.FONDO_OSCURO);
        area.setForeground(Colores.TEXTO_CLARO);
        area.setCaretColor(Colores.TEXTO_CLARO);
        area.setFont(Colores.FUENTE_NORMAL);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
        return area;
    }

    private void estilizarTabs(JTabbedPane tabs) {
        tabs.setBackground(Colores.FONDO_OSCURO);
        tabs.setForeground(Colores.TEXTO_CLARO);
        tabs.setFont(Colores.FUENTE_BOLD);
        tabs.setBorder(null);
        tabs.setOpaque(true);
        UIManager.put("TabbedPane.selected",             Colores.FONDO_PANEL);
        UIManager.put("TabbedPane.background",           Colores.FONDO_OSCURO);
        UIManager.put("TabbedPane.foreground",           Colores.TEXTO_CLARO);
        UIManager.put("TabbedPane.unselectedBackground", Colores.SIDEBAR_FONDO);
        UIManager.put("TabbedPane.contentAreaColor",     Colores.FONDO_OSCURO);
        UIManager.put("TabbedPane.light",                Colores.BORDE);
        UIManager.put("TabbedPane.highlight",            Colores.BORDE);
        UIManager.put("TabbedPane.shadow",               Colores.BORDE);
        UIManager.put("TabbedPane.darkShadow",           Colores.BORDE);
        UIManager.put("TabbedPane.focus",                Colores.AZUL_PRIMARIO);
        tabs.updateUI();
    }
}

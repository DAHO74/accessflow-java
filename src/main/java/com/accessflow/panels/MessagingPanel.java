package com.accessflow.panels;

import com.accessflow.dao.MensajeDAO;
import com.accessflow.dao.TutorDAO;
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
import java.util.ArrayList;
import java.util.List;

public class MessagingPanel extends JPanel {

    private final MainFrame  mainFrame;
    private final TutorDAO   tutorDAO   = new TutorDAO();
    private final MensajeDAO mensajeDAO = new MensajeDAO();

    // ── Pestaña Redactar ─────────────────────────────
    private JTextField         campoBuscarTutor;
    private JList<Tutor>       listaTutores;
    private DefaultListModel<Tutor> modeloTutores;
    private JLabel             lblTutorSel;
    private JTextField         campoAsunto;
    private JTextArea          areaCuerpo;
    private JLabel             lblEstado;

    // ── Pestaña Historial ────────────────────────────
    private JTextField         campoBuscarHistorial;
    private JTable             tabla;
    private DefaultTableModel  modeloTabla;
    private List<Mensaje>      todosMensajes    = new ArrayList<>();
    private List<Mensaje>      mensajesVisibles = new ArrayList<>();

    // ── Tutores ──────────────────────────────────────
    private final List<Tutor>  todosTutores  = new ArrayList<>();

    public MessagingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 14));
        setBackground(Colores.FONDO_OSCURO);

        // Título
        JLabel titulo = new JLabel("Mensajería");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        titulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        estilizarTabs(tabs);
        tabs.addTab("  Redactar  ", construirTabRedactar());
        tabs.addTab("  Historial  ", construirTabHistorial());
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 1) cargarHistorial();
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

        panel.add(construirPanelTutor(),  BorderLayout.WEST);
        panel.add(construirPanelMensaje(), BorderLayout.CENTER);
        return panel;
    }

    // ── Columna izquierda: búsqueda de tutor ─────────

    private JPanel construirPanelTutor() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Colores.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(16, 16, 16, 16)
        ));
        panel.setPreferredSize(new Dimension(320, 0));

        // Cabecera
        JLabel lblTit = new JLabel("Tutor destinatario");
        lblTit.setFont(Colores.FUENTE_BOLD);
        lblTit.setForeground(Colores.TEXTO_CLARO);

        lblTutorSel = new JLabel("Ninguno seleccionado");
        lblTutorSel.setFont(Colores.FUENTE_PEQUEÑA);
        lblTutorSel.setForeground(Colores.TEXTO_GRIS);

        campoBuscarTutor = new JTextField();
        campoBuscarTutor.setBackground(Colores.FONDO_OSCURO);
        campoBuscarTutor.setForeground(Colores.TEXTO_CLARO);
        campoBuscarTutor.setCaretColor(Colores.TEXTO_CLARO);
        campoBuscarTutor.setFont(Colores.FUENTE_NORMAL);
        campoBuscarTutor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
        campoBuscarTutor.setToolTipText("Buscar por nombre o correo...");
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
        cabecera.add(campoBuscarTutor);

        // Lista
        modeloTutores = new DefaultListModel<>();
        listaTutores  = new JList<>(modeloTutores);
        listaTutores.setBackground(Colores.FONDO_OSCURO);
        listaTutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTutores.setFixedCellHeight(46);
        listaTutores.setCellRenderer(new TutorCellRenderer());
        listaTutores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarTutorSel();
        });

        JScrollPane scroll = new JScrollPane(listaTutores);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scroll.setBackground(Colores.FONDO_OSCURO);
        scroll.getViewport().setBackground(Colores.FONDO_OSCURO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(scroll,   BorderLayout.CENTER);
        return panel;
    }

    // ── Columna derecha: asunto + cuerpo + enviar ────

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

        areaCuerpo = new JTextArea();
        areaCuerpo.setBackground(Colores.FONDO_OSCURO);
        areaCuerpo.setForeground(Colores.TEXTO_CLARO);
        areaCuerpo.setCaretColor(Colores.TEXTO_CLARO);
        areaCuerpo.setFont(Colores.FUENTE_NORMAL);
        areaCuerpo.setLineWrap(true);
        areaCuerpo.setWrapStyleWord(true);
        areaCuerpo.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scrollCuerpo = new JScrollPane(areaCuerpo);
        scrollCuerpo.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scrollCuerpo.setBackground(Colores.FONDO_OSCURO);
        scrollCuerpo.getViewport().setBackground(Colores.FONDO_OSCURO);
        scrollCuerpo.getVerticalScrollBar().setUnitIncrement(12);

        panelCuerpo.add(lblCuerpo,    BorderLayout.NORTH);
        panelCuerpo.add(scrollCuerpo, BorderLayout.CENTER);

        // Pie: estado + botón
        lblEstado = new JLabel(" ");
        lblEstado.setFont(Colores.FUENTE_PEQUEÑA);
        lblEstado.setForeground(Colores.TEXTO_GRIS);

        JButton btnEnviar = Componentes.botonPrimario("Enviar correo");
        btnEnviar.addActionListener(e -> enviar());

        JPanel pie = new JPanel(new BorderLayout(8, 0));
        pie.setBackground(Colores.FONDO_PANEL);
        pie.add(lblEstado, BorderLayout.CENTER);
        pie.add(btnEnviar, BorderLayout.EAST);

        panel.add(panelAsunto, BorderLayout.NORTH);
        panel.add(panelCuerpo, BorderLayout.CENTER);
        panel.add(pie,         BorderLayout.SOUTH);
        return panel;
    }

    // ═══════════════════════════════════════════════
    //  TAB: HISTORIAL
    // ═══════════════════════════════════════════════

    private JPanel construirTabHistorial() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Colores.FONDO_OSCURO);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Buscador
        campoBuscarHistorial = new JTextField();
        campoBuscarHistorial.setBackground(Colores.FONDO_PANEL);
        campoBuscarHistorial.setForeground(Colores.TEXTO_CLARO);
        campoBuscarHistorial.setCaretColor(Colores.TEXTO_CLARO);
        campoBuscarHistorial.setFont(Colores.FUENTE_NORMAL);
        campoBuscarHistorial.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
        campoBuscarHistorial.setToolTipText("Filtrar por tutor o asunto...");
        campoBuscarHistorial.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrarHistorial(); }
            public void removeUpdate(DocumentEvent e)  { filtrarHistorial(); }
            public void changedUpdate(DocumentEvent e) { filtrarHistorial(); }
        });

        // Tabla
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
    //  LÓGICA — Tutores
    // ═══════════════════════════════════════════════

    private void cargarTutores() {
        todosTutores.clear();
        todosTutores.addAll(tutorDAO.listarTodos());
        filtrarTutores();
    }

    private void filtrarTutores() {
        String txt = campoBuscarTutor.getText().trim().toLowerCase();
        modeloTutores.clear();
        for (Tutor t : todosTutores) {
            if (txt.isEmpty() || coincideTutor(t, txt)) modeloTutores.addElement(t);
        }
    }

    private boolean coincideTutor(Tutor t, String txt) {
        if (t.getNombre() != null && t.getNombre().toLowerCase().contains(txt)) return true;
        if (t.getEmail()  != null && t.getEmail().toLowerCase().contains(txt))  return true;
        return false;
    }

    private void actualizarTutorSel() {
        Tutor t = listaTutores.getSelectedValue();
        if (t == null) {
            lblTutorSel.setText("Ninguno seleccionado");
            lblTutorSel.setForeground(Colores.TEXTO_GRIS);
        } else {
            String email = (t.getEmail() != null && !t.getEmail().isEmpty()) ? t.getEmail() : "sin correo";
            lblTutorSel.setText("✓ " + t.getNombre() + "  —  " + email);
            lblTutorSel.setForeground(Colores.VERDE);
        }
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA — Envío
    // ═══════════════════════════════════════════════

    private void enviar() {
        Tutor tutor = listaTutores.getSelectedValue();
        if (tutor == null) { mostrarEstado("Selecciona un tutor destinatario.", Colores.AMARILLO); return; }
        if (tutor.getEmail() == null || tutor.getEmail().isEmpty()) {
            mostrarEstado("El tutor \"" + tutor.getNombre() + "\" no tiene correo registrado.", Colores.AMARILLO);
            return;
        }
        String asunto = campoAsunto.getText().trim();
        String cuerpo = areaCuerpo.getText().trim();
        if (asunto.isEmpty()) { mostrarEstado("El asunto no puede estar vacío.", Colores.AMARILLO); return; }
        if (cuerpo.isEmpty()) { mostrarEstado("El mensaje no puede estar vacío.", Colores.AMARILLO); return; }

        mostrarEstado("Enviando...", Colores.TEXTO_GRIS);
        String error = EmailService.enviar(tutor.getEmail(), asunto, cuerpo);
        if (error != null) { mostrarEstado(error, Colores.ROJO); return; }

        mensajeDAO.insertar(new Mensaje(SessionManager.getUsuario().getId(), tutor.getId(), asunto, cuerpo));
        campoAsunto.setText("");
        areaCuerpo.setText("");
        listaTutores.clearSelection();
        campoBuscarTutor.setText("");
        mostrarEstado("Correo enviado a " + tutor.getEmail(), Colores.VERDE);
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
            if (txt.isEmpty()
                    || tutor.toLowerCase().contains(txt)
                    || asunto.toLowerCase().contains(txt)) {
                modeloTabla.addRow(new Object[]{tutor, asunto, m.getFechaTexto()});
                mensajesVisibles.add(m);
            }
        }
    }

    private void eliminarMensaje() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un mensaje del historial para eliminar.",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Mensaje m = mensajesVisibles.get(fila);
        String preview = m.getAsunto() != null ? m.getAsunto() : "(sin asunto)";
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el mensaje \"" + preview + "\"?\nEsta acción no se puede deshacer.",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            if (mensajeDAO.eliminar(m.getId())) {
                cargarHistorial();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el mensaje.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarEstado(String texto, Color color) {
        lblEstado.setText(texto);
        lblEstado.setForeground(color);
    }

    // ═══════════════════════════════════════════════
    //  RENDERER — fila de tutor en la lista
    // ═══════════════════════════════════════════════

    private class TutorCellRenderer implements ListCellRenderer<Tutor> {

        private final JPanel celda   = new JPanel(new BorderLayout(0, 2));
        private final JLabel lblNom  = new JLabel();
        private final JLabel lblInfo = new JLabel();

        TutorCellRenderer() {
            JPanel textos = new JPanel();
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            textos.setOpaque(false);
            textos.add(lblNom);
            textos.add(lblInfo);
            celda.add(textos, BorderLayout.CENTER);
            celda.setBorder(new EmptyBorder(6, 12, 6, 12));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Tutor> list,
                Tutor t, int index, boolean isSelected, boolean cellHasFocus) {

            lblNom.setText(t.getNombre());
            lblNom.setFont(Colores.FUENTE_BOLD);

            String email  = (t.getEmail()      != null && !t.getEmail().isEmpty())      ? t.getEmail()      : "Sin correo";
            String parent = (t.getParentesco() != null && !t.getParentesco().isEmpty()) ? "  ·  " + t.getParentesco() : "";
            lblInfo.setText(email + parent);
            lblInfo.setFont(Colores.FUENTE_PEQUEÑA);

            Color fondo = isSelected
                ? new Color(0x1E, 0x3A, 0x5A)
                : (index % 2 == 0 ? Colores.FONDO_OSCURO : new Color(0x16, 0x20, 0x2C));

            celda.setBackground(fondo);
            lblNom.setForeground(isSelected ? Colores.AZUL_PRIMARIO : Colores.TEXTO_CLARO);
            lblInfo.setForeground(Colores.TEXTO_GRIS);
            return celda;
        }
    }

    // ═══════════════════════════════════════════════
    //  ESTILO — JTabbedPane oscuro
    // ═══════════════════════════════════════════════

    private void estilizarTabs(JTabbedPane tabs) {
        tabs.setBackground(Colores.FONDO_OSCURO);
        tabs.setForeground(Colores.TEXTO_CLARO);
        tabs.setFont(Colores.FUENTE_BOLD);
        tabs.setBorder(null);
        tabs.setOpaque(true);

        UIManager.put("TabbedPane.selected",          Colores.FONDO_PANEL);
        UIManager.put("TabbedPane.background",        Colores.FONDO_OSCURO);
        UIManager.put("TabbedPane.foreground",        Colores.TEXTO_CLARO);
        UIManager.put("TabbedPane.unselectedBackground", Colores.SIDEBAR_FONDO);
        UIManager.put("TabbedPane.contentAreaColor",  Colores.FONDO_OSCURO);
        UIManager.put("TabbedPane.light",             Colores.BORDE);
        UIManager.put("TabbedPane.highlight",         Colores.BORDE);
        UIManager.put("TabbedPane.shadow",            Colores.BORDE);
        UIManager.put("TabbedPane.darkShadow",        Colores.BORDE);
        UIManager.put("TabbedPane.focus",             Colores.AZUL_PRIMARIO);
        tabs.updateUI();
    }
}

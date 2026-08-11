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

    private JTextField         campoBuscarTutor;
    private JList<Tutor>       listaTutores;
    private DefaultListModel<Tutor> modeloTutores;
    private JLabel             lblTutorSeleccionado;

    private JTextField         campoAsunto;
    private JTextArea          areaCuerpo;
    private JLabel             lblEstado;
    private JTable             tabla;
    private DefaultTableModel  modeloTabla;

    private final List<Tutor> todosTutores = new ArrayList<>();

    public MessagingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 14));
        setBackground(Colores.FONDO_OSCURO);

        add(construirNorte(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);

        cargarHistorial();
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ
    // ═══════════════════════════════════════════════

    private JPanel construirNorte() {
        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setBackground(Colores.FONDO_OSCURO);

        JPanel fila1 = new JPanel(new BorderLayout());
        fila1.setBackground(Colores.FONDO_OSCURO);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JLabel titulo = new JLabel("Mensajería");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        fila1.add(titulo, BorderLayout.WEST);

        // ── Card principal ────────────────────────────
        JPanel card = new JPanel(new BorderLayout(14, 10));
        card.setBackground(Colores.FONDO_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(14, 16, 14, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Columna izquierda: buscador de tutor ──────
        JPanel colTutor = new JPanel(new BorderLayout(0, 6));
        colTutor.setBackground(Colores.FONDO_PANEL);
        colTutor.setPreferredSize(new Dimension(300, 0));

        JPanel cabTutor = new JPanel(new BorderLayout(0, 4));
        cabTutor.setBackground(Colores.FONDO_PANEL);

        JLabel lblTitTutor = new JLabel("Tutor destinatario");
        lblTitTutor.setFont(Colores.FUENTE_PEQUEÑA);
        lblTitTutor.setForeground(Colores.TEXTO_GRIS);

        lblTutorSeleccionado = new JLabel("Ninguno seleccionado");
        lblTutorSeleccionado.setFont(Colores.FUENTE_PEQUEÑA);
        lblTutorSeleccionado.setForeground(Colores.TEXTO_GRIS);
        lblTutorSeleccionado.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel filaTitTutor = new JPanel(new BorderLayout());
        filaTitTutor.setBackground(Colores.FONDO_PANEL);
        filaTitTutor.add(lblTitTutor,          BorderLayout.WEST);
        filaTitTutor.add(lblTutorSeleccionado, BorderLayout.EAST);

        campoBuscarTutor = new JTextField();
        campoBuscarTutor.setBackground(Colores.FONDO_OSCURO);
        campoBuscarTutor.setForeground(Colores.TEXTO_CLARO);
        campoBuscarTutor.setCaretColor(Colores.TEXTO_CLARO);
        campoBuscarTutor.setFont(Colores.FUENTE_NORMAL);
        campoBuscarTutor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(5, 8, 5, 8)
        ));
        campoBuscarTutor.setToolTipText("Buscar por nombre o correo...");
        campoBuscarTutor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrarTutores(); }
            public void removeUpdate(DocumentEvent e)  { filtrarTutores(); }
            public void changedUpdate(DocumentEvent e) { filtrarTutores(); }
        });

        cabTutor.add(filaTitTutor,    BorderLayout.NORTH);
        cabTutor.add(campoBuscarTutor, BorderLayout.SOUTH);

        modeloTutores = new DefaultListModel<>();
        listaTutores  = new JList<>(modeloTutores);
        listaTutores.setBackground(Colores.FONDO_OSCURO);
        listaTutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTutores.setFixedCellHeight(42);
        listaTutores.setCellRenderer(new TutorCellRenderer());
        listaTutores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) actualizarTutorSeleccionado();
        });

        JScrollPane scrollTutores = new JScrollPane(listaTutores);
        scrollTutores.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scrollTutores.setBackground(Colores.FONDO_OSCURO);
        scrollTutores.getViewport().setBackground(Colores.FONDO_OSCURO);
        scrollTutores.getVerticalScrollBar().setUnitIncrement(16);

        colTutor.add(cabTutor,      BorderLayout.NORTH);
        colTutor.add(scrollTutores, BorderLayout.CENTER);

        // ── Columna derecha: asunto + cuerpo ──────────
        JPanel colMensaje = new JPanel(new BorderLayout(0, 8));
        colMensaje.setBackground(Colores.FONDO_PANEL);

        JPanel panelAsunto = new JPanel(new BorderLayout(0, 4));
        panelAsunto.setBackground(Colores.FONDO_PANEL);
        JLabel lblAsunto = new JLabel("Asunto");
        lblAsunto.setFont(Colores.FUENTE_PEQUEÑA);
        lblAsunto.setForeground(Colores.TEXTO_GRIS);
        campoAsunto = new JTextField();
        Componentes.estilizarCampo(campoAsunto);
        panelAsunto.add(lblAsunto,    BorderLayout.NORTH);
        panelAsunto.add(campoAsunto,  BorderLayout.CENTER);

        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 4));
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
        areaCuerpo.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane scrollCuerpo = new JScrollPane(areaCuerpo);
        scrollCuerpo.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scrollCuerpo.setBackground(Colores.FONDO_OSCURO);
        scrollCuerpo.getViewport().setBackground(Colores.FONDO_OSCURO);

        panelCuerpo.add(lblCuerpo,   BorderLayout.NORTH);
        panelCuerpo.add(scrollCuerpo, BorderLayout.CENTER);

        // Fila inferior: estado + botón enviar
        JPanel filaEnviar = new JPanel(new BorderLayout(8, 0));
        filaEnviar.setBackground(Colores.FONDO_PANEL);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(Colores.FUENTE_PEQUEÑA);
        lblEstado.setForeground(Colores.TEXTO_GRIS);

        JButton btnEnviar = Componentes.botonPrimario("Enviar correo");
        btnEnviar.addActionListener(e -> enviar());

        filaEnviar.add(lblEstado, BorderLayout.CENTER);
        filaEnviar.add(btnEnviar, BorderLayout.EAST);

        colMensaje.add(panelAsunto, BorderLayout.NORTH);
        colMensaje.add(panelCuerpo, BorderLayout.CENTER);
        colMensaje.add(filaEnviar,  BorderLayout.SOUTH);

        card.add(colTutor,   BorderLayout.WEST);
        card.add(colMensaje, BorderLayout.CENTER);

        // ── Título historial ──────────────────────────
        JLabel lblHistorial = new JLabel("Mensajes enviados");
        lblHistorial.setFont(Colores.FUENTE_BOLD);
        lblHistorial.setForeground(Colores.TEXTO_GRIS);
        lblHistorial.setAlignmentX(Component.LEFT_ALIGNMENT);

        norte.add(fila1);
        norte.add(Box.createVerticalStrut(10));
        norte.add(card);
        norte.add(Box.createVerticalStrut(14));
        norte.add(lblHistorial);
        norte.add(Box.createVerticalStrut(4));

        cargarTutores();
        return norte;
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"Tutor", "Asunto", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(170);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(340);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150);
        return Componentes.crearScrollTabla(tabla);
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void cargarTutores() {
        todosTutores.clear();
        todosTutores.addAll(tutorDAO.listarTodos());
        filtrarTutores();
    }

    private void filtrarTutores() {
        String texto = campoBuscarTutor.getText().trim().toLowerCase();
        modeloTutores.clear();
        for (Tutor t : todosTutores) {
            if (texto.isEmpty() || coincideTutor(t, texto)) {
                modeloTutores.addElement(t);
            }
        }
    }

    private boolean coincideTutor(Tutor t, String texto) {
        if (t.getNombre() != null && t.getNombre().toLowerCase().contains(texto)) return true;
        if (t.getEmail()  != null && t.getEmail().toLowerCase().contains(texto))  return true;
        return false;
    }

    private void actualizarTutorSeleccionado() {
        Tutor t = listaTutores.getSelectedValue();
        if (t == null) {
            lblTutorSeleccionado.setText("Ninguno seleccionado");
            lblTutorSeleccionado.setForeground(Colores.TEXTO_GRIS);
        } else {
            String email = (t.getEmail() != null && !t.getEmail().isEmpty()) ? t.getEmail() : "sin correo";
            lblTutorSeleccionado.setText("✓ " + t.getNombre() + " — " + email);
            lblTutorSeleccionado.setForeground(Colores.VERDE);
        }
    }

    private void enviar() {
        Tutor tutor = listaTutores.getSelectedValue();
        if (tutor == null) {
            mostrarEstado("Selecciona un tutor destinatario.", Colores.AMARILLO);
            return;
        }
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

        int usuarioId = SessionManager.getUsuario().getId();
        mensajeDAO.insertar(new Mensaje(usuarioId, tutor.getId(), asunto, cuerpo));

        campoAsunto.setText("");
        areaCuerpo.setText("");
        listaTutores.clearSelection();
        campoBuscarTutor.setText("");
        mostrarEstado("Correo enviado a " + tutor.getEmail(), Colores.VERDE);
        cargarHistorial();
    }

    private void cargarHistorial() {
        List<Mensaje> lista = mensajeDAO.listarTodos();
        modeloTabla.setRowCount(0);
        for (Mensaje m : lista) {
            modeloTabla.addRow(new Object[]{
                m.getTutorNombre() != null ? m.getTutorNombre() : "-",
                m.getAsunto(),
                m.getFechaTexto()
            });
        }
    }

    private void mostrarEstado(String texto, Color color) {
        lblEstado.setText(texto);
        lblEstado.setForeground(color);
    }

    // ═══════════════════════════════════════════════
    //  RENDERER — celda de tutor
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

            String email = (t.getEmail() != null && !t.getEmail().isEmpty()) ? t.getEmail() : "Sin correo";
            String parent = (t.getParentesco() != null && !t.getParentesco().isEmpty()) ? "  ·  " + t.getParentesco() : "";
            lblInfo.setText(email + parent);
            lblInfo.setFont(Colores.FUENTE_PEQUEÑA);

            Color fondo = isSelected ? new Color(0x1E, 0x3A, 0x5A)
                        : (index % 2 == 0 ? Colores.FONDO_OSCURO : new Color(0x16, 0x20, 0x2C));

            celda.setBackground(fondo);
            lblNom.setForeground(isSelected ? Colores.AZUL_PRIMARIO : Colores.TEXTO_CLARO);
            lblInfo.setForeground(Colores.TEXTO_GRIS);

            return celda;
        }
    }
}

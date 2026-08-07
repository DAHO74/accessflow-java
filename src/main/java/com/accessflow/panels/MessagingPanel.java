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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MessagingPanel extends JPanel {

    private final MainFrame  mainFrame;
    private final TutorDAO   tutorDAO   = new TutorDAO();
    private final MensajeDAO mensajeDAO = new MensajeDAO();

    private JComboBox<Tutor> cmbTutor;
    private JTextField       campoAsunto;
    private JTextArea        areaCuerpo;
    private JLabel           lblEstado;
    private JTable           tabla;
    private DefaultTableModel modeloTabla;

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

        // ── Fila 1: título ─────────────────────────
        JPanel fila1 = new JPanel(new BorderLayout());
        fila1.setBackground(Colores.FONDO_OSCURO);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titulo = new JLabel("Mensajería");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        fila1.add(titulo, BorderLayout.WEST);

        // ── Card de redacción ──────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Colores.FONDO_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(14, 16, 14, 16)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        // Fila: tutor + asunto
        JPanel filaTop = new JPanel(new GridLayout(1, 2, 12, 0));
        filaTop.setBackground(Colores.FONDO_PANEL);
        filaTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        filaTop.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panelTutor  = construirCampoConEtiqueta("Tutor destinatario", null);
        JPanel panelAsunto = construirCampoConEtiqueta("Asunto",             null);

        // Construir combobox de tutores
        cmbTutor = new JComboBox<>();
        cmbTutor.addItem(null); // sin selección
        for (Tutor t : tutorDAO.listarTodos()) {
            cmbTutor.addItem(t);
        }
        cmbTutor.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Seleccionar tutor..." : value.toString());
                setBackground(isSelected ? Colores.AZUL_PRIMARIO : Colores.FONDO_OSCURO);
                setForeground(value == null ? Colores.TEXTO_GRIS : Colores.TEXTO_CLARO);
                setBorder(new EmptyBorder(3, 8, 3, 8));
                return this;
            }
        });
        cmbTutor.setBackground(Colores.FONDO_OSCURO);
        cmbTutor.setForeground(Colores.TEXTO_CLARO);
        cmbTutor.setFont(Colores.FUENTE_NORMAL);
        cmbTutor.setBorder(BorderFactory.createLineBorder(Colores.BORDE));

        campoAsunto = new JTextField();
        Componentes.estilizarCampo(campoAsunto);

        panelTutor.add(cmbTutor,    BorderLayout.CENTER);
        panelAsunto.add(campoAsunto, BorderLayout.CENTER);
        filaTop.add(panelTutor);
        filaTop.add(panelAsunto);

        // Área de texto del cuerpo
        areaCuerpo = new JTextArea(4, 0);
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
        scrollCuerpo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        scrollCuerpo.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fila inferior: estado + botón enviar
        JPanel filaEnviar = new JPanel(new BorderLayout(8, 0));
        filaEnviar.setBackground(Colores.FONDO_PANEL);
        filaEnviar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        filaEnviar.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(Colores.FUENTE_PEQUEÑA);
        lblEstado.setForeground(Colores.TEXTO_GRIS);

        JButton btnEnviar = Componentes.botonPrimario("Enviar correo");
        btnEnviar.addActionListener(e -> enviar());

        filaEnviar.add(lblEstado,  BorderLayout.CENTER);
        filaEnviar.add(btnEnviar,  BorderLayout.EAST);

        JLabel lblCuerpo = new JLabel("Mensaje");
        lblCuerpo.setFont(Colores.FUENTE_PEQUEÑA);
        lblCuerpo.setForeground(Colores.TEXTO_GRIS);
        lblCuerpo.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(filaTop);
        card.add(Box.createVerticalStrut(10));
        card.add(lblCuerpo);
        card.add(Box.createVerticalStrut(4));
        card.add(scrollCuerpo);
        card.add(Box.createVerticalStrut(8));
        card.add(filaEnviar);

        // ── Título del historial ────────────────────
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
        return norte;
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"Tutor", "Asunto", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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

    private void enviar() {
        Tutor tutor = (Tutor) cmbTutor.getSelectedItem();
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
        if (asunto.isEmpty()) {
            mostrarEstado("El asunto no puede estar vacío.", Colores.AMARILLO);
            return;
        }
        if (cuerpo.isEmpty()) {
            mostrarEstado("El mensaje no puede estar vacío.", Colores.AMARILLO);
            return;
        }

        mostrarEstado("Enviando...", Colores.TEXTO_GRIS);

        String error = EmailService.enviar(tutor.getEmail(), asunto, cuerpo);
        if (error != null) {
            mostrarEstado(error, Colores.ROJO);
            return;
        }

        // Guardar en historial solo si el correo salió bien
        int usuarioId = SessionManager.getUsuario().getId();
        Mensaje m = new Mensaje(usuarioId, tutor.getId(), asunto, cuerpo);
        mensajeDAO.insertar(m);

        campoAsunto.setText("");
        areaCuerpo.setText("");
        cmbTutor.setSelectedIndex(0);
        mostrarEstado("Correo enviado a " + tutor.getEmail(), Colores.VERDE);
        cargarHistorial();
    }

    private void cargarHistorial() {
        List<Mensaje> lista = mensajeDAO.listarTodos();
        modeloTabla.setRowCount(0);
        for (Mensaje m : lista) {
            String tutor = (m.getTutorNombre() != null) ? m.getTutorNombre() : "-";
            modeloTabla.addRow(new Object[]{tutor, m.getAsunto(), m.getFechaTexto()});
        }
    }

    private void mostrarEstado(String texto, Color color) {
        lblEstado.setText(texto);
        lblEstado.setForeground(color);
    }

    // ── Auxiliar ──────────────────────────────────────

    private JPanel construirCampoConEtiqueta(String etiqueta, Object campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Colores.FONDO_PANEL);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        p.add(lbl, BorderLayout.NORTH);
        // el campo real se añade desde afuera con p.add(campo, CENTER)

        return p;
    }
}

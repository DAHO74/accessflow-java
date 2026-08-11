package com.accessflow.panels;

import com.accessflow.dao.AlumnoDAO;
import com.accessflow.dao.AsistenciaDAO;
import com.accessflow.model.Alumno;
import com.accessflow.model.Asistencia;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AttendancePanel extends JPanel {

    private final MainFrame      mainFrame;
    private final AlumnoDAO      alumnoDAO      = new AlumnoDAO();
    private final AsistenciaDAO  asistenciaDAO  = new AsistenciaDAO();

    private JTextField        campoRfid;
    private JLabel            lblMensaje;
    private JLabel            lblPresentes;
    private JTable            tabla;
    private DefaultTableModel modeloTabla;

    public AttendancePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 14));
        setBackground(Colores.FONDO_OSCURO);

        add(construirNorte(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);

        cargarTablaHoy();
        actualizarContador();
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ
    // ═══════════════════════════════════════════════

    private JPanel construirNorte() {
        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.setBackground(Colores.FONDO_OSCURO);

        // ── Fila 1: título + conteo de presentes ──────
        JPanel fila1 = new JPanel(new BorderLayout(8, 0));
        fila1.setBackground(Colores.FONDO_OSCURO);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titulo = new JLabel("Asistencia de hoy");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);

        lblPresentes = new JLabel("0 presentes");
        lblPresentes.setFont(Colores.FUENTE_BOLD);
        lblPresentes.setForeground(Colores.VERDE);
        lblPresentes.setHorizontalAlignment(SwingConstants.RIGHT);

        fila1.add(titulo,       BorderLayout.WEST);
        fila1.add(lblPresentes, BorderLayout.EAST);

        // ── Fila 2: panel de registro RFID ────────────
        JPanel panelRfid = new JPanel();
        panelRfid.setLayout(new BoxLayout(panelRfid, BoxLayout.Y_AXIS));
        panelRfid.setBackground(Colores.FONDO_PANEL);
        panelRfid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(12, 16, 12, 16)
        ));
        panelRfid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel instruccion = new JLabel("Ingresa o escanea el UID RFID del alumno:");
        instruccion.setFont(Colores.FUENTE_PEQUEÑA);
        instruccion.setForeground(Colores.TEXTO_GRIS);
        instruccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filaInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaInput.setBackground(Colores.FONDO_PANEL);
        filaInput.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoRfid = new JTextField(22);
        campoRfid.setBackground(Colores.FONDO_OSCURO);
        campoRfid.setForeground(Colores.TEXTO_CLARO);
        campoRfid.setCaretColor(Colores.TEXTO_CLARO);
        campoRfid.setFont(Colores.FUENTE_NORMAL);
        campoRfid.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(5, 8, 5, 8)
        ));
        // Enter en el campo dispara el registro (útil con lector RFID físico)
        campoRfid.addActionListener(e -> registrar());

        JButton btnRegistrar = Componentes.botonPrimario("Registrar");
        btnRegistrar.addActionListener(e -> registrar());

        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(Colores.FUENTE_BOLD);
        lblMensaje.setForeground(Colores.TEXTO_GRIS);

        filaInput.add(campoRfid);
        filaInput.add(btnRegistrar);
        filaInput.add(lblMensaje);

        panelRfid.add(instruccion);
        panelRfid.add(Box.createVerticalStrut(6));
        panelRfid.add(filaInput);

        norte.add(fila1);
        norte.add(Box.createVerticalStrut(10));
        norte.add(panelRfid);
        return norte;
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"Alumno", "Grupo", "Hora entrada", "Hora salida", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(110);

        return Componentes.crearScrollTabla(tabla);
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void registrar() {
        String rfid = campoRfid.getText().trim();
        if (rfid.isEmpty()) {
            mostrarMensaje("Escribe o escanea un UID.", Colores.AMARILLO);
            return;
        }

        Alumno alumno = alumnoDAO.buscarPorRfid(rfid);
        if (alumno == null) {
            mostrarMensaje("RFID no registrado: " + rfid, Colores.ROJO);
            campoRfid.selectAll();
            return;
        }

        Asistencia sesionActiva = asistenciaDAO.buscarSesionActiva(alumno.getId());

        if (sesionActiva != null) {
            boolean ok = asistenciaDAO.registrarSalida(sesionActiva.getId());
            if (ok) {
                mostrarMensaje("Salida registrada: " + alumno.getNombre(), Colores.AZUL_PRIMARIO);
            } else {
                mostrarMensaje("Error al registrar salida.", Colores.ROJO);
            }
        } else {
            boolean ok = asistenciaDAO.registrarEntrada(alumno.getId());
            if (ok) {
                mostrarMensaje("Entrada registrada: " + alumno.getNombre(), Colores.VERDE);
            } else {
                mostrarMensaje("Error al registrar entrada.", Colores.ROJO);
            }
        }

        campoRfid.setText("");
        campoRfid.requestFocus();
        cargarTablaHoy();
        actualizarContador();
    }

    private void cargarTablaHoy() {
        List<Asistencia> lista = asistenciaDAO.listarHoy();
        modeloTabla.setRowCount(0);
        for (Asistencia a : lista) {
            String grupo = (a.getGrupoNombre() != null) ? a.getGrupoNombre() : "Sin grupo";
            modeloTabla.addRow(new Object[]{
                a.getAlumnoNombre(),
                grupo,
                a.getHoraEntrada(),
                a.getHoraSalida(),
                a.getEstado()
            });
        }
    }

    private void actualizarContador() {
        int presentes = asistenciaDAO.contarPresentesHoy();
        lblPresentes.setText(presentes + " presentes hoy");
    }

    private void mostrarMensaje(String texto, Color color) {
        lblMensaje.setText(texto);
        lblMensaje.setForeground(color);
    }
}

package com.accessflow.panels;

import com.accessflow.dao.AlumnoDAO;
import com.accessflow.dao.AsistenciaDAO;
import com.accessflow.dao.GrupoDAO;
import com.accessflow.model.Asistencia;
import com.accessflow.util.Colores;

import com.accessflow.util.Componentes;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final AlumnoDAO     alumnoDAO     = new AlumnoDAO();
    private final GrupoDAO      grupoDAO      = new GrupoDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

    // Labels de las tarjetas (necesitamos referencias para actualizarlas)
    private JLabel           lblTotalAlumnos;
    private JLabel           lblTotalGrupos;
    private JLabel           lblPresentes;
    private JLabel           lblAusentes;
    private DefaultTableModel modeloTabla;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(),        BorderLayout.NORTH);
        add(construirCuerpo(),        BorderLayout.CENTER);

        cargarDatos();
    }

    // ═══════════════════════════════════════════════
    //  CONSTRUCCIÓN DE LA INTERFAZ
    // ═══════════════════════════════════════════════

    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colores.FONDO_OSCURO);
        header.setBorder(new EmptyBorder(0, 0, 4, 0));

        JLabel titulo = new JLabel("Inicio");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(Colores.AZUL_PRIMARIO);
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFont(Colores.FUENTE_BOLD);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setOpaque(true);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnActualizar.addActionListener(e -> cargarDatos());

        header.add(titulo,        BorderLayout.WEST);
        header.add(btnActualizar, BorderLayout.EAST);

        return header;
    }

    private JPanel construirCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(0, 16));
        cuerpo.setBackground(Colores.FONDO_OSCURO);

        cuerpo.add(construirTarjetas(),      BorderLayout.NORTH);
        cuerpo.add(construirSeccionTabla(),  BorderLayout.CENTER);

        return cuerpo;
    }

    private JPanel construirTarjetas() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBackground(Colores.FONDO_OSCURO);
        panel.setPreferredSize(new Dimension(0, 110));

        // Creamos los labels aquí para guardar referencia y actualizarlos después
        lblTotalAlumnos = new JLabel("0");
        lblTotalGrupos  = new JLabel("0");
        lblPresentes    = new JLabel("0");
        lblAusentes     = new JLabel("0");

        panel.add(crearTarjeta(lblTotalAlumnos, "Total Alumnos",  Colores.TEXTO_CLARO));
        panel.add(crearTarjeta(lblTotalGrupos,  "Total Grupos",   Colores.TEXTO_CLARO));
        panel.add(crearTarjeta(lblPresentes,    "Presentes hoy",  Colores.VERDE));
        panel.add(crearTarjeta(lblAusentes,     "Ausentes hoy",   Colores.ROJO));

        return panel;
    }

    private JPanel crearTarjeta(JLabel lblNumero, String descripcion, Color colorNumero) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Colores.FONDO_PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));

        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblNumero.setForeground(colorNumero);
        lblNumero.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(Colores.FUENTE_PEQUEÑA);
        lblDesc.setForeground(Colores.TEXTO_GRIS);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(lblNumero);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(lblDesc);
        tarjeta.add(Box.createVerticalGlue());

        return tarjeta;
    }

    private JPanel construirSeccionTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Colores.FONDO_OSCURO);

        JLabel lblTitulo = new JLabel("Registros de hoy");
        lblTitulo.setFont(Colores.FUENTE_BOLD);
        lblTitulo.setForeground(Colores.TEXTO_CLARO);

        // DefaultTableModel maneja los datos de la tabla
        String[] columnas = {"Alumno", "Grupo", "Entrada", "Salida", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // las celdas no son editables
            }
        };

        JTable tabla = new JTable(modeloTabla);
        tabla.setBackground(Colores.FONDO_PANEL);
        tabla.setForeground(Colores.TEXTO_CLARO);
        tabla.setGridColor(Colores.BORDE);
        tabla.setFont(Colores.FUENTE_NORMAL);
        tabla.setRowHeight(32);
        tabla.setSelectionBackground(Colores.AZUL_PRIMARIO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));

        javax.swing.table.DefaultTableCellRenderer centrado = Componentes.rendererCentrado();
        tabla.getColumnModel().getColumn(1).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(3).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(4).setCellRenderer(centrado);

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setBackground(Colores.FONDO_OSCURO);
        encabezado.setForeground(Colores.TEXTO_GRIS);
        encabezado.setFont(Colores.FUENTE_PEQUEÑA);
        encabezado.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Colores.FONDO_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE, 1));

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);

        return panel;
    }

    // ═══════════════════════════════════════════════
    //  CARGA DE DATOS
    // ═══════════════════════════════════════════════

    private void cargarDatos() {
        int totalAlumnos = alumnoDAO.contar();
        int totalGrupos  = grupoDAO.contar();
        int presentes    = asistenciaDAO.contarPresentesHoy();
        int ausentes     = Math.max(0, totalAlumnos - presentes);

        lblTotalAlumnos.setText(String.valueOf(totalAlumnos));
        lblTotalGrupos.setText(String.valueOf(totalGrupos));
        lblPresentes.setText(String.valueOf(presentes));
        lblAusentes.setText(String.valueOf(ausentes));

        // Limpiar la tabla y cargar los registros del día
        modeloTabla.setRowCount(0);
        List<Asistencia> lista = asistenciaDAO.listarHoy();
        for (Asistencia a : lista) {
            String gNombre = a.getGrupoNombre() != null ? a.getGrupoNombre() : "";
            String gGrado  = a.getGrupoGrado()  != null ? a.getGrupoGrado()  : "";
            String grupo   = gGrado.isEmpty() ? (gNombre.isEmpty() ? "-" : gNombre) : gGrado + " " + gNombre;
            modeloTabla.addRow(new Object[]{
                a.getAlumnoNombre(),
                grupo,
                a.getHoraEntrada(),
                a.getHoraSalida(),
                a.getEstado()
            });
        }
    }
}

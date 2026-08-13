package com.accessflow.panels;

import com.accessflow.dao.AsistenciaDAO;
import com.accessflow.dao.GrupoDAO;
import com.accessflow.model.Asistencia;
import com.accessflow.model.Grupo;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AttendanceHistoryPanel extends JPanel {

    private final MainFrame     mainFrame;
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final GrupoDAO      grupoDAO      = new GrupoDAO();

    private JTextField         campoDesde;
    private JTextField         campoHasta;
    private JComboBox<Grupo>   cmbGrupo;
    private JLabel             lblError;
    private JTable             tabla;
    private DefaultTableModel  modeloTabla;

    public AttendanceHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 14));
        setBackground(Colores.FONDO_OSCURO);

        add(construirNorte(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);

        filtrar(); // carga el historial con los filtros por defecto (hoy)
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

        JLabel titulo = new JLabel("Historial de asistencias");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        fila1.add(titulo, BorderLayout.WEST);

        // ── Fila 2: filtros ────────────────────────
        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));
        panelFiltros.setBackground(Colores.FONDO_PANEL);
        panelFiltros.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel filaControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaControles.setBackground(Colores.FONDO_PANEL);
        filaControles.setAlignmentX(Component.LEFT_ALIGNMENT);

        String hoy = LocalDate.now().toString(); // yyyy-MM-dd

        campoDesde = crearCampoFecha(hoy);
        campoHasta = crearCampoFecha(hoy);

        // ComboBox de grupos — primer elemento: "Todos los grupos"
        cmbGrupo = new JComboBox<>();
        cmbGrupo.addItem(null); // null = sin filtro de grupo
        for (Grupo g : grupoDAO.listarTodos()) {
            cmbGrupo.addItem(g);
        }
        cmbGrupo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Todos los grupos" : value.toString());
                setBackground(isSelected ? Colores.AZUL_PRIMARIO : Colores.FONDO_OSCURO);
                setForeground(Colores.TEXTO_CLARO);
                setBorder(new EmptyBorder(3, 6, 3, 6));
                return this;
            }
        });
        cmbGrupo.setBackground(Colores.FONDO_OSCURO);
        cmbGrupo.setForeground(Colores.TEXTO_CLARO);
        cmbGrupo.setFont(Colores.FUENTE_NORMAL);
        cmbGrupo.setPreferredSize(new Dimension(160, 30));
        cmbGrupo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(0, 4, 0, 4)
        ));

        JButton btnFiltrar = Componentes.botonPrimario("Filtrar");
        btnFiltrar.addActionListener(e -> filtrar());

        filaControles.add(crearEtiqueta("Desde:"));
        filaControles.add(campoDesde);
        filaControles.add(crearEtiqueta("Hasta:"));
        filaControles.add(campoHasta);
        filaControles.add(crearEtiqueta("Grupo:"));
        filaControles.add(cmbGrupo);
        filaControles.add(btnFiltrar);

        lblError = new JLabel(" ");
        lblError.setFont(Colores.FUENTE_PEQUEÑA);
        lblError.setForeground(Colores.ROJO);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelFiltros.add(filaControles);
        panelFiltros.add(Box.createVerticalStrut(5));
        panelFiltros.add(lblError);

        norte.add(fila1);
        norte.add(Box.createVerticalStrut(10));
        norte.add(panelFiltros);
        return norte;
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"Alumno", "Grupo", "Entrada", "Salida", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);

        javax.swing.table.DefaultTableCellRenderer centrado = Componentes.rendererCentrado();
        tabla.getColumnModel().getColumn(1).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(3).setCellRenderer(centrado);
        tabla.getColumnModel().getColumn(4).setCellRenderer(centrado);

        return Componentes.crearScrollTabla(tabla);
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void filtrar() {
        lblError.setText(" ");

        LocalDate desde;
        LocalDate hasta;
        try {
            desde = LocalDate.parse(campoDesde.getText().trim());
            hasta = LocalDate.parse(campoHasta.getText().trim());
        } catch (DateTimeParseException ex) {
            lblError.setText("Formato de fecha incorrecto. Usa yyyy-MM-dd (ej. 2025-08-01).");
            return;
        }

        if (hasta.isBefore(desde)) {
            lblError.setText("La fecha 'Hasta' no puede ser anterior a 'Desde'.");
            return;
        }

        Grupo grupoSeleccionado = (Grupo) cmbGrupo.getSelectedItem();
        Integer grupoId = (grupoSeleccionado != null) ? grupoSeleccionado.getId() : null;

        List<Asistencia> lista = asistenciaDAO.buscarPorFiltros(desde, hasta, grupoId, null);
        modeloTabla.setRowCount(0);
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

    // ── Auxiliares de estilo ─────────────────────────

    private JTextField crearCampoFecha(String valorDefecto) {
        JTextField campo = new JTextField(10);
        campo.setText(valorDefecto);
        campo.setBackground(Colores.FONDO_OSCURO);
        campo.setForeground(Colores.TEXTO_CLARO);
        campo.setCaretColor(Colores.TEXTO_CLARO);
        campo.setFont(Colores.FUENTE_NORMAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(5, 8, 5, 8)
        ));
        return campo;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        return lbl;
    }
}

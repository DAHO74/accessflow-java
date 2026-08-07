package com.accessflow.panels;

import com.accessflow.dao.GrupoDAO;
import com.accessflow.model.Grupo;
import com.accessflow.service.SessionManager;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GroupsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final GrupoDAO  grupoDAO = new GrupoDAO();

    private JTextField        campoBuscar;
    private JTable            tabla;
    private DefaultTableModel modeloTabla;
    private List<Grupo>       todosLosGrupos = new ArrayList<>();

    public GroupsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 16));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirTabla(),  BorderLayout.CENTER);

        cargarGrupos();
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ
    // ═══════════════════════════════════════════════

    private JPanel construirHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Colores.FONDO_OSCURO);
        header.setBorder(new EmptyBorder(0, 0, 4, 0));

        // Fila 1: título + botón Nuevo (solo admin)
        JPanel fila1 = new JPanel(new BorderLayout());
        fila1.setBackground(Colores.FONDO_OSCURO);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JLabel titulo = new JLabel("Grupos");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        fila1.add(titulo, BorderLayout.WEST);

        if (SessionManager.esAdmin()) {
            JButton btnNuevo = Componentes.botonPrimario("+ Nuevo grupo");
            btnNuevo.addActionListener(e -> nuevo());
            fila1.add(btnNuevo, BorderLayout.EAST);
        }

        // Fila 2: búsqueda + acciones
        JPanel fila2 = new JPanel(new BorderLayout(8, 0));
        fila2.setBackground(Colores.FONDO_OSCURO);
        fila2.setBorder(new EmptyBorder(10, 0, 0, 0));
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        campoBuscar = new JTextField();
        campoBuscar.setBackground(Colores.FONDO_PANEL);
        campoBuscar.setForeground(Colores.TEXTO_CLARO);
        campoBuscar.setCaretColor(Colores.TEXTO_CLARO);
        campoBuscar.setFont(Colores.FUENTE_NORMAL);
        campoBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelBotones.setBackground(Colores.FONDO_OSCURO);

        JButton btnBuscar = Componentes.botonSecundario("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        panelBotones.add(btnBuscar);

        if (SessionManager.esAdmin()) {
            JButton btnEditar   = Componentes.botonSecundario("Editar");
            JButton btnEliminar = Componentes.botonPeligro("Eliminar");
            btnEditar.addActionListener(e   -> editar());
            btnEliminar.addActionListener(e -> eliminar());
            panelBotones.add(btnEditar);
            panelBotones.add(btnEliminar);
        }

        fila2.add(campoBuscar,  BorderLayout.CENTER);
        fila2.add(panelBotones, BorderLayout.EAST);

        header.add(fila1);
        header.add(fila2);
        return header;
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"ID", "Nombre", "Grado", "Turno"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        tabla.getColumnModel().getColumn(0).setMaxWidth(55);
        tabla.getColumnModel().getColumn(0).setCellRenderer(Componentes.rendererCentrado());
        tabla.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150);

        return Componentes.crearScrollTabla(tabla);
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void cargarGrupos() {
        todosLosGrupos = grupoDAO.listarTodos();
        mostrarEnTabla(todosLosGrupos);
    }

    private void mostrarEnTabla(List<Grupo> lista) {
        modeloTabla.setRowCount(0);
        for (Grupo g : lista) {
            String grado = (g.getGrado() != null && !g.getGrado().isEmpty()) ? g.getGrado() : "-";
            String turno = (g.getTurno() != null && !g.getTurno().isEmpty()) ? g.getTurno() : "-";
            modeloTabla.addRow(new Object[]{g.getId(), g.getNombre(), grado, turno});
        }
    }

    private void buscar() {
        String texto = campoBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            mostrarEnTabla(todosLosGrupos);
            return;
        }
        List<Grupo> filtrados = new ArrayList<>();
        for (Grupo g : todosLosGrupos) {
            if (g.getNombre().toLowerCase().contains(texto)) {
                filtrados.add(g);
            }
        }
        mostrarEnTabla(filtrados);
    }

    private void nuevo() {
        mainFrame.mostrarPanel(new GroupFormPanel(mainFrame, null));
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un grupo de la lista para editar.",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Grupo grupo = grupoDAO.buscarPorId(id);
        mainFrame.mostrarPanel(new GroupFormPanel(mainFrame, grupo));
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un grupo de la lista para eliminar.",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar el grupo \"" + nombre + "\"?\n" +
            "Los alumnos de este grupo quedarán sin grupo asignado.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(fila, 0);
            if (grupoDAO.eliminar(id)) {
                cargarGrupos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el grupo.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

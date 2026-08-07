package com.accessflow.panels;

import com.accessflow.dao.TutorDAO;
import com.accessflow.model.Tutor;
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

public class TutorsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final TutorDAO  tutorDAO = new TutorDAO();

    private JTextField        campoBuscar;
    private JTable            tabla;
    private DefaultTableModel modeloTabla;
    private List<Tutor>       todosLosTutores = new ArrayList<>();

    public TutorsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 16));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirTabla(),  BorderLayout.CENTER);

        cargarTutores();
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

        JLabel titulo = new JLabel("Tutores");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        fila1.add(titulo, BorderLayout.WEST);

        if (SessionManager.esAdmin()) {
            JButton btnNuevo = Componentes.botonPrimario("+ Nuevo tutor");
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
        String[] columnas = {"ID", "Nombre", "Correo electrónico", "Teléfono"};
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
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(220);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);

        return Componentes.crearScrollTabla(tabla);
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void cargarTutores() {
        todosLosTutores = tutorDAO.listarTodos();
        mostrarEnTabla(todosLosTutores);
    }

    private void mostrarEnTabla(List<Tutor> lista) {
        modeloTabla.setRowCount(0);
        for (Tutor t : lista) {
            String email    = (t.getEmail()    != null && !t.getEmail().isEmpty())    ? t.getEmail()    : "-";
            String telefono = (t.getTelefono() != null && !t.getTelefono().isEmpty()) ? t.getTelefono() : "-";
            modeloTabla.addRow(new Object[]{t.getId(), t.getNombre(), email, telefono});
        }
    }

    private void buscar() {
        String texto = campoBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            mostrarEnTabla(todosLosTutores);
            return;
        }
        List<Tutor> filtrados = new ArrayList<>();
        for (Tutor t : todosLosTutores) {
            if (t.getNombre().toLowerCase().contains(texto)) {
                filtrados.add(t);
            }
        }
        mostrarEnTabla(filtrados);
    }

    private void nuevo() {
        mainFrame.mostrarPanel(new TutorFormPanel(mainFrame, null));
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un tutor de la lista para editar.",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Tutor tutor = tutorDAO.buscarPorId(id);
        mainFrame.mostrarPanel(new TutorFormPanel(mainFrame, tutor));
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un tutor de la lista para eliminar.",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar al tutor \"" + nombre + "\"?\nEsta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(fila, 0);
            if (tutorDAO.eliminar(id)) {
                cargarTutores();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el tutor.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

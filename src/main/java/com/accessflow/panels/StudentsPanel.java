package com.accessflow.panels;

import com.accessflow.dao.AlumnoDAO;
import com.accessflow.model.Alumno;
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

public class StudentsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final AlumnoDAO alumnoDAO = new AlumnoDAO();

    private JTextField        campoBuscar;
    private JTable            tabla;
    private DefaultTableModel modeloTabla;
    private List<Alumno>      todosLosAlumnos = new ArrayList<>();

    public StudentsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 16));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirTabla(),  BorderLayout.CENTER);

        cargarAlumnos();
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

        JLabel titulo = new JLabel("Alumnos");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        fila1.add(titulo, BorderLayout.WEST);

        if (SessionManager.esAdmin()) {
            JButton btnNuevo = Componentes.botonPrimario("+ Nuevo alumno");
            btnNuevo.addActionListener(e -> nuevo());
            fila1.add(btnNuevo, BorderLayout.EAST);
        }

        // Fila 2: campo de búsqueda + botones de acción
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
        String[] columnas = {"ID", "Nombre", "RFID UID", "Grupo", "Tutor"};
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
        tabla.getColumnModel().getColumn(1).setPreferredWidth(190);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(150);

        return Componentes.crearScrollTabla(tabla);
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void cargarAlumnos() {
        todosLosAlumnos = alumnoDAO.listarTodos();
        mostrarEnTabla(todosLosAlumnos);
    }

    private void mostrarEnTabla(List<Alumno> lista) {
        modeloTabla.setRowCount(0);
        for (Alumno a : lista) {
            String rfid  = (a.getRfidUid() != null && !a.getRfidUid().isEmpty())
                           ? a.getRfidUid() : "-";
            String grupo = (a.getGrupoNombre() != null && !a.getGrupoNombre().isEmpty())
                           ? a.getGrupoNombre() : "Sin grupo";
            String tutor = (a.getTutorNombre() != null && !a.getTutorNombre().isEmpty())
                           ? a.getTutorNombre() : "Sin tutor";
            modeloTabla.addRow(new Object[]{a.getId(), a.getNombre(), rfid, grupo, tutor});
        }
    }

    private void buscar() {
        String texto = campoBuscar.getText().trim().toLowerCase();
        if (texto.isEmpty()) {
            mostrarEnTabla(todosLosAlumnos);
            return;
        }
        List<Alumno> filtrados = new ArrayList<>();
        for (Alumno a : todosLosAlumnos) {
            if (a.getNombre().toLowerCase().contains(texto)) {
                filtrados.add(a);
            }
        }
        mostrarEnTabla(filtrados);
    }

    private void nuevo() {
        mainFrame.mostrarPanel(new StudentFormPanel(mainFrame, null));
    }

    private void editar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un alumno de la lista para editar.",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        Alumno alumno = alumnoDAO.buscarPorId(id);
        mainFrame.mostrarPanel(new StudentFormPanel(mainFrame, alumno));
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                "Selecciona un alumno de la lista para eliminar.",
                "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Eliminar al alumno \"" + nombre + "\"?\nEsta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabla.getValueAt(fila, 0);
            if (alumnoDAO.eliminar(id)) {
                cargarAlumnos();
            } else {
                JOptionPane.showMessageDialog(this,
                    "No se pudo eliminar el alumno.\n" +
                    "Puede tener registros de asistencia asociados.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

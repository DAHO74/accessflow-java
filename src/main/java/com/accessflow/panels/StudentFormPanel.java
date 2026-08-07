package com.accessflow.panels;

import com.accessflow.dao.AlumnoDAO;
import com.accessflow.dao.GrupoDAO;
import com.accessflow.dao.TutorDAO;
import com.accessflow.model.Alumno;
import com.accessflow.model.Grupo;
import com.accessflow.model.Tutor;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class StudentFormPanel extends JPanel {

    private final MainFrame mainFrame;
    private final Alumno    alumno;

    private final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private final GrupoDAO  grupoDAO  = new GrupoDAO();
    private final TutorDAO  tutorDAO  = new TutorDAO();

    private JTextField       campoNombre;
    private JTextField       campoRfid;
    private JComboBox<Grupo> cmbGrupo;
    private JComboBox<Tutor> cmbTutor;
    private JLabel           lblError;

    public StudentFormPanel(MainFrame mainFrame, Alumno alumno) {
        this.mainFrame = mainFrame;
        this.alumno    = alumno;

        setLayout(new BorderLayout(0, 0));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);

        if (alumno != null) {
            llenarFormulario();
        }
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ
    // ═══════════════════════════════════════════════

    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colores.FONDO_OSCURO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        String textoTitulo = (alumno == null) ? "Nuevo alumno" : "Editar alumno";
        JLabel titulo = new JLabel(textoTitulo);
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);

        JButton btnVolver = Componentes.botonSecundario("← Volver");
        btnVolver.addActionListener(e -> mainFrame.irAAlumnos());

        header.add(titulo,    BorderLayout.WEST);
        header.add(btnVolver, BorderLayout.EAST);
        return header;
    }

    private JPanel construirCentro() {
        JPanel exterior = new JPanel(new GridBagLayout());
        exterior.setBackground(Colores.FONDO_OSCURO);
        exterior.add(construirCard());
        return exterior;
    }

    private JPanel construirCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Colores.FONDO_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(32, 32, 32, 32)
        ));

        campoNombre = new JTextField();
        campoRfid   = new JTextField();
        Componentes.estilizarCampo(campoNombre);
        Componentes.estilizarCampo(campoRfid);

        cmbGrupo = new JComboBox<>();
        cmbGrupo.addItem(null);
        for (Grupo g : grupoDAO.listarTodos()) cmbGrupo.addItem(g);
        estilizarCombo(cmbGrupo, "Sin grupo");

        // Apellido del alumno actual (última palabra del nombre) para detectar hermanos
        String apellidoActual = "";
        if (alumno != null) {
            String nombreCompleto = alumno.getNombre().trim();
            int ultimoEspacio = nombreCompleto.lastIndexOf(' ');
            apellidoActual = (ultimoEspacio >= 0)
                ? nombreCompleto.substring(ultimoEspacio + 1).toLowerCase()
                : nombreCompleto.toLowerCase();
        }

        cmbTutor = new JComboBox<>();
        cmbTutor.addItem(null);
        for (Tutor t : tutorDAO.listarTodos()) {
            Alumno alumnoDelTutor = alumnoDAO.buscarPorTutorId(t.getId());
            if (alumnoDelTutor == null) {
                // Tutor sin alumno asignado: siempre disponible
                cmbTutor.addItem(t);
            } else if (alumno != null && alumnoDelTutor.getId() == alumno.getId()) {
                // El tutor ya está asignado a este mismo alumno (modo edición)
                cmbTutor.addItem(t);
            } else if (!apellidoActual.isEmpty()) {
                // Verificar si el alumno asignado comparte apellido (hermanos)
                String nombreAsignado = alumnoDelTutor.getNombre().trim();
                int lastSpace = nombreAsignado.lastIndexOf(' ');
                String apellidoAsignado = (lastSpace >= 0)
                    ? nombreAsignado.substring(lastSpace + 1).toLowerCase()
                    : nombreAsignado.toLowerCase();
                if (apellidoActual.equals(apellidoAsignado)) {
                    cmbTutor.addItem(t);
                }
            }
            // Tutor con alumno asignado de diferente apellido: no se muestra
        }
        estilizarCombo(cmbTutor, "Sin tutor");

        lblError = new JLabel(" ");
        lblError.setFont(Colores.FUENTE_PEQUEÑA);
        lblError.setForeground(Colores.ROJO);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblError.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JButton btnCancelar = Componentes.botonSecundario("Cancelar");
        JButton btnGuardar  = Componentes.botonPrimario("Guardar");
        btnCancelar.addActionListener(e -> mainFrame.irAAlumnos());
        btnGuardar.addActionListener(e  -> guardar());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setBackground(Colores.FONDO_PANEL);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        card.add(crearEtiqueta("Nombre completo *"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoNombre);
        card.add(Box.createVerticalStrut(14));
        card.add(crearEtiqueta("RFID UID  (dejar vacío si no se usa lector)"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoRfid);
        card.add(Box.createVerticalStrut(14));
        card.add(crearEtiqueta("Grupo"));
        card.add(Box.createVerticalStrut(5));
        card.add(cmbGrupo);
        card.add(Box.createVerticalStrut(14));
        card.add(crearEtiqueta("Tutor responsable"));
        card.add(Box.createVerticalStrut(5));
        card.add(cmbTutor);
        card.add(Box.createVerticalStrut(12));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        card.add(panelBotones);

        return card;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void llenarFormulario() {
        campoNombre.setText(alumno.getNombre());
        campoRfid.setText(alumno.getRfidUid() != null ? alumno.getRfidUid() : "");

        if (alumno.getGrupoId() > 0) {
            for (int i = 0; i < cmbGrupo.getItemCount(); i++) {
                Grupo g = cmbGrupo.getItemAt(i);
                if (g != null && g.getId() == alumno.getGrupoId()) {
                    cmbGrupo.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (alumno.getTutorId() > 0) {
            for (int i = 0; i < cmbTutor.getItemCount(); i++) {
                Tutor t = cmbTutor.getItemAt(i);
                if (t != null && t.getId() == alumno.getTutorId()) {
                    cmbTutor.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            lblError.setText("El nombre es obligatorio.");
            return;
        }

        String rfid = campoRfid.getText().trim();
        if (rfid.isEmpty()) rfid = null;

        int grupoId = 0;
        Grupo grupoSel = (Grupo) cmbGrupo.getSelectedItem();
        if (grupoSel != null) grupoId = grupoSel.getId();

        int tutorId = 0;
        Tutor tutorSel = (Tutor) cmbTutor.getSelectedItem();
        if (tutorSel != null) tutorId = tutorSel.getId();

        boolean exito;
        if (alumno == null) {
            exito = alumnoDAO.insertar(new Alumno(nombre, rfid, grupoId, tutorId));
        } else {
            alumno.setNombre(nombre);
            alumno.setRfidUid(rfid);
            alumno.setGrupoId(grupoId);
            alumno.setTutorId(tutorId);
            exito = alumnoDAO.actualizar(alumno);
        }

        if (exito) {
            mainFrame.irAAlumnos();
        } else {
            lblError.setText("Error al guardar. Verifica que el RFID no esté duplicado.");
        }
    }

    // ── Auxiliares de estilo ─────────────────────────

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private <T> void estilizarCombo(JComboBox<T> combo, final String textoVacio) {
        combo.setBackground(Colores.FONDO_OSCURO);
        combo.setForeground(Colores.TEXTO_CLARO);
        combo.setFont(Colores.FUENTE_NORMAL);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(0, 4, 0, 4)
        ));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? textoVacio : value.toString());
                setBackground(isSelected ? Colores.AZUL_PRIMARIO : Colores.FONDO_OSCURO);
                setForeground(Colores.TEXTO_CLARO);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }
}

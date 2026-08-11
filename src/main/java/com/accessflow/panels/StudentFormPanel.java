package com.accessflow.panels;

import com.accessflow.dao.AlumnoDAO;
import com.accessflow.dao.GrupoDAO;
import com.accessflow.model.Alumno;
import com.accessflow.model.Grupo;
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

    private JTextField       campoNombre;
    private JTextField       campoRfid;
    private JComboBox<Grupo> cmbGrupo;
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

        // El tutor se gestiona desde el panel de tutores; se preserva el existente
        int tutorId = (alumno != null) ? alumno.getTutorId() : 0;

        boolean exito;
        if (alumno == null) {
            exito = alumnoDAO.insertar(new Alumno(nombre, rfid, grupoId, tutorId));
        } else {
            alumno.setNombre(nombre);
            alumno.setRfidUid(rfid);
            alumno.setGrupoId(grupoId);
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

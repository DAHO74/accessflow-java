package com.accessflow.panels;

import com.accessflow.dao.GrupoDAO;
import com.accessflow.model.Grupo;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GroupFormPanel extends JPanel {

    private final MainFrame mainFrame;
    private final Grupo     grupo;     // null cuando se crea un nuevo grupo

    private final GrupoDAO grupoDAO = new GrupoDAO();

    private JTextField       campoNombre;
    private JTextField       campoGrado;
    private JComboBox<String> cmbTurno;
    private JLabel           lblError;

    public GroupFormPanel(MainFrame mainFrame, Grupo grupo) {
        this.mainFrame = mainFrame;
        this.grupo     = grupo;

        setLayout(new BorderLayout(0, 0));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);

        if (grupo != null) {
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

        String textoTitulo = (grupo == null) ? "Nuevo grupo" : "Editar grupo";
        JLabel titulo = new JLabel(textoTitulo);
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);

        JButton btnVolver = Componentes.botonSecundario("← Volver");
        btnVolver.addActionListener(e -> mainFrame.irAGrupos());

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
        campoGrado  = new JTextField();
        Componentes.estilizarCampo(campoNombre);
        Componentes.estilizarCampo(campoGrado);

        // ComboBox con los turnos más comunes
        String[] opciones = {"", "Matutino", "Vespertino"};
        cmbTurno = new JComboBox<>(opciones);
        estilizarComboBox(cmbTurno);

        lblError = new JLabel(" ");
        lblError.setFont(Colores.FUENTE_PEQUEÑA);
        lblError.setForeground(Colores.ROJO);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblError.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JButton btnCancelar = Componentes.botonSecundario("Cancelar");
        JButton btnGuardar  = Componentes.botonPrimario("Guardar");
        btnCancelar.addActionListener(e -> mainFrame.irAGrupos());
        btnGuardar.addActionListener(e  -> guardar());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setBackground(Colores.FONDO_PANEL);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);

        card.add(crearEtiqueta("Nombre del grupo *"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoNombre);
        card.add(Box.createVerticalStrut(16));
        card.add(crearEtiqueta("Grado  (ej. 1°, 2°, 3°)"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoGrado);
        card.add(Box.createVerticalStrut(16));
        card.add(crearEtiqueta("Turno"));
        card.add(Box.createVerticalStrut(5));
        card.add(cmbTurno);
        card.add(Box.createVerticalStrut(14));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        card.add(panelBotones);

        return card;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void llenarFormulario() {
        campoNombre.setText(grupo.getNombre());
        campoGrado.setText(grupo.getGrado() != null ? grupo.getGrado() : "");

        // Seleccionar el turno en el ComboBox
        String turno = grupo.getTurno();
        if (turno != null) {
            for (int i = 0; i < cmbTurno.getItemCount(); i++) {
                if (cmbTurno.getItemAt(i).equals(turno)) {
                    cmbTurno.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void guardar() {
        String nombre = campoNombre.getText().trim();
        if (nombre.isEmpty()) {
            lblError.setText("El nombre del grupo es obligatorio.");
            return;
        }

        String grado = campoGrado.getText().trim();
        if (grado.isEmpty()) grado = null;

        String turno = (String) cmbTurno.getSelectedItem();
        if (turno != null && turno.isEmpty()) turno = null;

        boolean exito;
        if (grupo == null) {
            Grupo nuevo = new Grupo(nombre, grado, turno);
            exito = grupoDAO.insertar(nuevo);
        } else {
            grupo.setNombre(nombre);
            grupo.setGrado(grado);
            grupo.setTurno(turno);
            exito = grupoDAO.actualizar(grupo);
        }

        if (exito) {
            mainFrame.irAGrupos();
        } else {
            lblError.setText("Error al guardar el grupo. Intenta de nuevo.");
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

    private void estilizarComboBox(JComboBox<String> combo) {
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
                String texto = (value == null || value.toString().isEmpty())
                               ? "Seleccionar turno..." : value.toString();
                setText(texto);
                setBackground(isSelected ? Colores.AZUL_PRIMARIO : Colores.FONDO_OSCURO);
                setForeground(Colores.TEXTO_CLARO);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }
}

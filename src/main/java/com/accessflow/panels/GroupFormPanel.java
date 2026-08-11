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

    private static final String[] LETRAS = {"A","B","C","D","E","F","G","H","I","J","K","L"};
    private static final String[] GRADOS = {"1°","2°","3°"};

    private final MainFrame mainFrame;
    private final Grupo     grupo;

    private final GrupoDAO grupoDAO = new GrupoDAO();

    private JComboBox<String> cmbLetra;
    private JComboBox<String> cmbGrado;
    private JLabel            lblTurno;
    private JLabel            lblError;

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

        cmbLetra = new JComboBox<>(LETRAS);
        estilizarComboBox(cmbLetra, "Seleccionar letra...");
        cmbLetra.setSelectedIndex(-1);
        cmbLetra.addActionListener(e -> actualizarTurno());

        cmbGrado = new JComboBox<>(GRADOS);
        estilizarComboBox(cmbGrado, "Seleccionar grado...");
        cmbGrado.setSelectedIndex(-1);

        // Turno: solo lectura, se rellena automáticamente según la letra
        lblTurno = new JLabel("—");
        lblTurno.setFont(Colores.FUENTE_NORMAL);
        lblTurno.setForeground(Colores.TEXTO_CLARO);
        lblTurno.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTurno.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x2D, 0x3A, 0x4A)),
            new EmptyBorder(8, 10, 8, 10)
        ));
        lblTurno.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        lblTurno.setOpaque(true);
        lblTurno.setBackground(new Color(0x1A, 0x24, 0x2F));

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

        card.add(crearEtiqueta("Letra del grupo *"));
        card.add(Box.createVerticalStrut(5));
        card.add(cmbLetra);
        card.add(Box.createVerticalStrut(16));
        card.add(crearEtiqueta("Grado *"));
        card.add(Box.createVerticalStrut(5));
        card.add(cmbGrado);
        card.add(Box.createVerticalStrut(16));
        card.add(crearEtiqueta("Turno  (se asigna automáticamente)"));
        card.add(Box.createVerticalStrut(5));
        card.add(lblTurno);
        card.add(Box.createVerticalStrut(14));
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        card.add(panelBotones);

        return card;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void actualizarTurno() {
        String letra = (String) cmbLetra.getSelectedItem();
        if (letra == null) {
            lblTurno.setText("—");
            return;
        }
        boolean esMatutino = "ABCDEF".contains(letra);
        lblTurno.setText(esMatutino ? "Matutino" : "Vespertino");
        lblTurno.setForeground(esMatutino ? Colores.VERDE : Colores.AZUL_PRIMARIO);
    }

    private void llenarFormulario() {
        // Seleccionar letra
        String nombre = grupo.getNombre();
        if (nombre != null) {
            for (int i = 0; i < LETRAS.length; i++) {
                if (LETRAS[i].equals(nombre.toUpperCase())) {
                    cmbLetra.setSelectedIndex(i);
                    break;
                }
            }
        }
        // Seleccionar grado
        String grado = grupo.getGrado();
        if (grado != null) {
            for (int i = 0; i < GRADOS.length; i++) {
                if (GRADOS[i].equals(grado)) {
                    cmbGrado.setSelectedIndex(i);
                    break;
                }
            }
        }
        actualizarTurno();
    }

    private void guardar() {
        String letra = (String) cmbLetra.getSelectedItem();
        if (letra == null) {
            lblError.setText("Selecciona la letra del grupo.");
            return;
        }
        String grado = (String) cmbGrado.getSelectedItem();
        if (grado == null) {
            lblError.setText("Selecciona el grado.");
            return;
        }

        String turno = lblTurno.getText();
        if ("—".equals(turno)) turno = null;

        int idActual = (grupo == null) ? 0 : grupo.getId();
        if (grupoDAO.existeConNombre(letra, idActual)) {
            lblError.setText("Ya existe un grupo con esa letra.");
            return;
        }

        boolean exito;
        if (grupo == null) {
            exito = grupoDAO.insertar(new Grupo(letra, grado, turno));
        } else {
            grupo.setNombre(letra);
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

    private void estilizarComboBox(JComboBox<String> combo, String placeholder) {
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
                String texto = (value == null) ? placeholder : value.toString();
                setText(texto);
                setBackground(isSelected ? Colores.AZUL_PRIMARIO : Colores.FONDO_OSCURO);
                setForeground(value == null ? Colores.TEXTO_GRIS : Colores.TEXTO_CLARO);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }
}

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
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
    private JLabel           lblError;

    // Sección tutor
    private JTextField              campoBuscarTutor;
    private JList<Tutor>            listaTutores;
    private DefaultListModel<Tutor> modeloTutores;
    private JLabel                  lblTutorSel;
    private Tutor                   tutorSeleccionado;

    private final List<Tutor> todosTutores = new ArrayList<>();

    public StudentFormPanel(MainFrame mainFrame, Alumno alumno) {
        this.mainFrame = mainFrame;
        this.alumno    = alumno;

        setLayout(new BorderLayout(0, 0));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);

        cargarTutores();

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
        JPanel contenedor = new JPanel(new BorderLayout(16, 0));
        contenedor.setBackground(Colores.FONDO_OSCURO);
        contenedor.add(construirCardDatos(),  BorderLayout.WEST);
        contenedor.add(construirCardTutor(),  BorderLayout.CENTER);
        return contenedor;
    }

    // ── Card izquierdo: datos del alumno ──────────────

    private JPanel construirCardDatos() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Colores.FONDO_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(32, 32, 32, 32)
        ));
        card.setPreferredSize(new Dimension(360, 0));

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
        card.add(Box.createVerticalGlue());
        card.add(lblError);
        card.add(Box.createVerticalStrut(8));
        card.add(panelBotones);

        return card;
    }

    // ── Card derecho: selector de tutor ──────────────

    private JPanel construirCardTutor() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Colores.FONDO_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(32, 32, 32, 32)
        ));

        // Cabecera
        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        cabecera.setBackground(Colores.FONDO_PANEL);

        JLabel lblTit = new JLabel("Tutor / Padre de familia");
        lblTit.setFont(Colores.FUENTE_BOLD);
        lblTit.setForeground(Colores.TEXTO_CLARO);

        lblTutorSel = new JLabel("Ninguno asignado");
        lblTutorSel.setFont(Colores.FUENTE_PEQUEÑA);
        lblTutorSel.setForeground(Colores.TEXTO_GRIS);

        campoBuscarTutor = new JTextField();
        campoBuscarTutor.setBackground(Colores.FONDO_OSCURO);
        campoBuscarTutor.setForeground(Colores.TEXTO_CLARO);
        campoBuscarTutor.setCaretColor(Colores.TEXTO_CLARO);
        campoBuscarTutor.setFont(Colores.FUENTE_NORMAL);
        campoBuscarTutor.setToolTipText("Buscar por nombre o correo...");
        campoBuscarTutor.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
        campoBuscarTutor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrarTutores(); }
            public void removeUpdate(DocumentEvent e)  { filtrarTutores(); }
            public void changedUpdate(DocumentEvent e) { filtrarTutores(); }
        });

        cabecera.add(lblTit);
        cabecera.add(Box.createVerticalStrut(4));
        cabecera.add(lblTutorSel);
        cabecera.add(Box.createVerticalStrut(10));
        cabecera.add(campoBuscarTutor);

        // Lista de tutores
        modeloTutores = new DefaultListModel<>();
        listaTutores  = new JList<>(modeloTutores);
        listaTutores.setBackground(Colores.FONDO_OSCURO);
        listaTutores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTutores.setFixedCellHeight(46);
        listaTutores.setCellRenderer(new TutorCellRenderer());
        listaTutores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarTutor(listaTutores.getSelectedValue());
        });

        JScrollPane scroll = new JScrollPane(listaTutores);
        scroll.setBorder(BorderFactory.createLineBorder(Colores.BORDE));
        scroll.getViewport().setBackground(Colores.FONDO_OSCURO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // Pie: botón crear tutor nuevo
        JButton btnNuevoTutor = Componentes.botonSecundario("+ Crear nuevo tutor");
        btnNuevoTutor.addActionListener(e -> abrirDialogoNuevoTutor());

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pie.setBackground(Colores.FONDO_PANEL);
        pie.add(btnNuevoTutor);

        card.add(cabecera, BorderLayout.NORTH);
        card.add(scroll,   BorderLayout.CENTER);
        card.add(pie,      BorderLayout.SOUTH);
        return card;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA — Tutores
    // ═══════════════════════════════════════════════

    private void cargarTutores() {
        todosTutores.clear();
        todosTutores.addAll(tutorDAO.listarTodos());
        filtrarTutores();
    }

    private void filtrarTutores() {
        String txt = campoBuscarTutor == null ? "" : campoBuscarTutor.getText().trim().toLowerCase();
        modeloTutores.clear();
        for (Tutor t : todosTutores) {
            if (txt.isEmpty() || coincide(t, txt)) modeloTutores.addElement(t);
        }
    }

    private boolean coincide(Tutor t, String txt) {
        if (t.getNombre() != null && t.getNombre().toLowerCase().contains(txt)) return true;
        if (t.getEmail()  != null && t.getEmail().toLowerCase().contains(txt))  return true;
        return false;
    }

    private void seleccionarTutor(Tutor t) {
        tutorSeleccionado = t;
        if (t == null) {
            lblTutorSel.setText("Ninguno asignado");
            lblTutorSel.setForeground(Colores.TEXTO_GRIS);
        } else {
            String info = t.getNombre();
            if (t.getParentesco() != null && !t.getParentesco().isEmpty())
                info += "  (" + t.getParentesco() + ")";
            lblTutorSel.setText("✓ " + info);
            lblTutorSel.setForeground(Colores.VERDE);
        }
    }

    private void abrirDialogoNuevoTutor() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Nuevo tutor",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(440, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Colores.FONDO_OSCURO);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Colores.FONDO_PANEL);
        form.setBorder(new EmptyBorder(24, 24, 24, 24));

        JTextField fNombre = new JTextField(); Componentes.estilizarCampo(fNombre);
        JTextField fEmail  = new JTextField(); Componentes.estilizarCampo(fEmail);

        // Teléfono con filtro: solo dígitos y guiones, máx 10 dígitos / 12 chars total
        JTextField fTelefono = new JTextField() {
            @Override
            protected void processKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    char c = e.getKeyChar();
                    if (c >= ' ') {
                        if (!Character.isDigit(c) && c != '-') return;
                        int ss = getSelectionStart(), se = getSelectionEnd();
                        String cur  = getText();
                        String next = cur.substring(0, ss) + c + cur.substring(se);
                        long digits = next.chars().filter(Character::isDigit).count();
                        if ((Character.isDigit(c) && digits > 10) || next.length() > 12) return;
                    }
                }
                super.processKeyEvent(e);
            }
        };
        Componentes.estilizarCampo(fTelefono);

        String[] parentescos = {"Madre", "Padre", "Otro"};
        JComboBox<String> cmbP = new JComboBox<>(parentescos);
        cmbP.setBackground(Colores.FONDO_OSCURO);
        cmbP.setForeground(Colores.TEXTO_CLARO);
        cmbP.setFont(Colores.FUENTE_NORMAL);
        cmbP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cmbP.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Campo "Otro" con CardLayout — sin setVisible, sin revalidate
        JTextField fOtroParentesco = new JTextField();
        Componentes.estilizarCampo(fOtroParentesco);

        JPanel otroCard = new JPanel(new BorderLayout(0, 4));
        otroCard.setBackground(Colores.FONDO_PANEL);
        otroCard.add(etiquetaDialog("Especificar parentesco *"), BorderLayout.NORTH);
        otroCard.add(fOtroParentesco, BorderLayout.CENTER);

        JPanel vacioCard = new JPanel(); vacioCard.setOpaque(false);

        JPanel cardOtro = new JPanel(new CardLayout());
        cardOtro.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardOtro.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        cardOtro.setOpaque(false);
        cardOtro.add(vacioCard, "vacio");
        cardOtro.add(otroCard,  "otro");

        cmbP.addActionListener(e -> {
            boolean esOtro = "Otro".equals(cmbP.getSelectedItem());
            ((CardLayout) cardOtro.getLayout()).show(cardOtro, esOtro ? "otro" : "vacio");
            if (!esOtro) fOtroParentesco.setText("");
        });

        JLabel lblErr = new JLabel(" ");
        lblErr.setFont(Colores.FUENTE_PEQUEÑA);
        lblErr.setForeground(Colores.ROJO);
        lblErr.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(etiquetaDialog("Nombre completo *")); form.add(Box.createVerticalStrut(4));
        form.add(fNombre);   form.add(Box.createVerticalStrut(10));
        form.add(etiquetaDialog("Correo electrónico")); form.add(Box.createVerticalStrut(4));
        form.add(fEmail);    form.add(Box.createVerticalStrut(10));
        form.add(etiquetaDialog("Teléfono  (10 dígitos, guiones permitidos)")); form.add(Box.createVerticalStrut(4));
        form.add(fTelefono); form.add(Box.createVerticalStrut(10));
        form.add(etiquetaDialog("Parentesco")); form.add(Box.createVerticalStrut(4));
        form.add(cmbP);      form.add(Box.createVerticalStrut(8));
        form.add(cardOtro);  form.add(Box.createVerticalStrut(8));
        form.add(lblErr);

        JButton btnGuardar  = Componentes.botonPrimario("Guardar");
        JButton btnCancelar = Componentes.botonSecundario("Cancelar");

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            String nombre = fNombre.getText().trim();
            if (nombre.isEmpty()) { lblErr.setText("El nombre es obligatorio."); return; }

            String telefono = fTelefono.getText().trim();
            if (!telefono.isEmpty()) {
                String soloDigitos = telefono.replaceAll("-", "");
                if (!soloDigitos.matches("\\d{10}")) {
                    lblErr.setText("Teléfono: exactamente 10 dígitos (guiones opcionales)."); return;
                }
            }

            String parentesco = (String) cmbP.getSelectedItem();
            if ("Otro".equals(parentesco)) {
                parentesco = fOtroParentesco.getText().trim();
                if (parentesco.isEmpty()) { lblErr.setText("Especifica el parentesco."); return; }
            }

            Tutor nuevo = new Tutor(nombre,
                fEmail.getText().trim().isEmpty() ? null : fEmail.getText().trim(),
                telefono.isEmpty() ? null : telefono);
            nuevo.setParentesco(parentesco);

            int id = tutorDAO.insertarConId(nuevo);
            if (id > 0) {
                cargarTutores();
                for (int i = 0; i < modeloTutores.getSize(); i++) {
                    if (modeloTutores.getElementAt(i).getId() == id) {
                        listaTutores.setSelectedIndex(i);
                        listaTutores.ensureIndexIsVisible(i);
                        break;
                    }
                }
                dialog.dispose();
            } else {
                lblErr.setText("Error al guardar. Intenta de nuevo.");
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setBackground(Colores.FONDO_PANEL);
        botones.add(btnCancelar);
        botones.add(btnGuardar);

        dialog.add(form,    BorderLayout.CENTER);
        dialog.add(botones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA — Formulario
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
            Tutor t = tutorDAO.buscarPorId(alumno.getTutorId());
            if (t != null) {
                tutorSeleccionado = t;
                for (int i = 0; i < modeloTutores.getSize(); i++) {
                    if (modeloTutores.getElementAt(i).getId() == t.getId()) {
                        listaTutores.setSelectedIndex(i);
                        listaTutores.ensureIndexIsVisible(i);
                        break;
                    }
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

        int tutorId = (tutorSeleccionado != null) ? tutorSeleccionado.getId() : 0;

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

    // ═══════════════════════════════════════════════
    //  RENDERER — lista de tutores
    // ═══════════════════════════════════════════════

    private class TutorCellRenderer implements ListCellRenderer<Tutor> {

        private final JPanel celda   = new JPanel(new BorderLayout(0, 2));
        private final JLabel lblNom  = new JLabel();
        private final JLabel lblInfo = new JLabel();

        TutorCellRenderer() {
            JPanel textos = new JPanel();
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
            textos.setOpaque(false);
            textos.add(lblNom);
            textos.add(lblInfo);
            celda.add(textos, BorderLayout.CENTER);
            celda.setBorder(new EmptyBorder(6, 12, 6, 12));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Tutor> list,
                Tutor t, int index, boolean isSelected, boolean cellHasFocus) {
            lblNom.setText(t.getNombre());
            lblNom.setFont(Colores.FUENTE_BOLD);

            String email = (t.getEmail() != null && !t.getEmail().isEmpty()) ? t.getEmail() : "Sin correo";
            String par   = (t.getParentesco() != null && !t.getParentesco().isEmpty()) ? "  ·  " + t.getParentesco() : "";
            lblInfo.setText(email + par);
            lblInfo.setFont(Colores.FUENTE_PEQUEÑA);

            Color fondo = isSelected
                ? new Color(0x1E, 0x3A, 0x5A)
                : (index % 2 == 0 ? Colores.FONDO_OSCURO : new Color(0x16, 0x20, 0x2C));

            celda.setBackground(fondo);
            lblNom.setForeground(isSelected ? Colores.AZUL_PRIMARIO : Colores.TEXTO_CLARO);
            lblInfo.setForeground(Colores.TEXTO_GRIS);
            return celda;
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

    private JLabel etiquetaDialog(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
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

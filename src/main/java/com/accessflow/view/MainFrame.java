package com.accessflow.view;

import com.accessflow.panels.AttendanceHistoryPanel;
import com.accessflow.panels.AttendancePanel;
import com.accessflow.panels.DashboardPanel;
import com.accessflow.panels.GroupsPanel;
import com.accessflow.panels.MessagingPanel;
import com.accessflow.panels.ProfilePanel;
import com.accessflow.panels.SettingsPanel;
import com.accessflow.panels.StudentsPanel;
import com.accessflow.panels.TutorsPanel;
import com.accessflow.service.SessionManager;
import com.accessflow.util.Colores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private JPanel  areaContenido;
    private JButton botonActivo;

    // Referencias a todos los botones del sidebar para manejar el estado activo
    private JButton btnDashboard;
    private JButton btnAsistencia;
    private JButton btnHistorial;
    private JButton btnAlumnos;
    private JButton btnGrupos;
    private JButton btnTutores;
    private JButton btnMensajeria;
    private JButton btnPerfil;
    private JButton btnConfiguracion;

    public MainFrame() {
        configurarVentana();
        construirUI();
        irADashboard(); // pantalla que se muestra al iniciar
    }

    private void configurarVentana() {
        setTitle("AccessFlow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Colores.FONDO_OSCURO);

        areaContenido = new JPanel(new BorderLayout());
        areaContenido.setBackground(Colores.FONDO_OSCURO);
        areaContenido.setBorder(new EmptyBorder(20, 24, 20, 24));

        add(construirSidebar(), BorderLayout.WEST);
        add(areaContenido,      BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════
    //  SIDEBAR
    // ═══════════════════════════════════════════════

    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Colores.SIDEBAR_FONDO);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Colores.BORDE));

        sidebar.add(construirSidebarTop(),    BorderLayout.NORTH);
        sidebar.add(construirSidebarNav(),    BorderLayout.CENTER);
        sidebar.add(construirSidebarBottom(), BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel construirSidebarTop() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Colores.SIDEBAR_FONDO);

        // Logo y nombre de la aplicación
        JLabel logo = new JLabel("AF");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        logo.setBackground(Colores.AZUL_PRIMARIO);
        logo.setOpaque(true);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setBorder(new EmptyBorder(5, 14, 5, 14));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appNombre = new JLabel("AccessFlow");
        appNombre.setFont(new Font("Segoe UI", Font.BOLD, 15));
        appNombre.setForeground(Colores.TEXTO_CLARO);
        appNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Información del usuario en sesión
        String nombre = SessionManager.getUsuario().getNombre();
        String email  = SessionManager.getUsuario().getEmail();
        String rol    = SessionManager.getUsuario().getRol();

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(Colores.FUENTE_BOLD);
        lblNombre.setForeground(Colores.TEXTO_CLARO);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblEmail = new JLabel(email);
        lblEmail.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEmail.setForeground(Colores.TEXTO_GRIS);
        lblEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Badge de rol: azul para ADMIN, verde para PROFESOR
        Color colorRol = SessionManager.esAdmin()
                ? Colores.AZUL_PRIMARIO
                : new Color(0x10, 0xB9, 0x81);

        JLabel lblRol = new JLabel(rol);
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblRol.setForeground(Color.WHITE);
        lblRol.setBackground(colorRol);
        lblRol.setOpaque(true);
        lblRol.setBorder(new EmptyBorder(2, 8, 2, 8));
        lblRol.setAlignmentX(Component.CENTER_ALIGNMENT);

        top.add(Box.createVerticalStrut(20));
        top.add(logo);
        top.add(Box.createVerticalStrut(6));
        top.add(appNombre);
        top.add(Box.createVerticalStrut(14));
        top.add(crearLineaSidebar());
        top.add(Box.createVerticalStrut(12));
        top.add(lblNombre);
        top.add(Box.createVerticalStrut(3));
        top.add(lblEmail);
        top.add(Box.createVerticalStrut(6));
        top.add(lblRol);
        top.add(Box.createVerticalStrut(10));
        top.add(crearLineaSidebar());

        return top;
    }

    private JPanel construirSidebarNav() {
        btnDashboard  = crearNavBtn("  Inicio");
        btnAsistencia = crearNavBtn("  Asistencia");
        btnHistorial  = crearNavBtn("  Historial");
        btnAlumnos    = crearNavBtn("  Alumnos");
        btnGrupos     = crearNavBtn("  Grupos");
        btnTutores    = crearNavBtn("  Tutores");
        btnMensajeria = crearNavBtn("  Mensajería");

        btnDashboard.addActionListener(e  -> irADashboard());
        btnAsistencia.addActionListener(e -> irAAsistencia());
        btnHistorial.addActionListener(e  -> irAHistorial());
        btnAlumnos.addActionListener(e    -> irAAlumnos());
        btnGrupos.addActionListener(e     -> irAGrupos());
        btnTutores.addActionListener(e    -> irATutores());
        btnMensajeria.addActionListener(e -> irAMensajeria());

        // Los botones de gestión solo son visibles para el administrador
        boolean esAdmin = SessionManager.esAdmin();
        btnAlumnos.setVisible(esAdmin);
        btnGrupos.setVisible(esAdmin);
        btnTutores.setVisible(esAdmin);

        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(Colores.SIDEBAR_FONDO);
        nav.setBorder(new EmptyBorder(8, 0, 8, 0));

        nav.add(btnDashboard);
        nav.add(btnAsistencia);
        nav.add(btnHistorial);
        nav.add(Box.createVerticalStrut(4));
        nav.add(crearEtiquetaSeccion("GESTIÓN"));
        nav.add(btnAlumnos);
        nav.add(btnGrupos);
        nav.add(btnTutores);
        nav.add(Box.createVerticalStrut(4));
        nav.add(crearEtiquetaSeccion("COMUNICACIÓN"));
        nav.add(btnMensajeria);
        nav.add(Box.createVerticalGlue()); // empuja todo hacia arriba

        return nav;
    }

    private JPanel construirSidebarBottom() {
        btnPerfil        = crearNavBtn("  Mi Perfil");
        btnConfiguracion = crearNavBtn("  Configuración");
        JButton btnLogout = crearNavBtn("  Cerrar Sesión");

        btnPerfil.addActionListener(e        -> irAPerfil());
        btnConfiguracion.addActionListener(e -> irAConfiguracion());
        btnLogout.addActionListener(e        -> cerrarSesion());

        // Solo el admin puede acceder a configuración
        btnConfiguracion.setVisible(SessionManager.esAdmin());
        // El botón de cerrar sesión tiene color especial
        btnLogout.setForeground(Colores.ROJO);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBackground(Colores.SIDEBAR_FONDO);
        bottom.setBorder(new EmptyBorder(0, 0, 8, 0));

        bottom.add(crearLineaSidebar());
        bottom.add(btnPerfil);
        bottom.add(btnConfiguracion);
        bottom.add(btnLogout);

        return bottom;
    }

    // ═══════════════════════════════════════════════
    //  NAVEGACIÓN
    // ═══════════════════════════════════════════════

    public void irADashboard()     { setActive(btnDashboard);     mostrarPanel(new DashboardPanel()); }
    public void irAAsistencia()    { setActive(btnAsistencia);    mostrarPanel(new AttendancePanel(this)); }
    public void irAHistorial()     { setActive(btnHistorial);     mostrarPanel(new AttendanceHistoryPanel(this)); }
    public void irAAlumnos()       { setActive(btnAlumnos);       mostrarPanel(new StudentsPanel(this)); }
    public void irAGrupos()        { setActive(btnGrupos);        mostrarPanel(new GroupsPanel(this)); }
    public void irATutores()       { setActive(btnTutores);       mostrarPanel(new TutorsPanel(this)); }
    public void irAMensajeria()    { setActive(btnMensajeria);    mostrarPanel(new MessagingPanel(this)); }
    public void irAPerfil()        { setActive(btnPerfil);        mostrarPanel(new ProfilePanel(this)); }
    public void irAConfiguracion() { setActive(btnConfiguracion); mostrarPanel(new SettingsPanel(this)); }

    // Intercambia el JPanel visible en el área de contenido central
    public void mostrarPanel(JPanel panel) {
        areaContenido.removeAll();
        areaContenido.add(panel, BorderLayout.CENTER);
        areaContenido.revalidate();
        areaContenido.repaint();
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de que deseas cerrar sesión?",
            "Cerrar sesión", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            SessionManager.cerrarSesion();
            dispose();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        }
    }

    // Panel temporal para módulos que aún no están implementados
    private JPanel proximamente(String modulo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Colores.FONDO_OSCURO);
        JLabel lbl = new JLabel("Módulo \"" + modulo + "\" — próxima etapa", SwingConstants.CENTER);
        lbl.setFont(Colores.FUENTE_TITULO);
        lbl.setForeground(Colores.TEXTO_GRIS);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // ═══════════════════════════════════════════════
    //  MÉTODOS AUXILIARES DE ESTILO
    // ═══════════════════════════════════════════════

    // Resalta el botón activo y restaura el anterior
    private void setActive(JButton btn) {
        if (botonActivo != null) {
            botonActivo.setBackground(Colores.SIDEBAR_FONDO);
            botonActivo.setForeground(Colores.TEXTO_GRIS);
        }
        botonActivo = btn;
        btn.setBackground(Colores.SIDEBAR_ACTIVO);
        btn.setForeground(Color.WHITE);
    }

    private JButton crearNavBtn(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(Colores.FUENTE_NAV);
        btn.setForeground(Colores.TEXTO_GRIS);
        btn.setBackground(Colores.SIDEBAR_FONDO);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Efecto visual al pasar el cursor por encima
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != botonActivo) btn.setBackground(Colores.SIDEBAR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != botonActivo) btn.setBackground(Colores.SIDEBAR_FONDO);
            }
        });

        return btn;
    }

    private JLabel crearEtiquetaSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(new Color(0x47, 0x55, 0x69));
        lbl.setBorder(new EmptyBorder(6, 16, 4, 16));
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return lbl;
    }

    private Component crearLineaSidebar() {
        JSeparator sep = new JSeparator();
        sep.setForeground(Colores.BORDE);
        sep.setBackground(Colores.BORDE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}

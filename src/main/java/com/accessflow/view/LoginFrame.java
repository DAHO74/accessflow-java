package com.accessflow.view;

import com.accessflow.dao.UsuarioDAO;
import com.accessflow.model.Usuario;
import com.accessflow.service.AuthService;
import com.accessflow.service.SessionManager;
import com.accessflow.util.Colores;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private JTextField     campoEmail;
    private JPasswordField campoPassword;
    private JLabel         etiquetaError;
    private JButton        btnLogin;

    public LoginFrame() {
        configurarVentana();
        construirUI();
        verificarPrimerUso();
    }

    private void configurarVentana() {
        setTitle("AccessFlow - Iniciar Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 540);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirUI() {
        // Panel exterior: fondo oscuro, centra la card
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(Colores.FONDO_OSCURO);
        fondo.add(construirCard());

        setContentPane(fondo);
        // Enter en cualquier campo activa el botón de login
        getRootPane().setDefaultButton(btnLogin);
    }

    private JPanel construirCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Colores.FONDO_PANEL);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(40, 40, 40, 40)
        ));
        card.setPreferredSize(new Dimension(360, 460));

        // ── Logo ──────────────────────────────────────
        JLabel logo = new JLabel("AF");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(Color.WHITE);
        logo.setBackground(Colores.AZUL_PRIMARIO);
        logo.setOpaque(true);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setBorder(new EmptyBorder(6, 18, 6, 18));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("AccessFlow");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Sistema de Asistencia Escolar");
        subtitulo.setFont(Colores.FUENTE_PEQUEÑA);
        subtitulo.setForeground(Colores.TEXTO_GRIS);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Campos ────────────────────────────────────
        campoEmail    = new JTextField();
        campoPassword = new JPasswordField();
        estilizarCampo(campoEmail);
        estilizarCampo(campoPassword);

        // Enter en el campo de contraseña también dispara el login
        campoPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) iniciarSesion();
            }
        });

        etiquetaError = new JLabel(" ");
        etiquetaError.setFont(Colores.FUENTE_PEQUEÑA);
        etiquetaError.setForeground(Colores.ROJO);
        etiquetaError.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin = crearBotonPrimario("Iniciar Sesión");
        btnLogin.addActionListener(e -> iniciarSesion());

        // ── Ensamblar ─────────────────────────────────
        card.add(logo);
        card.add(Box.createVerticalStrut(10));
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitulo);
        card.add(Box.createVerticalStrut(28));
        card.add(crearEtiqueta("Correo electrónico"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoEmail);
        card.add(Box.createVerticalStrut(14));
        card.add(crearEtiqueta("Contraseña"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoPassword);
        card.add(Box.createVerticalStrut(10));
        card.add(etiquetaError);
        card.add(Box.createVerticalStrut(6));
        card.add(btnLogin);

        return card;
    }

    private void iniciarSesion() {
        String email    = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());
        etiquetaError.setText(" ");

        if (email.isEmpty() || password.isEmpty()) {
            etiquetaError.setText("Completa todos los campos.");
            return;
        }

        btnLogin.setEnabled(false);
        Usuario usuario = AuthService.login(email, password);

        if (usuario != null) {
            SessionManager.setUsuario(usuario);
            dispose();
            MainFrame ventanaPrincipal = new MainFrame();
            ventanaPrincipal.setVisible(true);
        } else {
            etiquetaError.setText("Correo o contraseña incorrectos.");
            btnLogin.setEnabled(true);
        }
    }

    // Si no existe ningún usuario, obliga a crear el primero
    private void verificarPrimerUso() {
        UsuarioDAO dao = new UsuarioDAO();
        if (dao.contarUsuarios() == 0) {
            JOptionPane.showMessageDialog(this,
                "No hay ningún usuario registrado.\n" +
                "Por favor crea la cuenta del primer administrador.",
                "Primer uso", JOptionPane.INFORMATION_MESSAGE);
            mostrarDialogoRegistro();
        }
    }

    private void mostrarDialogoRegistro() {
        JDialog dialogo = new JDialog(this, "Crear cuenta de administrador", true);
        dialogo.setSize(380, 460);
        dialogo.setLocationRelativeTo(this);
        dialogo.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colores.FONDO_PANEL);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titulo = new JLabel("Crear administrador");
        titulo.setFont(Colores.FUENTE_BOLD);
        titulo.setForeground(Colores.TEXTO_CLARO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField    campoNombre   = new JTextField();
        JTextField    campoEmailReg = new JTextField();
        JPasswordField campoPass1   = new JPasswordField();
        JPasswordField campoPass2   = new JPasswordField();

        estilizarCampo(campoNombre);
        estilizarCampo(campoEmailReg);
        estilizarCampo(campoPass1);
        estilizarCampo(campoPass2);

        JLabel lblError = new JLabel(" ");
        lblError.setForeground(Colores.ROJO);
        lblError.setFont(Colores.FUENTE_PEQUEÑA);
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnCrear = crearBotonPrimario("Crear cuenta");
        btnCrear.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            String emailR = campoEmailReg.getText().trim();
            String pass1  = new String(campoPass1.getPassword());
            String pass2  = new String(campoPass2.getPassword());

            if (nombre.isEmpty() || emailR.isEmpty() || pass1.isEmpty()) {
                lblError.setText("Todos los campos son obligatorios.");
                return;
            }
            if (!pass1.equals(pass2)) {
                lblError.setText("Las contraseñas no coinciden.");
                return;
            }

            boolean exito = AuthService.registrar(nombre, emailR, pass1, Usuario.ROL_ADMIN);
            if (exito) {
                JOptionPane.showMessageDialog(dialogo,
                    "Cuenta creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dialogo.dispose();
            } else {
                lblError.setText("Error al crear cuenta. ¿El correo ya existe?");
            }
        });

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(crearEtiqueta("Nombre completo"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(campoNombre);
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearEtiqueta("Correo electrónico"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(campoEmailReg);
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearEtiqueta("Contraseña"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(campoPass1);
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearEtiqueta("Confirmar contraseña"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(campoPass2);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblError);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnCrear);

        dialogo.setContentPane(panel);
        dialogo.setVisible(true);
    }

    // ── Métodos auxiliares de estilo ─────────────────

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(Colores.FONDO_OSCURO);
        campo.setForeground(Colores.TEXTO_CLARO);
        campo.setCaretColor(Colores.TEXTO_CLARO);
        campo.setFont(Colores.FUENTE_NORMAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private JButton crearBotonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Colores.AZUL_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFont(Colores.FUENTE_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}

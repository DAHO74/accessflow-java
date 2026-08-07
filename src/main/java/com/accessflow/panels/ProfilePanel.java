package com.accessflow.panels;

import com.accessflow.model.Usuario;
import com.accessflow.service.AuthService;
import com.accessflow.service.SessionManager;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfilePanel extends JPanel {

    private final MainFrame mainFrame;

    private JTextField    campoNombre;
    private JTextField    campoEmail;
    private JPasswordField campoPassword;
    private JPasswordField campoConfirmar;
    private JLabel        lblEstado;

    public ProfilePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 0));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);

        llenarFormulario();
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ
    // ═══════════════════════════════════════════════

    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colores.FONDO_OSCURO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titulo = new JLabel("Mi Perfil");
        titulo.setFont(Colores.FUENTE_TITULO);
        titulo.setForeground(Colores.TEXTO_CLARO);
        header.add(titulo, BorderLayout.WEST);
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

        Usuario usuario = SessionManager.getUsuario();

        // Badge de rol (solo lectura)
        Color colorRol = SessionManager.esAdmin()
                ? Colores.AZUL_PRIMARIO
                : new Color(0x10, 0xB9, 0x81);
        JLabel lblRol = new JLabel(usuario.getRol());
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRol.setForeground(Color.WHITE);
        lblRol.setBackground(colorRol);
        lblRol.setOpaque(true);
        lblRol.setBorder(new EmptyBorder(3, 10, 3, 10));
        lblRol.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoNombre   = new JTextField();
        campoEmail    = new JTextField();
        campoPassword = new JPasswordField();
        campoConfirmar = new JPasswordField();
        Componentes.estilizarCampo(campoNombre);
        Componentes.estilizarCampo(campoEmail);
        estilizarPassword(campoPassword);
        estilizarPassword(campoConfirmar);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(Colores.FUENTE_PEQUEÑA);
        lblEstado.setForeground(Colores.TEXTO_GRIS);
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JButton btnGuardar = Componentes.botonPrimario("Guardar cambios");
        btnGuardar.addActionListener(e -> guardar());

        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoton.setBackground(Colores.FONDO_PANEL);
        panelBoton.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBoton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelBoton.add(btnGuardar);

        card.add(lblRol);
        card.add(Box.createVerticalStrut(18));
        card.add(crearEtiqueta("Nombre completo"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoNombre);
        card.add(Box.createVerticalStrut(14));
        card.add(crearEtiqueta("Correo electrónico"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoEmail);
        card.add(Box.createVerticalStrut(24));
        card.add(crearEtiqueta("Nueva contraseña  (dejar vacío para no cambiar)"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoPassword);
        card.add(Box.createVerticalStrut(14));
        card.add(crearEtiqueta("Confirmar nueva contraseña"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoConfirmar);
        card.add(Box.createVerticalStrut(14));
        card.add(lblEstado);
        card.add(Box.createVerticalStrut(8));
        card.add(panelBoton);

        return card;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void llenarFormulario() {
        Usuario u = SessionManager.getUsuario();
        campoNombre.setText(u.getNombre());
        campoEmail.setText(u.getEmail());
    }

    private void guardar() {
        String nombre = campoNombre.getText().trim();
        String email  = campoEmail.getText().trim();

        if (nombre.isEmpty() || email.isEmpty()) {
            mostrarEstado("El nombre y el correo no pueden estar vacíos.", Colores.ROJO);
            return;
        }

        String pass1 = new String(campoPassword.getPassword()).trim();
        String pass2 = new String(campoConfirmar.getPassword()).trim();

        if (!pass1.isEmpty() && !pass1.equals(pass2)) {
            mostrarEstado("Las contraseñas no coinciden.", Colores.ROJO);
            return;
        }

        // pass1 vacío → AuthService no cambia el hash
        String nuevaPass = pass1.isEmpty() ? null : pass1;

        boolean ok = AuthService.actualizarPerfil(SessionManager.getUsuario(), nombre, email, nuevaPass);
        if (ok) {
            campoPassword.setText("");
            campoConfirmar.setText("");
            mostrarEstado("Perfil actualizado correctamente.", Colores.VERDE);
        } else {
            mostrarEstado("No se pudo guardar. Verifica que el correo no esté en uso.", Colores.ROJO);
        }
    }

    private void mostrarEstado(String texto, Color color) {
        lblEstado.setText(texto);
        lblEstado.setForeground(color);
    }

    // ── Auxiliares de estilo ─────────────────────────

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void estilizarPassword(JPasswordField campo) {
        campo.setBackground(Colores.FONDO_OSCURO);
        campo.setForeground(Colores.TEXTO_CLARO);
        campo.setCaretColor(Colores.TEXTO_CLARO);
        campo.setFont(Colores.FUENTE_NORMAL);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }
}

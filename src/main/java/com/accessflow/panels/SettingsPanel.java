package com.accessflow.panels;

import com.accessflow.dao.ConfiguracionDAO;
import com.accessflow.util.Colores;
import com.accessflow.util.Componentes;
import com.accessflow.view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class SettingsPanel extends JPanel {

    private final MainFrame        mainFrame;
    private final ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();

    private JTextField    campoHost;
    private JTextField    campoPuerto;
    private JTextField    campoUsuario;
    private JPasswordField campoPassword;
    private JLabel        lblEstado;

    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 0));
        setBackground(Colores.FONDO_OSCURO);

        add(construirHeader(), BorderLayout.NORTH);
        add(construirCentro(), BorderLayout.CENTER);

        cargarConfiguracion();
    }

    // ═══════════════════════════════════════════════
    //  INTERFAZ
    // ═══════════════════════════════════════════════

    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Colores.FONDO_OSCURO);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel titulo = new JLabel("Configuración");
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

        // Título de sección
        JLabel lblSeccion = new JLabel("Configuración SMTP");
        lblSeccion.setFont(Colores.FUENTE_BOLD);
        lblSeccion.setForeground(Colores.TEXTO_CLARO);
        lblSeccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nota informativa
        JLabel lblNota = new JLabel(
            "<html>Para Gmail: activa la verificación en dos pasos y genera una " +
            "contraseña de aplicación en tu cuenta de Google.</html>"
        );
        lblNota.setFont(Colores.FUENTE_PEQUEÑA);
        lblNota.setForeground(Colores.TEXTO_GRIS);
        lblNota.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Fila host + puerto (lado a lado)
        JPanel filaHostPuerto = new JPanel(new GridLayout(1, 2, 12, 0));
        filaHostPuerto.setBackground(Colores.FONDO_PANEL);
        filaHostPuerto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        filaHostPuerto.setAlignmentX(Component.LEFT_ALIGNMENT);

        campoHost   = new JTextField();
        campoPuerto = new JTextField();
        Componentes.estilizarCampo(campoHost);
        Componentes.estilizarCampo(campoPuerto);

        JPanel panelHost   = construirSubpanel("Servidor SMTP  (ej. smtp.gmail.com)", campoHost);
        JPanel panelPuerto = construirSubpanel("Puerto  (ej. 587)", campoPuerto);
        filaHostPuerto.add(panelHost);
        filaHostPuerto.add(panelPuerto);

        campoUsuario  = new JTextField();
        campoPassword = new JPasswordField();
        Componentes.estilizarCampo(campoUsuario);
        estilizarPassword(campoPassword);

        lblEstado = new JLabel(" ");
        lblEstado.setFont(Colores.FUENTE_PEQUEÑA);
        lblEstado.setForeground(Colores.TEXTO_GRIS);
        lblEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JButton btnGuardar = Componentes.botonPrimario("Guardar configuración");
        btnGuardar.addActionListener(e -> guardar());

        JButton btnProbar = Componentes.botonSecundario("Probar conexión");
        btnProbar.addActionListener(e -> probar());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setBackground(Colores.FONDO_PANEL);
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelBotones.add(btnProbar);
        panelBotones.add(btnGuardar);

        // ── Sección de respaldo ──────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(Colores.BORDE);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSeccionRespaldo = new JLabel("Respaldo de base de datos");
        lblSeccionRespaldo.setFont(Colores.FUENTE_BOLD);
        lblSeccionRespaldo.setForeground(Colores.TEXTO_CLARO);
        lblSeccionRespaldo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNotaRespaldo = new JLabel(
            "<html>Genera un archivo .sql con toda la información del sistema. " +
            "Requiere que <b>mysqldump</b> esté disponible en el sistema.</html>"
        );
        lblNotaRespaldo.setFont(Colores.FUENTE_PEQUEÑA);
        lblNotaRespaldo.setForeground(Colores.TEXTO_GRIS);
        lblNotaRespaldo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNotaRespaldo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel lblEstadoRespaldo = new JLabel(" ");
        lblEstadoRespaldo.setFont(Colores.FUENTE_PEQUEÑA);
        lblEstadoRespaldo.setForeground(Colores.TEXTO_GRIS);
        lblEstadoRespaldo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblEstadoRespaldo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JButton btnRespaldo = Componentes.botonSecundario("Generar respaldo (.sql)");
        JPanel panelRespaldo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelRespaldo.setBackground(Colores.FONDO_PANEL);
        panelRespaldo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelRespaldo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelRespaldo.add(btnRespaldo);
        btnRespaldo.addActionListener(e -> generarRespaldo(lblEstadoRespaldo));

        card.add(lblSeccion);
        card.add(Box.createVerticalStrut(6));
        card.add(lblNota);
        card.add(Box.createVerticalStrut(20));
        card.add(filaHostPuerto);
        card.add(Box.createVerticalStrut(14));
        card.add(crearEtiqueta("Correo remitente"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoUsuario);
        card.add(Box.createVerticalStrut(14));
        card.add(crearEtiqueta("Contraseña / App Password"));
        card.add(Box.createVerticalStrut(5));
        card.add(campoPassword);
        card.add(Box.createVerticalStrut(14));
        card.add(lblEstado);
        card.add(Box.createVerticalStrut(8));
        card.add(panelBotones);
        card.add(Box.createVerticalStrut(20));
        card.add(sep);
        card.add(Box.createVerticalStrut(14));
        card.add(lblSeccionRespaldo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblNotaRespaldo);
        card.add(Box.createVerticalStrut(10));
        card.add(panelRespaldo);
        card.add(Box.createVerticalStrut(6));
        card.add(lblEstadoRespaldo);

        return card;
    }

    // ═══════════════════════════════════════════════
    //  LÓGICA
    // ═══════════════════════════════════════════════

    private void cargarConfiguracion() {
        campoHost.setText(configuracionDAO.obtener("smtp_host"));
        campoPuerto.setText(configuracionDAO.obtener("smtp_puerto"));
        campoUsuario.setText(configuracionDAO.obtener("smtp_usuario"));
        campoPassword.setText(configuracionDAO.obtener("smtp_password"));
    }

    private void guardar() {
        String host     = campoHost.getText().trim();
        String puerto   = campoPuerto.getText().trim();
        String usuario  = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword()).trim();

        if (host.isEmpty() || puerto.isEmpty()) {
            mostrarEstado("El servidor y el puerto son obligatorios.", Colores.ROJO);
            return;
        }

        boolean ok = configuracionDAO.guardar("smtp_host",     host)
                  && configuracionDAO.guardar("smtp_puerto",   puerto)
                  && configuracionDAO.guardar("smtp_usuario",  usuario)
                  && configuracionDAO.guardar("smtp_password", password);

        if (ok) {
            mostrarEstado("Configuración guardada.", Colores.VERDE);
        } else {
            mostrarEstado("Error al guardar la configuración.", Colores.ROJO);
        }
    }

    private void probar() {
        // Guardar primero para que EmailService use los valores actuales
        guardar();

        String usuario = campoUsuario.getText().trim();
        if (usuario.isEmpty()) {
            mostrarEstado("Ingresa un correo remitente para probar.", Colores.AMARILLO);
            return;
        }

        mostrarEstado("Enviando correo de prueba...", Colores.TEXTO_GRIS);

        String error = com.accessflow.service.EmailService.enviar(
            usuario,
            "AccessFlow — prueba de conexión SMTP",
            "Si recibes este correo, la configuración SMTP es correcta."
        );

        if (error == null) {
            mostrarEstado("Correo de prueba enviado a " + usuario, Colores.VERDE);
        } else {
            mostrarEstado(error, Colores.ROJO);
        }
    }

    private void mostrarEstado(String texto, Color color) {
        lblEstado.setText(texto);
        lblEstado.setForeground(color);
    }

    private void generarRespaldo(JLabel lblEstadoRespaldo) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar respaldo como...");
        chooser.setSelectedFile(new java.io.File("accessflow_backup_" +
            java.time.LocalDate.now().toString().replace("-", "") + ".sql"));
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivo SQL (*.sql)", "sql"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File destino = chooser.getSelectedFile();
        if (!destino.getName().endsWith(".sql")) {
            destino = new java.io.File(destino.getAbsolutePath() + ".sql");
        }

        final java.io.File archivoFinal = destino;
        lblEstadoRespaldo.setText("Generando respaldo...");
        lblEstadoRespaldo.setForeground(Colores.TEXTO_GRIS);

        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder(
                    "mysqldump", "-u", "root", "--password=", "accessflow_db"
                );
                pb.redirectOutput(archivoFinal);
                pb.redirectErrorStream(false);
                Process proceso = pb.start();
                int code = proceso.waitFor();

                SwingUtilities.invokeLater(() -> {
                    if (code == 0) {
                        lblEstadoRespaldo.setText("Respaldo guardado en: " + archivoFinal.getName());
                        lblEstadoRespaldo.setForeground(Colores.VERDE);
                    } else {
                        lblEstadoRespaldo.setText("Error al ejecutar mysqldump (código " + code + ").");
                        lblEstadoRespaldo.setForeground(Colores.ROJO);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    lblEstadoRespaldo.setText("No se encontró mysqldump en el sistema.");
                    lblEstadoRespaldo.setForeground(Colores.ROJO);
                });
            }
        }).start();
    }

    // ── Auxiliares de estilo ─────────────────────────

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel construirSubpanel(String etiqueta, JComponent campo) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Colores.FONDO_PANEL);
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Colores.FUENTE_PEQUEÑA);
        lbl.setForeground(Colores.TEXTO_GRIS);
        p.add(lbl,   BorderLayout.NORTH);
        p.add(campo, BorderLayout.CENTER);
        return p;
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

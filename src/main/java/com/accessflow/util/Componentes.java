package com.accessflow.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

// Métodos reutilizables para crear componentes con el estilo del proyecto.
// Cada panel los usa para mantener un aspecto uniforme en toda la aplicación.
public class Componentes {

    public static JButton botonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Colores.AZUL_PRIMARIO);
        btn.setForeground(Color.WHITE);
        btn.setFont(Colores.FUENTE_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JButton botonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Colores.FONDO_PANEL);
        btn.setForeground(Colores.TEXTO_CLARO);
        btn.setFont(Colores.FUENTE_BOLD);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE),
            new EmptyBorder(7, 14, 7, 14)
        ));
        return btn;
    }

    public static JButton botonPeligro(String texto) {
        JButton btn = botonSecundario(texto);
        btn.setForeground(Colores.ROJO);
        return btn;
    }

    public static void estilizarCampo(JTextField campo) {
        campo.setBackground(Colores.FONDO_OSCURO);
        campo.setForeground(Colores.TEXTO_CLARO);
        campo.setCaretColor(Colores.TEXTO_CLARO);
        campo.setFont(Colores.FUENTE_NORMAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Colores.BORDE, 1),
            new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setPreferredSize(new Dimension(390, 36));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    // Renderer centrado para columnas numéricas (ej. ID)
    public static DefaultTableCellRenderer rendererCentrado() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.CENTER);
        return r;
    }

    // Aplica el estilo oscuro a una tabla y devuelve el JScrollPane que la contiene
    public static JScrollPane crearScrollTabla(JTable tabla) {
        tabla.setBackground(Colores.FONDO_PANEL);
        tabla.setForeground(Colores.TEXTO_CLARO);
        tabla.setGridColor(Colores.BORDE);
        tabla.setFont(Colores.FUENTE_NORMAL);
        tabla.setRowHeight(32);
        tabla.setSelectionBackground(Colores.AZUL_PRIMARIO);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.getTableHeader().setBackground(Colores.FONDO_OSCURO);
        tabla.getTableHeader().setForeground(Colores.TEXTO_GRIS);
        tabla.getTableHeader().setFont(Colores.FUENTE_PEQUEÑA);
        tabla.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Colores.BORDE));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Colores.FONDO_PANEL);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }
}

package com.accessflow;

import com.accessflow.database.Conexion;
import com.accessflow.view.LoginFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Usamos el Look and Feel multiplataforma (Metal) para
        // que setBackground() en botones funcione correctamente
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Conexion.migrar();

        // Toda la interfaz Swing debe iniciarse en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}

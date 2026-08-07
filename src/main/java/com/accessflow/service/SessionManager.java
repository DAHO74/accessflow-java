package com.accessflow.service;

import com.accessflow.model.Usuario;

public class SessionManager {

    private static Usuario usuarioActual;

    public static void setUsuario(Usuario usuario) {
        usuarioActual = usuario;
    }

    public static Usuario getUsuario() {
        return usuarioActual;
    }

    public static boolean esAdmin() {
        return usuarioActual != null && usuarioActual.esAdmin();
    }

    public static void cerrarSesion() {
        usuarioActual = null;
    }
}

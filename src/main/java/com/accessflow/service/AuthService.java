package com.accessflow.service;

import com.accessflow.dao.UsuarioDAO;
import com.accessflow.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {

    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public static Usuario login(String email, String password) {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario != null && BCrypt.checkpw(password, usuario.getPasswordHash())) {
            return usuario;
        }
        return null;
    }

    public static boolean registrar(String nombre, String email, String password, String rol) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        Usuario nuevo = new Usuario(nombre, email, hash, rol);
        return usuarioDAO.insertar(nuevo);
    }

    public static boolean actualizarPerfil(Usuario usuario, String nuevoNombre,
                                            String nuevoEmail, String nuevaPassword) {
        usuario.setNombre(nuevoNombre);
        usuario.setEmail(nuevoEmail);
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            usuario.setPasswordHash(BCrypt.hashpw(nuevaPassword, BCrypt.gensalt()));
        }
        return usuarioDAO.actualizar(usuario);
    }
}

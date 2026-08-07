package com.accessflow.model;

import java.time.LocalDateTime;

public class Usuario {

    public static final String ROL_ADMIN    = "ADMIN";
    public static final String ROL_PROFESOR = "PROFESOR";

    private int           id;
    private String        nombre;
    private String        email;
    private String        passwordHash;
    private String        rol;
    private LocalDateTime creadoEn;

    public Usuario() {}

    public Usuario(String nombre, String email, String passwordHash, String rol) {
        this.nombre       = nombre;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.rol          = rol;
        this.creadoEn     = LocalDateTime.now();
    }

    public boolean esAdmin() {
        return ROL_ADMIN.equals(rol);
    }

    // Getters y Setters
    public int getId()                         { return id; }
    public void setId(int id)                  { this.id = id; }
    public String getNombre()                  { return nombre; }
    public void setNombre(String nombre)       { this.nombre = nombre; }
    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }
    public String getPasswordHash()            { return passwordHash; }
    public void setPasswordHash(String hash)   { this.passwordHash = hash; }
    public String getRol()                     { return rol; }
    public void setRol(String rol)             { this.rol = rol; }
    public LocalDateTime getCreadoEn()         { return creadoEn; }
    public void setCreadoEn(LocalDateTime dt)  { this.creadoEn = dt; }

    @Override
    public String toString() { return nombre; }
}

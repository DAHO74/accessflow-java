package com.accessflow.model;

public class Tutor {

    private int    id;
    private String nombre;
    private String email;
    private String telefono;
    private String parentesco;

    public Tutor() {}

    public Tutor(String nombre, String email, String telefono) {
        this.nombre   = nombre;
        this.email    = email;
        this.telefono = telefono;
    }

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }
    public String getNombre()                    { return nombre; }
    public void setNombre(String nombre)         { this.nombre = nombre; }
    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }
    public String getTelefono()                  { return telefono; }
    public void setTelefono(String telefono)     { this.telefono = telefono; }
    public String getParentesco()                { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    @Override
    public String toString() { return nombre; }
}

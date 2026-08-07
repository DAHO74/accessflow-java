package com.accessflow.model;

public class Grupo {

    private int    id;
    private String nombre;
    private String grado;
    private String turno;

    public Grupo() {}

    public Grupo(String nombre, String grado, String turno) {
        this.nombre = nombre;
        this.grado  = grado;
        this.turno  = turno;
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }
    public String getNombre()            { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getGrado()             { return grado; }
    public void setGrado(String grado)   { this.grado = grado; }
    public String getTurno()             { return turno; }
    public void setTurno(String turno)   { this.turno = turno; }

    @Override
    public String toString() { return nombre; }
}

package com.accessflow.model;

public class Alumno {

    private int    id;
    private String nombre;
    private String rfidUid;
    private int    grupoId;
    private int    tutorId;
    private String grupoNombre;  // para mostrar en tablas
    private String grupoGrado;   // para mostrar en tablas
    private String tutorNombre;  // para mostrar en tablas

    public Alumno() {}

    public Alumno(String nombre, String rfidUid, int grupoId, int tutorId) {
        this.nombre   = nombre;
        this.rfidUid  = rfidUid;
        this.grupoId  = grupoId;
        this.tutorId  = tutorId;
    }

    public int getId()                         { return id; }
    public void setId(int id)                  { this.id = id; }
    public String getNombre()                  { return nombre; }
    public void setNombre(String nombre)       { this.nombre = nombre; }
    public String getRfidUid()                 { return rfidUid; }
    public void setRfidUid(String rfidUid)     { this.rfidUid = rfidUid; }
    public int getGrupoId()                    { return grupoId; }
    public void setGrupoId(int grupoId)        { this.grupoId = grupoId; }
    public int getTutorId()                    { return tutorId; }
    public void setTutorId(int tutorId)        { this.tutorId = tutorId; }
    public String getGrupoNombre()             { return grupoNombre; }
    public void setGrupoNombre(String nombre)  { this.grupoNombre = nombre; }
    public String getGrupoGrado()              { return grupoGrado; }
    public void setGrupoGrado(String grado)    { this.grupoGrado = grado; }
    public String getTutorNombre()             { return tutorNombre; }
    public void setTutorNombre(String nombre)  { this.tutorNombre = nombre; }

    @Override
    public String toString() { return nombre; }
}

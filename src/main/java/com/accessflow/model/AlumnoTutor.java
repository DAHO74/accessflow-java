package com.accessflow.model;

public class AlumnoTutor {

    private int    id;
    private int    alumnoId;
    private int    tutorId;
    private String parentesco;

    // Datos extra para mostrar en tablas sin consultas adicionales
    private String alumnoNombre;
    private String tutorNombre;

    public AlumnoTutor() {}

    public AlumnoTutor(int alumnoId, int tutorId, String parentesco) {
        this.alumnoId   = alumnoId;
        this.tutorId    = tutorId;
        this.parentesco = parentesco;
    }

    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }
    public int getAlumnoId()                     { return alumnoId; }
    public void setAlumnoId(int alumnoId)        { this.alumnoId = alumnoId; }
    public int getTutorId()                      { return tutorId; }
    public void setTutorId(int tutorId)          { this.tutorId = tutorId; }
    public String getParentesco()                { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }
    public String getAlumnoNombre()              { return alumnoNombre; }
    public void setAlumnoNombre(String nombre)   { this.alumnoNombre = nombre; }
    public String getTutorNombre()               { return tutorNombre; }
    public void setTutorNombre(String nombre)    { this.tutorNombre = nombre; }
}

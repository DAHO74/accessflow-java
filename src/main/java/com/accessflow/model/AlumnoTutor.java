package com.accessflow.model;

import jakarta.persistence.*;

@Entity
@Table(name = "AlumnoTutor")
public class AlumnoTutor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    private String parentesco;

    public AlumnoTutor() {}
    public AlumnoTutor(Alumno alumno, Tutor tutor, String parentesco) {
        this.alumno = alumno;
        this.tutor = tutor;
        this.parentesco = parentesco;
    }

    public Long getId() { return id; }
    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }
}

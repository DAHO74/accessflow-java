package com.accessflow.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Grupo")
public class Grupo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String grado;
    private String turno;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Alumno> alumnos = new ArrayList<>();

    public Grupo() {}
    public Grupo(String nombre, String grado, String turno) {
        this.nombre = nombre;
        this.grado = grado;
        this.turno = turno;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getGrado() { return grado; }
    public void setGrado(String grado) { this.grado = grado; }
    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
    public List<Alumno> getAlumnos() { return alumnos; }

    @Override
    public String toString() { return nombre + " - " + grado + " (" + turno + ")"; }
}

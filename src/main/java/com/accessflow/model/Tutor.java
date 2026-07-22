package com.accessflow.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tutor")
public class Tutor {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String email;
    private String telefono;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<AlumnoTutor> alumnos = new ArrayList<>();

    public Tutor() {}
    public Tutor(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public List<AlumnoTutor> getAlumnos() { return alumnos; }

    @Override
    public String toString() { return nombre; }
}

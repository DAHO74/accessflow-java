package com.accessflow.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Alumno")
public class Alumno {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "rfid_uid", unique = true)
    private String rfidUid;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<AlumnoTutor> tutores = new ArrayList<>();

    public Alumno() {}
    public Alumno(String nombre, String rfidUid, Grupo grupo) {
        this.nombre = nombre;
        this.rfidUid = rfidUid;
        this.grupo = grupo;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getRfidUid() { return rfidUid; }
    public void setRfidUid(String rfidUid) { this.rfidUid = rfidUid; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public List<AlumnoTutor> getTutores() { return tutores; }

    @Override
    public String toString() { return nombre; }
}

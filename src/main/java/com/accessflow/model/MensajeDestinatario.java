package com.accessflow.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MensajeDestinatario")
public class MensajeDestinatario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mensaje_id")
    private Mensaje mensaje;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id")
    private Tutor tutor;

    public MensajeDestinatario() {}
    public MensajeDestinatario(Mensaje mensaje, Tutor tutor) {
        this.mensaje = mensaje;
        this.tutor = tutor;
    }

    public Long getId() { return id; }
    public Mensaje getMensaje() { return mensaje; }
    public void setMensaje(Mensaje mensaje) { this.mensaje = mensaje; }
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }
}

package com.accessflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Mensaje")
public class Mensaje {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String cuerpo;

    private String tipo; // "individual" o "grupal"

    @Column(name = "enviado_en")
    private LocalDateTime enviadoEn = LocalDateTime.now();

    @OneToMany(mappedBy = "mensaje", cascade = CascadeType.ALL)
    private List<MensajeDestinatario> destinatarios = new ArrayList<>();

    public Mensaje() {}
    public Mensaje(Admin admin, String asunto, String cuerpo, String tipo) {
        this.admin = admin;
        this.asunto = asunto;
        this.cuerpo = cuerpo;
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getCuerpo() { return cuerpo; }
    public void setCuerpo(String cuerpo) { this.cuerpo = cuerpo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDateTime getEnviadoEn() { return enviadoEn; }
    public List<MensajeDestinatario> getDestinatarios() { return destinatarios; }
}

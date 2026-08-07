package com.accessflow.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Mensaje {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int           id;
    private int           usuarioId;
    private int           tutorId;
    private String        tutorNombre; // para mostrar en tablas
    private String        asunto;
    private String        cuerpo;
    private LocalDateTime enviadoEn;

    public Mensaje() {}

    public Mensaje(int usuarioId, int tutorId, String asunto, String cuerpo) {
        this.usuarioId = usuarioId;
        this.tutorId   = tutorId;
        this.asunto    = asunto;
        this.cuerpo    = cuerpo;
        this.enviadoEn = LocalDateTime.now();
    }

    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }
    public int getUsuarioId()                   { return usuarioId; }
    public void setUsuarioId(int usuarioId)     { this.usuarioId = usuarioId; }
    public int getTutorId()                     { return tutorId; }
    public void setTutorId(int tutorId)         { this.tutorId = tutorId; }
    public String getTutorNombre()              { return tutorNombre; }
    public void setTutorNombre(String nombre)   { this.tutorNombre = nombre; }
    public String getAsunto()                   { return asunto; }
    public void setAsunto(String asunto)        { this.asunto = asunto; }
    public String getCuerpo()                   { return cuerpo; }
    public void setCuerpo(String cuerpo)        { this.cuerpo = cuerpo; }
    public LocalDateTime getEnviadoEn()         { return enviadoEn; }
    public void setEnviadoEn(LocalDateTime dt)  { this.enviadoEn = dt; }

    public String getFechaTexto() {
        return enviadoEn != null ? enviadoEn.format(FORMATO) : "-";
    }
}

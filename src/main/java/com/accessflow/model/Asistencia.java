package com.accessflow.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Asistencia {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private int           id;
    private int           alumnoId;
    private String        alumnoNombre; // para mostrar en tablas
    private String        grupoNombre;  // para mostrar en tablas
    private LocalDateTime fechaEntrada;
    private LocalDateTime fechaSalida;

    public Asistencia() {}

    public int getId()                         { return id; }
    public void setId(int id)                  { this.id = id; }
    public int getAlumnoId()                   { return alumnoId; }
    public void setAlumnoId(int alumnoId)      { this.alumnoId = alumnoId; }
    public String getAlumnoNombre()            { return alumnoNombre; }
    public void setAlumnoNombre(String nombre) { this.alumnoNombre = nombre; }
    public String getGrupoNombre()             { return grupoNombre; }
    public void setGrupoNombre(String nombre)  { this.grupoNombre = nombre; }
    public LocalDateTime getFechaEntrada()     { return fechaEntrada; }
    public void setFechaEntrada(LocalDateTime dt) { this.fechaEntrada = dt; }
    public LocalDateTime getFechaSalida()      { return fechaSalida; }
    public void setFechaSalida(LocalDateTime dt)  { this.fechaSalida = dt; }

    public String getHoraEntrada() {
        return fechaEntrada != null ? fechaEntrada.format(FORMATO_HORA) : "-";
    }

    public String getHoraSalida() {
        return fechaSalida != null ? fechaSalida.format(FORMATO_HORA) : "-";
    }

    public String getFechaTexto() {
        return fechaEntrada != null ? fechaEntrada.format(FORMATO_FECHA) : "-";
    }

    public String getEstado() {
        return fechaSalida != null ? "Completa" : "En escuela";
    }
}

package com.accessflow.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Configuracion")
public class Configuracion {

    @Id
    private String clave;

    @Column(columnDefinition = "TEXT")
    private String valor;

    public Configuracion() {}
    public Configuracion(String clave, String valor) {
        this.clave = clave;
        this.valor = valor;
    }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
}

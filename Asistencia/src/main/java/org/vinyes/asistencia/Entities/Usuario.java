package org.vinyes.asistencia.Entities;

public class Usuario {
    private String nombreCompleto;
    private String uuid;
    private boolean fichado;

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isFichado() {
        return fichado;
    }

    public void setFichado(boolean fichado) {
        this.fichado = fichado;
    }

    public Usuario(String nombreCompleto, String uuid) {
        this.nombreCompleto = nombreCompleto;
        this.uuid = uuid;
        this.fichado = false;
    }
}
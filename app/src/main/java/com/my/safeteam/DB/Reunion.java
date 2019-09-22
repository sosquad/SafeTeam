package com.my.safeteam.DB;

import java.util.List;

public class Reunion {
    String motivo;
    String fecha;
    long fechaEnMilis;
    String hora;
    Ubicacion ubicacion;
    List<BasicUser> convocados;

    public Reunion(String motivo, String fecha, long fechaEnMilis, String hora, Ubicacion ubicacion, List<BasicUser> convocados) {
        this.motivo = motivo;
        this.fecha = fecha;
        this.fechaEnMilis = fechaEnMilis;
        this.hora = hora;
        this.ubicacion = ubicacion;
        this.convocados = convocados;
    }

    public Reunion() {
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public long getFechaEnMilis() {
        return fechaEnMilis;
    }

    public void setFechaEnMilis(long fechaEnMilis) {
        this.fechaEnMilis = fechaEnMilis;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<BasicUser> getConvocados() {
        return convocados;
    }

    public void setConvocados(List<BasicUser> convocados) {
        this.convocados = convocados;
    }
}

package com.my.safeteam.DB;

import java.io.Serializable;

public class InvitacionGrupo implements Serializable {
    private String IDGrupo;
    private boolean visto;
    private boolean aceptado;
    private long fecha_invitacion;
    private String lider;

    public InvitacionGrupo(String IDGrupo, String idLider, boolean visto, boolean aceptado, long fecha_invitacion) {
        this.IDGrupo = IDGrupo;
        this.visto = visto;
        this.lider = idLider;
        this.aceptado = aceptado;
        this.fecha_invitacion = fecha_invitacion;
    }

    public String getLider() {
        return lider;
    }

    public void setLider(String lider) {
        this.lider = lider;
    }

    public InvitacionGrupo() {
    }

    public String getIDGrupo() {
        return IDGrupo;
    }

    public void setIDGrupo(String IDGrupo) {
        this.IDGrupo = IDGrupo;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public boolean isAceptado() {
        return aceptado;
    }

    public void setAceptado(boolean aceptado) {
        this.aceptado = aceptado;
    }

    public long getFecha_invitacion() {
        return fecha_invitacion;
    }

    public void setFecha_invitacion(long fecha_invitacion) {
        this.fecha_invitacion = fecha_invitacion;
    }
}



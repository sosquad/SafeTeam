package com.my.safeteam.DB;

import java.io.Serializable;
import java.util.List;

public class Grupo implements Serializable {

    private String uId;
    private String avatar;
    private String nombre;
    private String contexto;
    private long created_at;
    List<BasicUser> usuariosEnGrupo;
    private BasicUser Lider;

    public Grupo() {
    }

    public Grupo(String avatar, String nombre, String contexto, BasicUser lider, long created_at) {
        this.avatar = avatar;
        this.nombre = nombre;
        this.contexto = contexto;
        this.Lider = lider;
        this.created_at = created_at;
    }

    public BasicUser getLider() {
        return Lider;
    }

    public void setLider(BasicUser lider) {
        Lider = lider;
    }

    public Grupo(String nombre, String contexto, List<BasicUser> usuariosEnGrupo, BasicUser lider, long created_at) {
        this.nombre = nombre;
        this.contexto = contexto;
        this.Lider = lider;
        this.usuariosEnGrupo = usuariosEnGrupo;
        this.created_at = created_at;
    }


    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContexto() {
        return contexto;
    }

    public void setContexto(String contexto) {
        this.contexto = contexto;
    }

    public List<BasicUser> getUsuariosEnGrupo() {
        return usuariosEnGrupo;
    }

    public void setUsuariosEnGrupo(List<BasicUser> usuariosEnGrupo) {
        this.usuariosEnGrupo = usuariosEnGrupo;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "avatar='" + avatar + '\'' +
                ", nombre='" + nombre + '\'' +
                ", contexto='" + contexto + '\'' +
                ", usuariosEnGrupo=" + usuariosEnGrupo +
                '}';
    }
}

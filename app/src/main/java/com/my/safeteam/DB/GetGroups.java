package com.my.safeteam.DB;

public class GetGroups {
    String nombre, contexto, avatar, created_at;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public GetGroups(String nombre, String contexto, String avatar, String created_at) {
        this.nombre = nombre;
        this.contexto = contexto;
        this.avatar = avatar;
        this.created_at = created_at;
    }
}

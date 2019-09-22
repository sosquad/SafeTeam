package com.my.safeteam.DB;

public class BasicUser {
    private String uId;
    private String name;
    private String photoUri;
    private String email;
    private int estado; //0 - RECHAZADO 1 - INVITADO 2 - ACEPTADO 3 - LIDER

    public BasicUser() {
    }

    public BasicUser(String uId, String name, String photoUri, String email) {
        this.uId = uId;
        this.name = name;
        this.photoUri = photoUri;
        this.email = email;
    }

    public BasicUser(String uId, String name, String photoUri, String email, int estado) {
        this.uId = uId;
        this.name = name;
        this.photoUri = photoUri;
        this.email = email;
        this.estado = estado;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "BasicUser{" +
                "uId='" + uId + '\'' +
                ", name='" + name + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

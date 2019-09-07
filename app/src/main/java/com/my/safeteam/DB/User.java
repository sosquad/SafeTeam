package com.my.safeteam.DB;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String uId;
    private String name;
    private String photoUri;
    private String email;
    private String tipoLogin;
    private boolean isSelected = false;

    public User() {
    }

    public User(int id, String name, String photoUri, String email) {
        this.id = id;
        this.name = name;
        this.photoUri = photoUri;
        this.email = email;
    }

    public User(@NonNull String name, String photoUri, String email) {
        this.name = name;
        this.photoUri = photoUri;
        this.email = email;
    }

    public String getTipoLogin() {
        return tipoLogin;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setTipoLogin(String tipoLogin) {
        this.tipoLogin = tipoLogin;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected() {
        isSelected = !isSelected;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", uId='" + uId + '\'' +
                ", name='" + name + '\'' +
                ", photoUri='" + photoUri + '\'' +
                ", email='" + email + '\'' +
                ", tipoLogin='" + tipoLogin + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}

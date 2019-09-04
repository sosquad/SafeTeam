package com.my.safeteam.DB;

import androidx.annotation.NonNull;

public class User {
    private String name;
    private String photoUri;
    private String email;

    public User(@NonNull String name, String photoUri, String email) {
        this.name = name;
        this.photoUri = photoUri;
        this.email = email;
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
        return "User{" +
                ", name='" + name + '\'' +
                ", photoUri=" + photoUri +
                ", email='" + email + '\'' +
                '}';
    }
}

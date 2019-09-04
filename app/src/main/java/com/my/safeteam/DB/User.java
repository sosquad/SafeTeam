package com.my.safeteam.DB;

import android.net.Uri;

public class User {
    String name;
    Uri photoUri;
    String email;

    public User(String name, Uri photoUri, String email) {
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
        return photoUri.toString();
    }

    public void setPhotoUri(Uri photoUri) {
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

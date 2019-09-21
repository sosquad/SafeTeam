package com.my.safeteam.DB;

public class Ubicacion {
    String lat;
    String lon;
    String lugar;

    public Ubicacion(String lat, String lon, String lugar) {
        this.lat = lat;
        this.lon = lon;
        this.lugar = lugar;
    }

    public Ubicacion() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}

package com.my.safeteam.DB;

public class Ubicacion {
    double lat;
    double lon;
    String lugar;

    public Ubicacion(double lat, double lon, String lugar) {
        this.lat = lat;
        this.lon = lon;
        this.lugar = lugar;
    }

    public Ubicacion() {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }
}

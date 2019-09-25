package com.my.safeteam.DB;

public class ReferenciaAlGrupo {
    String urlGrupo;
    long timestamp;

    public ReferenciaAlGrupo() {
    }

    public ReferenciaAlGrupo(String urlGrupo, long timestamp) {
        this.urlGrupo = urlGrupo;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrlGrupo() {
        return urlGrupo;
    }

    public void setUrlGrupo(String urlGrupo) {
        this.urlGrupo = urlGrupo;
    }
}

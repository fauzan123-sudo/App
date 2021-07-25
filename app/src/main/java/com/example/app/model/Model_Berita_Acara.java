package com.example.app.model;

public class Model_Berita_Acara {
    String Gambar;
    String judul;
    String Keterangan;

    public Model_Berita_Acara(String gambar, String judul, String keterangan) {
        Gambar = gambar;
        this.judul = judul;
        Keterangan = keterangan;
    }

    public String getGambar() {
        return Gambar;
    }

    public void setGambar(String gambar) {
        Gambar = gambar;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }
}

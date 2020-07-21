package com.greenrock.getorder.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Worker implements Serializable {
    public String email;
    public String isim;
    public String pozisyon;
    public String sifre;
    public String soyisim;
    public String telefon;

    public Worker(){

    }

    public Worker(String email, String isim, String pozisyon, String sifre, String soyisim, String telefon) {
        this.email = email;
        this.isim = isim;
        this.pozisyon = pozisyon;
        this.sifre = sifre;
        this.soyisim = soyisim;
        this.telefon = telefon;
    }
}

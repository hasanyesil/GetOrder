package com.greenrock.getorder.model;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class InactiveCheck implements Serializable {

    private String masa;
    private String saat;
    private String tarih;
    private String toplamFiyat;

    private HashMap<String, HashMap<Integer, Float>> urunler;   // Isim, [Adet, Birim fiyat]

    public InactiveCheck(String masa, String saat, String tarih, String toplamFiyat, HashMap<String, HashMap<Integer, Float>> urunler) {
        this.masa = masa;
        this.saat = saat;
        this.tarih = tarih;
        this.toplamFiyat = toplamFiyat;
        this.urunler = urunler;
    }

    public String getMasa() {
        return masa;
    }

    public String getSaat() {
        return saat;
    }

    public String getTarih() {
        return tarih;
    }

    public String getToplamFiyat() {
        return toplamFiyat;
    }

    public HashMap<String, HashMap<Integer, Float>> getUrunler() {
        return urunler;
    }
}

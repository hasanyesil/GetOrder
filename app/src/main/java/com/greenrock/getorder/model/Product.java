package com.greenrock.getorder.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Product implements Serializable {
    public String isim;
    public String kategori;
    public Float fiyat;

    public Product(){

    }

    public Product(String isim, String kategori, Float fiyat) {
        this.isim = isim;
        this.kategori = kategori;
        this.fiyat = fiyat;
    }

    public boolean isEquals(Product product){
        if (product.isim.equals(isim) && product.kategori.equals(kategori) && product.fiyat.equals(fiyat))
            return true;
        return false;
    }
}

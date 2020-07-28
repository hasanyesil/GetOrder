package com.greenrock.getorder.model;

import java.util.HashMap;

public class Order {
    private String mTableName;
    private HashMap<String, Integer> mProducts;

    public Order(){

    }

    public Order(String mTableName, HashMap<String, Integer> mProducts) {
        this.mTableName = mTableName;
        this.mProducts = mProducts;
    }

    public String getTableName() {
        return mTableName;
    }

    public void setTableName(String mTableName) {
        this.mTableName = mTableName;
    }

    public HashMap<String, Integer> getProducts() {
        return mProducts;
    }

    public void setProducts(HashMap<String, Integer> mProducts) {
        this.mProducts = mProducts;
    }

}

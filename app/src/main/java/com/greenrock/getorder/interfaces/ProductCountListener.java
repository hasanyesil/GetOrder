package com.greenrock.getorder.interfaces;

public interface ProductCountListener{
    void onDecreaseListener(String key, int count);
    void onIncreaseListener(String key, int count);
}

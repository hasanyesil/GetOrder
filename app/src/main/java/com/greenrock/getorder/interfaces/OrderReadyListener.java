package com.greenrock.getorder.interfaces;

import com.greenrock.getorder.model.Order;

public interface OrderReadyListener {
    void onOrderReady(Order order);
}

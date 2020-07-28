package com.greenrock.getorder.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;
import com.greenrock.getorder.interfaces.OrderReadyListener;
import com.greenrock.getorder.model.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderStateAdapter extends RecyclerView.Adapter<OrderStateAdapter.Holder>{

    private ArrayList<Order> mOrders;
    private Context context;

    private OrderReadyListener orderReadyListener;

    public OrderStateAdapter(ArrayList<Order> mOrders, Context context, OrderReadyListener orderReadyListener) {
        this.mOrders = mOrders;
        this.context = context;
        this.orderReadyListener = orderReadyListener;
    }

    public void setOrders(ArrayList<Order> orders){
        mOrders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_for_kitchen_recycler_item,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        holder.linearLayout.removeAllViews();

        Order order = mOrders.get(position);
        HashMap<String, Integer> products = order.getProducts();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = dp2px(10);
        params.setMargins(margin,margin,margin,margin);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        TextView tableName = new TextView(context);
        tableName.setTextColor(context.getColor(R.color.colorPrimary));
        tableName.setText(order.getTableName().toUpperCase());
        tableName.setTextSize(sp2px(12));
        holder.linearLayout.addView(tableName, params);

        for (Map.Entry product : products.entrySet()){
            TextView textView = new TextView(context);
            textView.setText(product.getKey().toString().substring(0,1).toUpperCase() + product.getKey().toString().substring(1).toLowerCase() + "\tx" + String.valueOf(product.getValue()));
            holder.linearLayout.addView(textView, params);
        }

        Button ready = new Button(context);
        ready.setBackground(context.getDrawable(R.drawable.button_shape));
        ready.setTextColor(context.getColor(R.color.title_color));
        ready.setText("HAZIR");
        holder.linearLayout.addView(ready,params);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderReadyListener.onOrderReady(order);
                mOrders.remove(order);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrders.size();
    }

    public int dp2px(int dp){
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public int sp2px(int sp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public class Holder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;

        public Holder(@NonNull View itemView) {
            super(itemView);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.order_linear_layout);
        }
    }
}

package com.greenrock.getorder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;

import java.util.HashMap;


public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.CustomHolder> {

    //Name, count
    private HashMap<String,String> orderListMap;

    public OrderListAdapter(){
        orderListMap = new HashMap<>();
    }

    public void setProductList(HashMap<String,String> orderList){
        orderListMap = orderList;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recycler_item,parent,false);
        return new CustomHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.CustomHolder holder, int position) {
        holder.productNameTv.setText(orderListMap.keySet().toArray()[position].toString());
        holder.productCountTv.setText(String.format("X%s", orderListMap.values().toArray()[position].toString()));
    }

    @Override
    public int getItemCount() {
        return orderListMap.size();
    }

    public class CustomHolder extends RecyclerView.ViewHolder{

        public TextView productNameTv;
        public TextView productCountTv;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);

            productNameTv = (TextView) itemView.findViewById(R.id.productTextView);
            productCountTv = (TextView) itemView.findViewById(R.id.productCountTextView);
        }
    }
}

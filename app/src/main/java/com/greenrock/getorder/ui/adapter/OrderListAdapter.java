package com.greenrock.getorder.ui.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        String key = orderListMap.keySet().toArray()[position].toString();
        int count = Integer.parseInt(orderListMap.values().toArray()[position].toString());
        holder.productNameTv.setText(key);
        holder.productCountTv.setText(String.format("X%s", count));

        holder.removeClickListeners();

        holder.decreaseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderListMap.put(key,String.valueOf(count-1));
                notifyItemChanged(position);
            }
        });

        holder.increaseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderListMap.put(key,String.valueOf(count+1));
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderListMap.size();
    }

    public class CustomHolder extends RecyclerView.ViewHolder{

        public TextView productNameTv;
        public TextView productCountTv;
        public ImageView decreaseImageView;
        public ImageView increaseImageView;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);

            productNameTv = (TextView) itemView.findViewById(R.id.productTextView);
            productCountTv = (TextView) itemView.findViewById(R.id.productCountTextView);
            decreaseImageView = (ImageView) itemView.findViewById(R.id.decreaseImageView);
            increaseImageView = (ImageView) itemView.findViewById(R.id.increaseImageView);
        }

        public void removeClickListeners(){
            decreaseImageView.setOnClickListener(null);
            increaseImageView.setOnClickListener(null);
        }
    }
}

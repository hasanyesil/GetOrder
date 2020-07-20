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
import com.greenrock.getorder.interfaces.ProductCountListener;

import java.util.HashMap;


public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.CustomHolder> {

    //Name, count
    private HashMap<String,String> orderListMap;
    private HashMap<String,Float> orderPriceMap;
    private ProductCountListener mProductCounterListener;
    private boolean isCashier;

    public OrderListAdapter(ProductCountListener productCountListener, HashMap<String,Float> orderPriceMap, boolean isCashier){
        mProductCounterListener = productCountListener;
        orderListMap = new HashMap<>();
        this.orderPriceMap = orderPriceMap;
        this.isCashier = isCashier;
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
        if (isCashier)
            holder.productPriceTv.setText(String.format("%s TL", orderPriceMap.get(key)));
        else
            holder.productPriceTv.setVisibility(View.GONE);
        holder.removeClickListeners();

        holder.decreaseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 1){
                    orderListMap.remove(key);
                    mProductCounterListener.onCountChange(key,count-1);
                    notifyItemRemoved(position);
                }else{
                    orderListMap.put(key,String.valueOf(count-1));
                    mProductCounterListener.onCountChange(key,count-1);
                }
            }
        });

        holder.increaseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderListMap.put(key,String.valueOf(count+1));
                mProductCounterListener.onCountChange(key,count+1);
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
        public TextView productPriceTv;
        public ImageView decreaseImageView;
        public ImageView increaseImageView;

        public CustomHolder(@NonNull View itemView) {
            super(itemView);

            productNameTv = (TextView) itemView.findViewById(R.id.productTextView);
            productCountTv = (TextView) itemView.findViewById(R.id.productCountTextView);
            productPriceTv = (TextView) itemView.findViewById(R.id.order_price_textview);
            decreaseImageView = (ImageView) itemView.findViewById(R.id.decreaseImageView);
            increaseImageView = (ImageView) itemView.findViewById(R.id.increaseImageView);
        }

        public void removeClickListeners(){
            decreaseImageView.setOnClickListener(null);
            increaseImageView.setOnClickListener(null);
        }
    }
}

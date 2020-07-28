package com.greenrock.getorder.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.Holder>{

    private HashMap<String, HashMap<Integer, Float>> mProductMap;
    private ArrayList<String> mProductNames;

    public ProductAdapter(HashMap<String, HashMap<Integer, Float>> mProductMap) {
        this.mProductMap = mProductMap;
        mProductNames = new ArrayList<>(mProductMap.keySet());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_product_recycler_item,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int adet = 0;
        float fiyat = 0;
        for (Map.Entry entry : mProductMap.get(mProductNames.get(position)).entrySet()){
            adet = Integer.parseInt(entry.getKey().toString());
            fiyat = Float.parseFloat(entry.getValue().toString());
        }
        holder.productNameTv.setText(mProductNames.get(position).toUpperCase() + String.format(" (%s TL)",fiyat));
        holder.productCountTv.setText(String.format("X%s", adet));
    }

    @Override
    public int getItemCount() {
        return mProductMap.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        private TextView productNameTv;
        private TextView productCountTv;

        public Holder(@NonNull View itemView) {
            super(itemView);

            productNameTv = (TextView) itemView.findViewById(R.id.product_name_tv);
            productCountTv = (TextView) itemView.findViewById(R.id.product_count_tv);
        }
    }
}

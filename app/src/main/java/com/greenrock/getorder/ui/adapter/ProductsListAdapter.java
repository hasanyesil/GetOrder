package com.greenrock.getorder.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.Holder> implements Filterable {

    private Context context;
    private HashMap<String,Float> productList;  //Name, Price
    private List<String> productListFiltered;
    private HashMap<String,Integer> selectedProducts;

    public ProductsListAdapter(Context context, HashMap<String, Float> productList){
        this.context = context;
        this.productList = productList;
        productListFiltered = new ArrayList<>(productList.keySet());
        selectedProducts = new HashMap<>();
        for (String s : productList.keySet()){
            selectedProducts.put(s,0);
        }
    }

    public HashMap<String,Integer> getSelectedProducts(){
        return selectedProducts;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_recycler_item,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final String productName = productListFiltered.get(position);
        holder.productNameTextview.setText(productName.toUpperCase());
        holder.priceTextView.setText(String.format("%s TL", productList.get(productList.keySet().toArray()[position])));
        if (selectedProducts.containsKey(productName)){
            holder.productCountTextview.setText(selectedProducts.get(productName).toString());
        }else
            holder.productCountTextview.setText("0");
        if (selectedProducts.get(productName) == 0){
            holder.removeProductImageView.setVisibility(View.INVISIBLE);
        }

        holder.removeListeners();

        View.OnClickListener addProductListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = selectedProducts.get(productName);
                selectedProducts.put(productName,count+1);
                holder.productCountTextview.setText(String.valueOf(count+1));
                holder.removeProductImageView.setVisibility(View.VISIBLE);
            }
        };

        View.OnClickListener removeProductListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = selectedProducts.get(productName);
                if (count == 1){
                    holder.removeProductImageView.setVisibility(View.INVISIBLE);
                }
                selectedProducts.put(productName,count-1);
                holder.productCountTextview.setText(String.valueOf(count-1));
            }
        };

        holder.addProductImageView.setOnClickListener(addProductListener);
        holder.removeProductImageView.setOnClickListener(removeProductListener);
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }


    @Override
    public Filter getFilter() {
            return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<String> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(productList.keySet());
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String product : productList.keySet()) {
                        if (product.toLowerCase().contains(filterPattern)) {
                            filteredList.add(product);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productListFiltered.clear();
                productListFiltered.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class Holder extends RecyclerView.ViewHolder {

        public TextView productNameTextview;
        public TextView productCountTextview;
        public TextView priceTextView;
        public ImageView removeProductImageView;
        public ImageView addProductImageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            productNameTextview = (TextView) itemView.findViewById(R.id.productNameTextview);
            productCountTextview = (TextView) itemView.findViewById(R.id.countTextview);
            priceTextView = (TextView) itemView.findViewById(R.id.price_textview);
            removeProductImageView = (ImageView) itemView.findViewById(R.id.removeProductImageView);
            addProductImageView = (ImageView) itemView.findViewById(R.id.addProductImageView);
        }

        public void removeListeners(){
            removeProductImageView.setOnClickListener(null);
            addProductImageView.setOnClickListener(null);
        }

    }
}

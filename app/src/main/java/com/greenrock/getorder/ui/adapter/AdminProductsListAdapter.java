package com.greenrock.getorder.ui.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.greenrock.getorder.model.Product;
import com.greenrock.getorder.ui.activity.ProductDetailActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminProductsListAdapter extends RecyclerView.Adapter<AdminProductsListAdapter.Holder> implements Filterable {

    private Context context;
    private HashMap<String, Product> productList;  //Name, Price
    private List<String> productListFiltered;

    public AdminProductsListAdapter(Context context, HashMap<String, Product> productList){
        this.context = context;
        this.productList = productList;
        productListFiltered = new ArrayList<>(productList.keySet());
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_product_recycler_item,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        final String productName = productListFiltered.get(position);
        holder.productNameTextview.setText(productName.toUpperCase());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product",productList.get(productName));
                context.startActivity(intent);
            }
        });
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

        public Holder(@NonNull View itemView) {
            super(itemView);
            productNameTextview = (TextView) itemView.findViewById(R.id.productNameTextview);
        }

    }
}

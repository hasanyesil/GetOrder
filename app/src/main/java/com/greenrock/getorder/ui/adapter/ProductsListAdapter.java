package com.greenrock.getorder.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenrock.getorder.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.Holder> implements Filterable {

    private Context context;
    private List<String> productList;
    private List<String> productListFiltered;
    private HashMap<String,Integer> selectedProducts;

    public ProductsListAdapter(Context context, List<String> productList){
        this.context = context;
        this.productList = productList;
        productListFiltered = new ArrayList<>(productList);
        selectedProducts = new HashMap<>();
        for (String s : productList){
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
        holder.productNameTextview.setText(productName);
        if (selectedProducts.containsKey(productName)){
            holder.productCountTextview.setText(selectedProducts.get(productName).toString());
        }else
            holder.productCountTextview.setText("0");

        holder.itemView.setOnClickListener(null);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = selectedProducts.get(productName);
                selectedProducts.put(productName,count+1);
                holder.productCountTextview.setText(String.valueOf(count+1));
            }
        };

        holder.itemView.setOnClickListener(onClickListener);
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
                    filteredList.addAll(productList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (String product : productList) {
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

        public Holder(@NonNull View itemView) {
            super(itemView);
            productNameTextview = (TextView) itemView.findViewById(R.id.productNameTextview);
            productCountTextview = (TextView) itemView.findViewById(R.id.countTextview);
        }

    }
}

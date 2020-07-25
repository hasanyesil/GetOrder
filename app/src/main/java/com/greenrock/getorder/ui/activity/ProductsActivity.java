package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.model.Product;
import com.greenrock.getorder.ui.adapter.AdminProductsListAdapter;
import com.greenrock.getorder.ui.adapter.ProductsListAdapter;

import java.io.Serializable;
import java.util.HashMap;

public class ProductsActivity extends AppCompatActivity {

    private DatabaseReference mProductDbRef;

    private HashMap<String, Product> mProducts;

    private Toolbar mToolbar;
    private RecyclerView mProductsRecyclerView;
    private SearchView searchView;

    private AdminProductsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        mProducts = new HashMap<>();

        initFirebase();
        initComponents();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_product){
            Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
            intent.putExtra("products", mProducts);
            startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_products, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private void initFirebase() {
        mProductDbRef = FirebaseDatabase.getInstance().getReference("urunler");

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProducts = new HashMap<>();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    for (DataSnapshot child2 : child.getChildren()){
                        Product product = child2.getValue(Product.class);
                        mProducts.put(child2.getKey(),product);
                    }
                }
                mAdapter = new AdminProductsListAdapter(ProductsActivity.this,mProducts);
                mProductsRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mProductDbRef.addValueEventListener(listener);
    }

    private void initComponents() {
        mToolbar = (Toolbar) findViewById(R.id.products_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        mProductsRecyclerView = (RecyclerView) findViewById(R.id.products_recyclerview);
        mProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProductsRecyclerView.setHasFixedSize(true);
    }
}
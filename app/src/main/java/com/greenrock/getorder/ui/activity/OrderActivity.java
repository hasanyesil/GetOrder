package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.ui.adapter.OrderListAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity {

    private String TAG = "OrderTest";
    private String mTableName;

    private DatabaseReference mCheckData;

    ValueEventListener orderCheckEventListener;

    private Toolbar mToolbar;
    private RecyclerView mOrderRecyclerView;
    private android.widget.SearchView mSearch;

    private OrderListAdapter mAdapter;
    private HashMap<String, String> mCheckOrderList;
    private ArrayList<String> mProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mCheckOrderList = new HashMap<>();

        mTableName = getIntent().getStringExtra("table_name");
        mProductList = getIntent().getStringArrayListExtra("product_list");

        initFirebase();
        initComponents();
    }

    public void initFirebase()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            {
        mCheckData = FirebaseDatabase.getInstance().getReference("aktif fisler/" + mTableName + "/urunler");

        orderCheckEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    mCheckOrderList.put(child.getKey(),child.child("adet").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Something goes wrong, check your network connection");
            }
        };

        mCheckData.addValueEventListener(orderCheckEventListener);
    }

    public void initComponents(){
        mToolbar = (Toolbar) findViewById(R.id.order_toolbar);
        mToolbar.setTitle(mTableName.toUpperCase());
        mSearch = (SearchView) findViewById(R.id.product_search);
    }

    @Override
    protected void onStop() {
        mCheckData.removeEventListener(orderCheckEventListener);
        super.onStop();
    }
}

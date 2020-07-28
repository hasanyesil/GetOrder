package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.interfaces.ProductCountListener;
import com.greenrock.getorder.model.Product;
import com.greenrock.getorder.ui.adapter.OrderListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity implements ProductCountListener {

    private String TAG = "OrderTest";
    private String mTableName;

    private DatabaseReference mCheckData;
    private DatabaseReference mOrderReference;

    ValueEventListener orderCheckEventListener;
    ValueEventListener orderReadyEventListener;

    private Toolbar mToolbar;
    private Button mAddProductButton;
    private RecyclerView mOrderRecyclerView;
    private OrderListAdapter mOrderListAdapter;

    private HashMap<String, String> mCheckOrderList;   //Name, Count
    private HashMap<String, Product> mProductList; //Name, Price
    private HashMap<String, Integer> mReadyOrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mCheckOrderList = new HashMap<>();
        mReadyOrderList = new HashMap<>();
        mTableName = getIntent().getStringExtra("table_name");
        mProductList = (HashMap<String, Product>) getIntent().getSerializableExtra("product_list");
        mOrderListAdapter = new OrderListAdapter(this,mProductList,false);
        Log.d(TAG, "onCreate: table name: " + mTableName);
        Log.d(TAG, "onCreate: product list: " + mProductList);
        initFirebase();
        initComponents();
    }

    public void initFirebase()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            {
        mCheckData = FirebaseDatabase.getInstance().getReference("aktif fisler/" + mTableName + "/urunler");
        mOrderReference = FirebaseDatabase.getInstance().getReference("siparisler/" + mTableName);

        orderReadyEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    mReadyOrderList.put(child.getKey(),child.child("adet").getValue(Integer.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        orderCheckEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    mCheckOrderList.put(child.getKey(),child.child("adet").getValue().toString());
                }
                mOrderListAdapter.setProductList(mCheckOrderList);
                Log.d(TAG, "onDataChange: sa");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Something goes wrong, check your network connection");
            }

        };

        mCheckData.addValueEventListener(orderCheckEventListener);
        mOrderReference.addValueEventListener(orderReadyEventListener);
    }

    public void initComponents(){
        mToolbar = (Toolbar) findViewById(R.id.order_toolbar);
        mToolbar.setTitle(mTableName.toUpperCase());
        mAddProductButton = (Button) findViewById(R.id.addProductButton);
        mAddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this,SearchActivity.class);
                intent.putExtra("products",mProductList);
                startActivityForResult(intent,1);
            }
        });
        mOrderRecyclerView = (RecyclerView) findViewById(R.id.orderRecyclerView);
        mOrderRecyclerView.setAdapter(mOrderListAdapter);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    @Override
    protected void onStop() {
        mCheckData.removeEventListener(orderCheckEventListener);
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK){
            HashMap<String,Integer> selectedProducts = (HashMap<String,Integer>) data.getSerializableExtra("selectedProducts");
            writeFirebase(selectedProducts);
        }
    }

    public void writeFirebase(HashMap<String,Integer> productMap){
        for (Map.Entry<String,Integer> entry : productMap.entrySet()){
            if (mReadyOrderList.containsKey(entry.getKey())){
                int count = mReadyOrderList.get(entry.getKey());
                mOrderReference.child(entry.getKey()).child("adet").setValue(count + entry.getValue());
            }else{
                mOrderReference.child(entry.getKey()).child("adet").setValue(entry.getValue());
            }

            if (mCheckOrderList.containsKey(entry.getKey())){
                int count = Integer.parseInt(mCheckOrderList.get(entry.getKey()));
                entry.setValue(entry.getValue() + count);
                Log.d(TAG, "writeFirebase: Urun -> " + entry.getKey() + " Adet: " + entry.getValue());
            }
            mCheckData.child(entry.getKey()).child("adet").setValue(entry.getValue());
            mCheckData.child(entry.getKey()).child("adet fiyat").setValue(mProductList.get(entry.getKey()).fiyat);
        }
        mCheckData.removeEventListener(orderCheckEventListener);
        mCheckData.addValueEventListener(orderCheckEventListener);
        mOrderReference.removeEventListener(orderReadyEventListener);
        mOrderReference.addValueEventListener(orderReadyEventListener);
    }

    @Override
    public void onCountChange(String key, int count) {
        if (count == 0){
            mCheckData.child(key).removeValue();
            mOrderReference.child(key).removeValue();
        }else{
            mCheckData.child(key).child("adet").setValue(count);
            if (mReadyOrderList.containsKey(key)){
                mOrderReference.child(key).child("adet").setValue(mReadyOrderList.get(key) + 1);
            }else
                mOrderReference.child(key).child("adet").setValue(1);
        }
    }
}

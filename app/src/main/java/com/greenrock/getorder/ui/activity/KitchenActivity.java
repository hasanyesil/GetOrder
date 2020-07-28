package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.interfaces.OrderReadyListener;
import com.greenrock.getorder.model.Order;
import com.greenrock.getorder.ui.adapter.OrderStateAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KitchenActivity extends AppCompatActivity implements OrderReadyListener {

    private FirebaseAuth mAuth;

    private DatabaseReference mOrderReference;
    private DatabaseReference mReadyOrderReference;

    private ValueEventListener listener;

    private ArrayList<Order> mOrders;

    private Toolbar mToolbar;
    private RecyclerView mOrderRecyclerView;
    private OrderStateAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        mOrders = new ArrayList<>();

        initFirebase();
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_waiter,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_button:
                mAuth.signOut();
                Intent intent = new Intent(KitchenActivity.this,LoginActivity.class);
                mOrderReference.removeEventListener(listener);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mOrderReference = FirebaseDatabase.getInstance().getReference("siparisler");
        mReadyOrderReference = FirebaseDatabase.getInstance().getReference("hazir siparisler");

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mOrders = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Order order = new Order();
                    order.setTableName(child.getKey());
                    HashMap<String, Integer> map = new HashMap<>();
                    for (DataSnapshot child2 : child.getChildren()){
                        map.put(child2.getKey(), child2.child("adet").getValue(Integer.class));
                    }
                    order.setProducts(map);
                    mOrders.add(order);
                }
                mAdapter.setOrders(mOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mOrderReference.addValueEventListener(listener);
    }

    @Override
    public void onBackPressed() {
        mOrderReference.removeEventListener(listener);
        super.onBackPressed();
    }

    private void initComponents() {
        mToolbar = (Toolbar) findViewById(R.id.kitchen_toolbar);
        setSupportActionBar(mToolbar);
        mOrderRecyclerView = (RecyclerView) findViewById(R.id.order_state_recyclerview);
        mOrderRecyclerView.setHasFixedSize(true);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new OrderStateAdapter(mOrders,this, this);
        mOrderRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onOrderReady(Order order) {
        mOrderReference.child(order.getTableName()).removeValue();
    }
}
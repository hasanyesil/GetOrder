package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.model.Product;
import com.greenrock.getorder.ui.adapter.TableAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WaiterActivity extends AppCompatActivity {

    private String TAG = "FirebaseTest";
    private int mTableCount;
    private int tableStatus[];

    private FirebaseApp mApp;
    private DatabaseReference mDatabase;
    private DatabaseReference mActiveCheckReferance;
    private DatabaseReference mProductListReference;

    private FirebaseAuth mAuth;

    private HashMap<String, Product> mProductList;

    private Toolbar mToolbar;
    private RecyclerView mTableRecyclerview;
    private TableAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter);

        initFirebase();
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_waiter,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_button:
                mAuth.signOut();
                Intent intent = new Intent(WaiterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void initFirebase(){
        mApp = FirebaseApp.getInstance();
        mAuth = FirebaseAuth.getInstance(mApp);
        mDatabase = FirebaseDatabase.getInstance().getReference("masa sayisi");
        mActiveCheckReferance = FirebaseDatabase.getInstance().getReference("aktif fisler");
        mProductListReference = FirebaseDatabase.getInstance().getReference("urunler");

        ValueEventListener eventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: " + dataSnapshot.getValue(String.class));
                mTableCount = Integer.parseInt(dataSnapshot.getValue(String.class));
                Log.d(TAG, "onDataChange: " + mTableCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error while gettin table count");
            }
        };

        ValueEventListener eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tableStatus = new int[mTableCount];
                int table;
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    String [] parts = child.getKey().split(" ");
                    table = Integer.parseInt(parts[1]);
                    tableStatus[table-1] = 1;
                }

                adapter.setTableStates(tableStatus);
                Log.d(TAG, "onDataChange: " + Arrays.toString(tableStatus));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ValueEventListener productListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    for (DataSnapshot child2 : child.getChildren()){
                        Product product = child2.getValue(Product.class);
                        mProductList.put(product.isim,product);
                    }
                }
                adapter.setProductList(mProductList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        mDatabase.addListenerForSingleValueEvent(eventListener1);
        mActiveCheckReferance.addValueEventListener(eventListener2);
        mProductListReference.addListenerForSingleValueEvent(productListListener);
    }

    public void initComponents(){
        mProductList = new HashMap<>();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mTableRecyclerview = (RecyclerView) findViewById(R.id.tableRecylerView);
        Log.d(TAG, "initComponents: " + mTableCount);
        adapter = new TableAdapter(this,false);
        mTableRecyclerview.setAdapter(adapter);
        mTableRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "initComponents: " + mTableCount);
    }

}

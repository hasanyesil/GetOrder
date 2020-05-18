package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.ui.adapter.TableAdapter;
import com.greenrock.getorder.util.interfaces.OnGetDataListener;
import com.greenrock.getorder.util.interfaces.OnGetTableDataListener;
import com.greenrock.getorder.util.interfaces.OnRecylerItemUpdateListener;

import java.util.Arrays;

public class WaiterActivity extends AppCompatActivity implements OnGetTableDataListener, OnRecylerItemUpdateListener {

    private String TAG = "FirebaseTest";
    private int mTableCount;
    private int tableStatus[];

    private OnGetDataListener getDataListener;
    private OnGetTableDataListener getTableDataListener;
    private OnRecylerItemUpdateListener onRecylerItemUpdateListener;

    private FirebaseApp mApp;
    private DatabaseReference mDatabase;
    private DatabaseReference mActiveCheckReferance;
    private FirebaseAuth mAuth;

    private Toolbar mToolbar;
    private RecyclerView mTableRecylerview;
    private TableAdapter adapter;

    @Override
    protected void onStart() {
        getTableDataListener = (OnGetTableDataListener) this;
        onRecylerItemUpdateListener = (OnRecylerItemUpdateListener) this;
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

                onGetTableDataSuccess();
                Log.d(TAG, "onDataChange: " + Arrays.toString(tableStatus));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabase.addListenerForSingleValueEvent(eventListener1);
        mActiveCheckReferance.addValueEventListener(eventListener2);
    }

    public void initComponents(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mTableRecylerview = (RecyclerView) findViewById(R.id.tableRecylerView);
        Log.d(TAG, "initComponents: " + mTableCount);
        adapter = new TableAdapter(this,this);
        mTableRecylerview.setAdapter(adapter);
        mTableRecylerview.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "initComponents: " + mTableCount);
    }


    @Override
    public void onGetTableDataSuccess() {
        // After getting active checks. Todo : Change tables color as green or grey.
        adapter.setTableStates(tableStatus);
    }

    @Override
    public void onRecylerViewItemUpdate(int updatedLineCount, int updatedLineOrder) {

    }
}

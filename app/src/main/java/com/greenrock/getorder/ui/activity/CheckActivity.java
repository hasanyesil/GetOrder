package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.interfaces.ProductCountListener;
import com.greenrock.getorder.ui.adapter.OrderListAdapter;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckActivity extends AppCompatActivity implements ProductCountListener {

    private String TAG = "OrderTest";
    private String mTableName;
    private float totalPrice;

    private DatabaseReference mCheckData;
    private DatabaseReference mClosedCheckData;
    private DatabaseReference mCheckNode;

    ValueEventListener orderCheckEventListener;

    private Toolbar mToolbar;
    private RecyclerView mOrderRecyclerView;
    private OrderListAdapter mOrderListAdapter;
    private Button mCheckButton;
    private TextView mTotalPriceTextview;


    private HashMap<String, String> mCheckOrderList;   //
    private HashMap<String,Float> mProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        mProductList = (HashMap<String, Float>) getIntent().getSerializableExtra("product_list");

        mCheckOrderList = new HashMap<>();
        mOrderListAdapter = new OrderListAdapter(this,mProductList,true);

        mTableName = getIntent().getStringExtra("table_name");
        Log.d(TAG, "onCreate: table name: " + mTableName);
        Log.d(TAG, "onCreate: product list: " + mProductList);
        initFirebase();
        initComponents();
    }

    public void initFirebase()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            {
        mCheckData = FirebaseDatabase.getInstance().getReference("aktif fisler/" + mTableName + "/urunler");
        mCheckNode = FirebaseDatabase.getInstance().getReference("aktif fisler/" + mTableName);
        mClosedCheckData = FirebaseDatabase.getInstance().getReference("inaktif fisler");
        orderCheckEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    mCheckOrderList.put(child.getKey(),child.child("adet").getValue().toString());
                }
                mOrderListAdapter.setProductList(mCheckOrderList);

                updateTotalPrice();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Something goes wrong, check your network connection");
            }

        };

        mCheckData.addValueEventListener(orderCheckEventListener);
    }

    public void initComponents(){
        mToolbar = (Toolbar) findViewById(R.id.check_order_toolbar);
        mToolbar.setTitle(mTableName.toUpperCase());
        mOrderRecyclerView = (RecyclerView) findViewById(R.id.check_orderRecyclerView);
        mOrderRecyclerView.setAdapter(mOrderListAdapter);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCheckButton = (Button) findViewById(R.id.check_button);
        mTotalPriceTextview = (TextView) findViewById(R.id.total_price_textview);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckActivity.this);

                TextView title = new TextView(CheckActivity.this);
                title.setTextColor(ContextCompat.getColor(CheckActivity.this, R.color.colorPrimary));
                title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                title.setTypeface(Typeface.DEFAULT_BOLD);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 20, 0, 0);
                title.setPadding(0,30,0,0);
                title.setLayoutParams(lp);
                title.setText("HESAP");
                title.setGravity(Gravity.CENTER);

                builder.setCustomTitle(title).setMessage("Hesap kapatılsın mı?");
                builder.setPositiveButton("Evet", new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Date date = new Date();
                        SimpleDateFormat dayFormatter = new SimpleDateFormat("dd-MM-yyyy");
                        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
                        String day = dayFormatter.format(date);
                        String time = timeFormatter.format(date);
                        mClosedCheckData.child(day).child(time).child("masa").setValue(mTableName.split(" ")[1]);
                        for (Map.Entry entry : mCheckOrderList.entrySet()){
                            mClosedCheckData.child(day).child(time).child("urunler").child(entry.getKey().toString()).child("adet").setValue(Float.valueOf(entry.getValue().toString()))
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mClosedCheckData.child(day).child(time).child("urunler").child(entry.getKey().toString()).child("adet fiyat")
                                                    .setValue(mProductList.get(entry.getKey()));
                                            mClosedCheckData.child(day).child(time).child("toplam fiyat").setValue(totalPrice);
                                        }
                                    });
                            mCheckNode.removeValue();
                            finish();
                        }
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        };
        mCheckButton.setOnClickListener(listener);
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
            if (mCheckOrderList.containsKey(entry.getKey())){
                int count = Integer.parseInt(mCheckOrderList.get(entry.getKey()));
                entry.setValue(entry.getValue() + count);
                Log.d(TAG, "writeFirebase: Urun -> " + entry.getKey() + " Adet: " + entry.getValue());
            }
            mCheckData.child(entry.getKey()).child("adet").setValue(entry.getValue());
            mCheckData.child(entry.getKey()).child("adet fiyat").setValue(mProductList.get(entry.getKey()));
            mCheckData.removeEventListener(orderCheckEventListener);
            mCheckData.addValueEventListener(orderCheckEventListener);
        }
    }

    @Override
    public void onCountChange(String key, int count) {
        if (count == 0){
            mCheckData.child(key).removeValue();
        }else{
            mCheckData.child(key).child("adet").setValue(count);
        }
    }

    public void updateTotalPrice(){
        totalPrice = 0;
        if (mTotalPriceTextview!=null){
            for (Map.Entry entry : mCheckOrderList.entrySet()){
                totalPrice = totalPrice + (Integer.parseInt(entry.getValue().toString()) * mProductList.get(entry.getKey()));
            }
            mTotalPriceTextview.setText(String.format("%s TL", totalPrice));
        }
    }
}

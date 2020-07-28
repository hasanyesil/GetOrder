package com.greenrock.getorder.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.greenrock.getorder.R;
import com.greenrock.getorder.model.InactiveCheck;
import com.greenrock.getorder.ui.adapter.ProductAdapter;

public class OldCheckDetailActivity extends AppCompatActivity {

    private InactiveCheck mCheck;

    private TextView mTableNameTv;
    private TextView mTotalPriceTv;
    private RecyclerView mProductsRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_check_detail);

        mCheck = (InactiveCheck) getIntent().getSerializableExtra("check");

        initComponents();
    }

    private void initComponents() {
        mTableNameTv = (TextView) findViewById(R.id.table_name_tv);
        mTableNameTv.setText("MASA " + mCheck.getMasa());
        mTotalPriceTv = (TextView) findViewById(R.id.total_price_tv);
        mTotalPriceTv.setText("TOPLAM FIYAT: " + mCheck.getToplamFiyat() + " TL");
        mProductsRv = (RecyclerView) findViewById(R.id.product_rv);
        mProductsRv.setHasFixedSize(true);
        mProductsRv.setLayoutManager(new LinearLayoutManager(this));
        mProductsRv.setAdapter(new ProductAdapter(mCheck.getUrunler()));
    }
}
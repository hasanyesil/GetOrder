package com.greenrock.getorder.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toolbar;

import com.greenrock.getorder.R;

public class AdminActivity extends AppCompatActivity {

    private CardView mWorkersCardView;
    private CardView mProductsCardView;
    private CardView mChecksCardView;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initComponents();
    }

    private void initComponents() {
        mWorkersCardView = (CardView) findViewById(R.id.workers_card_view);
        mProductsCardView = (CardView) findViewById(R.id.products_cardview);
        mChecksCardView = (CardView) findViewById(R.id.checks_cardview);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.workers_card_view:
                        intent = new Intent(AdminActivity.this, WorkersActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.products_cardview:
                        intent = new Intent(AdminActivity.this, ProductsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.checks_cardview:
                        intent = new Intent(AdminActivity.this, OldChecksAcitivity.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        mWorkersCardView.setOnClickListener(listener);
        mProductsCardView.setOnClickListener(listener);
        mChecksCardView.setOnClickListener(listener);
    }
}

package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.greenrock.getorder.R;

public class AdminActivity extends AppCompatActivity {

    private CardView mWorkersCardView;
    private CardView mProductsCardView;
    private CardView mChecksCardView;
    private Toolbar mToolbar;

    private Intent intent;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

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
        if (item.getItemId() == R.id.logout_button){
            mAuth.signOut();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        }
        return true;
    }

    private void initFirebase(){
        mAuth = FirebaseAuth.getInstance();
    }

    private void initComponents() {
        mToolbar = (Toolbar) findViewById(R.id.admin_toolbar);
        setSupportActionBar(mToolbar);
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

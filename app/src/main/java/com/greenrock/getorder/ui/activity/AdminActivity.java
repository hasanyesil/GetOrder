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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;

public class AdminActivity extends AppCompatActivity {

    private CardView mWorkersCardView;
    private CardView mProductsCardView;
    private CardView mChecksCardView;
    private Toolbar mToolbar;
    private EditText mTableCountEdt;
    private Button mSaveTableCountBtn;

    private DatabaseReference mTableCountReference;

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
        mTableCountReference = FirebaseDatabase.getInstance().getReference("masa sayisi");

        ValueEventListener mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mTableCountEdt.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mTableCountReference.addListenerForSingleValueEvent(mListener);
    }

    private void initComponents() {
        mToolbar = (Toolbar) findViewById(R.id.admin_toolbar);
        setSupportActionBar(mToolbar);
        mWorkersCardView = (CardView) findViewById(R.id.workers_card_view);
        mProductsCardView = (CardView) findViewById(R.id.products_cardview);
        mChecksCardView = (CardView) findViewById(R.id.checks_cardview);
        mTableCountEdt = (EditText) findViewById(R.id.table_count_edt);
        mSaveTableCountBtn = (Button) findViewById(R.id.table_count_save_btn);
        mSaveTableCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTableCountEdt.clearFocus();
                mTableCountReference.setValue(mTableCountEdt.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminActivity.this, "Masa sayısı güncellendi.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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
                        intent = new Intent(AdminActivity.this, OldChecksActivity.class);
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

package com.greenrock.getorder.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.greenrock.getorder.R;

public class WaiterActivity extends AppCompatActivity {

    String TAG = "FirebaseTest";

    DatabaseReference mDatabase;
    String pozisyon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter);

        if (getIntent().getStringExtra("pozisyon").equals("garson")){
            Log.d(TAG, "onCreate: TRUE");
        }
        
    }
}

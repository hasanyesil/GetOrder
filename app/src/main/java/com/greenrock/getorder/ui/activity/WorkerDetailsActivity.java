package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greenrock.getorder.R;
import com.greenrock.getorder.model.Worker;

public class WorkerDetailsActivity extends AppCompatActivity {

    private final String TAG = "WORKER_DETAIL_TEST";

    private DatabaseReference mWorkerDbRef;

    private Worker mWorker;

    private Toolbar mToolbar;
    private EditText mWorkerNameEdt;
    private EditText mWorkerPhoneEdt;
    private EditText mWorkerPositionEdt;
    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_details);
        mWorker = (Worker) getIntent().getSerializableExtra("worker");

        initFirebase();
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_worker_details,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_worker) {
            mWorkerDbRef.child(mWorker.telefon).removeValue();
            Intent intent = new Intent();
            intent.putExtra("deleted_worker", mWorker);
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    private void initFirebase(){
        mWorkerDbRef = FirebaseDatabase.getInstance().getReference("calisanlar");
    }

    private void initComponents() {
        mToolbar = (Toolbar) findViewById(R.id.worker_detail_toolbar);
        setSupportActionBar(mToolbar);
        mWorkerNameEdt = (EditText) findViewById(R.id.worker_name_edt);
        mWorkerNameEdt.setText(mWorker.isim);
        mWorkerPhoneEdt = (EditText) findViewById(R.id.worker_phone_edt);
        mWorkerPhoneEdt.setText(mWorker.telefon);
        mWorkerPositionEdt = (EditText) findViewById(R.id.worker_position_edt);
        mWorkerPositionEdt.setText(mWorker.pozisyon);

        mSaveButton = (Button) findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPhoneNumber = mWorker.telefon;
                mWorker.isim = mWorkerNameEdt.getText().toString();
                mWorker.telefon = mWorkerPhoneEdt.getText().toString();
                mWorker.pozisyon = mWorkerPositionEdt.getText().toString();
                mWorkerDbRef.child(oldPhoneNumber).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mWorkerDbRef.child(mWorker.telefon).setValue(mWorker).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent();
                                intent.putExtra("updated_worker",mWorker);
                                setResult(RESULT_FIRST_USER,intent);
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }


}
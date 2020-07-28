package com.greenrock.getorder.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.greenrock.getorder.R;
import com.greenrock.getorder.model.Worker;

import java.util.ArrayList;

public class AddWorkerActivity extends AppCompatActivity {

    EditText mWorkerNameEdt;
    EditText mWorkerSurnameEdt;
    EditText mWorkerPhoneEdt;
    EditText mWorkerPositionEdt;
    EditText mWorkerMailEdt;
    EditText mWorkerPasswordEdt;
    Button mSaveButton;

    private ArrayList<Worker> mWorkerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker);

        mWorkerList = (ArrayList<Worker>) getIntent().getSerializableExtra("workers");
        initComponents();
    }

    private void initComponents() {
        mWorkerNameEdt = (EditText) findViewById(R.id.new_worker_name_edt);
        mWorkerSurnameEdt = (EditText) findViewById(R.id.new_worker_surname_edt);
        mWorkerPhoneEdt = (EditText) findViewById(R.id.new_worker_phone_edt);
        mWorkerPositionEdt = (EditText) findViewById(R.id.new_worker_position_edt);
        mWorkerMailEdt = (EditText) findViewById(R.id.new_worker_mail);
        mWorkerPasswordEdt = (EditText) findViewById(R.id.new_worker_password);

        mSaveButton = (Button) findViewById(R.id.new_worker_save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Worker worker = new Worker();
                worker.isim = mWorkerNameEdt.getText().toString().trim();
                worker.soyisim = mWorkerSurnameEdt.getText().toString().trim();
                worker.telefon = mWorkerPhoneEdt.getText().toString().trim();
                worker.pozisyon = mWorkerPositionEdt.getText().toString().trim();
                worker.email = mWorkerMailEdt.getText().toString().trim();
                worker.sifre = mWorkerPasswordEdt.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(worker.email.trim()).matches()) {
                    Toast.makeText(AddWorkerActivity.this, "Doğru mail adresi girin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!worker.isim.matches("^[A-Za-z]+$")){
                    Toast.makeText(AddWorkerActivity.this, "Uygun isim girin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!worker.soyisim.matches("^[A-Za-z]+$")){
                    Toast.makeText(AddWorkerActivity.this, "Uygun soyad girin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!worker.pozisyon.matches("^[A-Za-z]+$")){
                    Toast.makeText(AddWorkerActivity.this, "Uygun pozisyon girin.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (worker.telefon.length() < 8){
                    Toast.makeText(AddWorkerActivity.this, "Şifre en az 8 haneli olmalı.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Worker w : mWorkerList){
                    if (worker.telefon.equals(w.telefon)){
                        Toast.makeText(AddWorkerActivity.this, "Bu telefon numarasına kayıtlı başka çalışan bulunuyor.", Toast.LENGTH_SHORT).show();
                        return;
                    }else if (worker.email.equals(w.email)){
                        Toast.makeText(AddWorkerActivity.this, "Bu emaile kayıtlı başka çalışan bulunuyor.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                worker.isim = worker.isim.substring(0,1).toUpperCase() + worker.isim.substring(1).toLowerCase();
                worker.soyisim = worker.soyisim.substring(0,1).toUpperCase() + worker.soyisim.substring(1).toLowerCase();
                worker.pozisyon = worker.pozisyon.toLowerCase();


                Intent intent = new Intent();
                intent.putExtra("new_worker",worker);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
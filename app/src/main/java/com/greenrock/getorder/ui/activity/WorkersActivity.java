package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.model.Worker;
import com.greenrock.getorder.ui.adapter.WorkerAdapter;

import java.util.ArrayList;

public class WorkersActivity extends AppCompatActivity {

    private final String TAG = "WORKER_TEST";

    private DatabaseReference mWorkerDbRef;
    private FirebaseAuth mAuth;

    private ArrayList<Worker> workerList;

    RecyclerView mWorkerRecyclerView;
    WorkerAdapter mWorkerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers);

        workerList = new ArrayList<>();

        initFirebase();
        initComponents();
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance(FirebaseApp.getInstance());
        
        mWorkerDbRef = FirebaseDatabase.getInstance().getReference("calisanlar");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                workerList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Worker worker = snapshot.getValue(Worker.class);
                    if (!worker.pozisyon.equals("admin"))
                        workerList.add(worker);
                }
                mWorkerAdapter.setWorkers(workerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mWorkerDbRef.addValueEventListener(eventListener);
    }

    private void initComponents() {
        mWorkerRecyclerView = (RecyclerView) findViewById(R.id.workers_recyler_view);
        mWorkerAdapter = new WorkerAdapter(workerList,this);
        mWorkerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWorkerRecyclerView.setAdapter(mWorkerAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK){
            Worker worker = (Worker) data.getSerializableExtra("worker");
            for (int i = 0; i<workerList.size(); i++){
                if (workerList.get(i).telefon.equals(worker.telefon)){
                    workerList.remove(i);

                    break;
                }
            }
        }
    }
}
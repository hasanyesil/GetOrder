package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.model.InactiveCheck;
import com.greenrock.getorder.ui.adapter.CheckAdapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class OldChecksActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private DatabaseReference mOldCheckDbRef;

    private Toolbar mToolbar;

    private TextView mStartDateTv;
    private TextView mEndDateTv;

    private EditText mStartDateEdt;
    private EditText mEndDateEdt;

    private RecyclerView mCheckRecyclerView;
    private CheckAdapter mAdapter;

    private Date mStartDate;
    private Date mEndDate;

    private ArrayList<InactiveCheck> mChecks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_checks);

        mChecks = new ArrayList<>();

        initComponents();
        initFirebase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_old_check,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter_item){
            if (mStartDateTv.getVisibility() == View.GONE) {
                mStartDateTv.setVisibility(View.VISIBLE);
                mEndDateTv.setVisibility(View.VISIBLE);
                mStartDateEdt.setVisibility(View.VISIBLE);
                mEndDateEdt.setVisibility(View.VISIBLE);
                mAdapter.setChecks(mChecks);
                item.setTitle("Filtreyi kaldır");
            }else{
                mStartDateTv.setVisibility(View.GONE);
                mEndDateTv.setVisibility(View.GONE);
                mStartDateEdt.setVisibility(View.GONE);
                mEndDateEdt.setVisibility(View.GONE);
                item.setTitle("Filtrele");
            }
        }
        return true;
    }

    private void initFirebase() {
        mOldCheckDbRef = FirebaseDatabase.getInstance().getReference("inaktif fisler");

        ValueEventListener checkListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    for (DataSnapshot child2 : child.getChildren()){
                        String masa = child2.child("masa").getValue().toString();
                        String saat = child2.child("saat").getValue().toString();
                        String tarih = child2.child("tarih").getValue().toString();
                        String toplamFiyat = child2.child("toplam fiyat").getValue().toString();

                        HashMap<String, HashMap<Integer, Float>> products = new HashMap<>();
                        for (DataSnapshot child3 : child2.child("urunler").getChildren()){
                            HashMap<Integer, Float> product = new HashMap<>();
                            product.put(child3.child("adet").getValue(Integer.class), child3.child("adet fiyat").getValue(Float.class));
                            products.put(child3.getKey(), product);
                        }
                        InactiveCheck inactiveCheck = new InactiveCheck(masa,saat,tarih,toplamFiyat,products);
                        mChecks.add(inactiveCheck);
                    }
                }
                mAdapter = new CheckAdapter(OldChecksActivity.this);
                mCheckRecyclerView.setAdapter(mAdapter);
                mAdapter.setChecks(mChecks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mOldCheckDbRef.addListenerForSingleValueEvent(checkListener);
    }

    private void initComponents(){

        mStartDateTv = (TextView) findViewById(R.id.start_date_tv);
        mEndDateTv = (TextView) findViewById(R.id.end_date_tv);

        mStartDateEdt = (EditText) findViewById(R.id.start_date_edt);
        mStartDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(false);
            }
        });
        mEndDateEdt = (EditText) findViewById(R.id.end_date_edt);
        mEndDateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(true);
            }
        });

        mCheckRecyclerView = (RecyclerView) findViewById(R.id.check_recyclerview);
        mCheckRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mToolbar = (Toolbar) findViewById(R.id.old_check_toolbar);
        setSupportActionBar(mToolbar);

    }

    public void showDateDialog(boolean isThemeDark){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                OldChecksActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setMaxDate(now);
        dpd.setThemeDark(isThemeDark);
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getSupportFragmentManager(),"DatePickerDialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String day = dayOfMonth > 9 ? String.valueOf(dayOfMonth) : "0" + dayOfMonth;
        String month = monthOfYear > 9 ? String.valueOf(monthOfYear) : "0" + monthOfYear;

        if (!view.isThemeDark()){
            mStartDateEdt.setText(day + "-" + month + "-" + year);
            try {
                mStartDate = new SimpleDateFormat("DD-MM-yyyy").parse(mStartDateEdt.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mEndDate != null){
                if (mStartDate.compareTo(mEndDate) > 0){
                    Toast.makeText(this, "Geçerli bir tarih aralığı girin.", Toast.LENGTH_SHORT).show();
                }else{
                    ArrayList<InactiveCheck> mFilteredChecks = new ArrayList<>();
                    for (InactiveCheck check : mChecks){
                        try {
                            Date date = new SimpleDateFormat("DD-MM-yyyy").parse(check.getTarih());
                            if (mStartDate.compareTo(date) <= 0 && date.compareTo(mEndDate) <= 0){
                                mFilteredChecks.add(check);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mAdapter.setChecks(mFilteredChecks);
                }
            }

        }else{
            mEndDateEdt.setText(day + "-" + month + "-" + year);
            try {
                mEndDate = new SimpleDateFormat("DD-MM-yyyy").parse(mEndDateEdt.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (mStartDate != null){
                if (mStartDate.compareTo(mEndDate) > 0){
                    Toast.makeText(this, "Geçerli bir tarih aralığı girin.", Toast.LENGTH_SHORT).show();
                }else{
                    ArrayList<InactiveCheck> mFilteredChecks = new ArrayList<>();
                    for (InactiveCheck check : mChecks){
                        try {
                            Date date = new SimpleDateFormat("DD-MM-yyyy").parse(check.getTarih());
                            if (mStartDate.compareTo(date) <= 0 && date.compareTo(mEndDate) <= 0){
                                mFilteredChecks.add(check);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    mAdapter.setChecks(mFilteredChecks);
                }
            }
        }
    }
}
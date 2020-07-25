package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.InputType;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;
import com.greenrock.getorder.model.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class ProductDetailActivity extends AppCompatActivity {

    private DatabaseReference mProductDbRef;

    private Intent intent;

    private Product mProduct;
    private String mProductCategory;
    private ArrayList<String> mProductNames;

    private EditText mProductNameEdt;
    private EditText mProductPriceEdt;
    private Toolbar mToolbar;
    private Button mSaveButton;
    private RadioButton mDrinkRadioButton;
    private RadioButton mFoodRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        intent = getIntent();
        if (intent != null)
            mProduct = (Product) intent.getSerializableExtra("product");

        initFirebase();
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_product){
            AlertDialog dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
            TextView title = new TextView(ProductDetailActivity.this);
            title.setTextColor(ContextCompat.getColor(ProductDetailActivity.this, R.color.colorPrimary));
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 20, 0, 0);
            title.setPadding(0,30,0,0);
            title.setLayoutParams(lp);
            title.setText("ÜRÜN");
            title.setGravity(Gravity.CENTER);

            builder.setCustomTitle(title).setMessage("Ürün silinsin mi?");
            builder.setPositiveButton("Evet", new Dialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    removeProduct();
                    finish();
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
        return true;
    }

    private void initFirebase(){
        mProductDbRef = FirebaseDatabase.getInstance().getReference("urunler");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProductNames = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    for (DataSnapshot child2 : child.getChildren()){
                        mProductNames.add(child2.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mProductDbRef.addValueEventListener(valueEventListener);
    }

    private void initComponents() {
        mProductNameEdt = (EditText) findViewById(R.id.product_name_edt);
        mProductPriceEdt = (EditText) findViewById(R.id.product_price_edt);
        mProductPriceEdt.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mToolbar = (Toolbar) findViewById(R.id.product_detail_toolbar);

        mProductNameEdt.setText(mProduct.isim);
        mProductPriceEdt.setText(String.valueOf(mProduct.fiyat));
        setSupportActionBar(mToolbar);

        mDrinkRadioButton = (RadioButton) findViewById(R.id.drink_radio__button);
        mDrinkRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mProductCategory = "içecek";
                }
            }
        });

        mFoodRadioButton = (RadioButton) findViewById(R.id.food_radio_button);
        mFoodRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    mProductCategory = "yemek";
                }
            }
        });

        if (mProduct.kategori.equals("yemek"))
            mFoodRadioButton.setChecked(true);
        else{
            mDrinkRadioButton.setChecked(true);
        }

        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isFieldsCorrect())
                    return;

                Product newProduct = new Product(mProductNameEdt.getText().toString().toLowerCase(),mProductCategory,Float.valueOf(mProductPriceEdt.getText().toString().trim()));
                if (newProduct.isEquals(mProduct)){
                    finish();
                }else{
                    for (String productName : mProductNames){
                        if (productName.toLowerCase().equals(newProduct.isim.trim().toLowerCase())  && !newProduct.isim.equals(mProduct.isim))
                        {
                            Toast.makeText(ProductDetailActivity.this, "Bu isimde ürün bulunmakta.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    updateProductInfo(newProduct);

                }
            }
        });
    }

    public boolean isFieldsCorrect(){
        if (mProductCategory == null){
            Toast.makeText(ProductDetailActivity.this, "Kategori seçiniz.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mProductNameEdt.getText().toString().trim().length() == 0 || mProductNameEdt.getText().toString().trim().length() == 0){
            Toast.makeText(ProductDetailActivity.this, "İlgili alanları doldurunuz.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void updateProductInfo(Product newProduct){
        mProductDbRef.child(mProduct.kategori).child(mProduct.isim).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mProductDbRef.child(newProduct.kategori).child(newProduct.isim).setValue(newProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProductDetailActivity.this, "Ürün güncellendi.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProductDetailActivity.this, "Hata: Ürün güncellenemedi.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Hata: Ürün güncellenemedi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeProduct(){
        mProductDbRef.child(mProduct.kategori).child(mProduct.isim).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProductDetailActivity.this, "Ürün silindi.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProductDetailActivity.this, "Hata: Ürün silinemedi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.greenrock.getorder.R;
import com.greenrock.getorder.model.Product;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    private DatabaseReference mProductDbRef;

    private HashMap<String,Product> mProducts;

    private String mProductCategory;

    private EditText mProductNameEdt;
    private EditText mProductPriceEdt;
    private Toolbar mToolbar;
    private Button mSaveButton;
    private RadioButton mDrinkRadioButton;
    private RadioButton mFoodRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mProducts = (HashMap<String, Product>) getIntent().getSerializableExtra("products");

        initFirebase();
        initComponents();
    }


    private void initFirebase(){
        mProductDbRef = FirebaseDatabase.getInstance().getReference("urunler");
    }

    private void initComponents() {
        mProductNameEdt = (EditText) findViewById(R.id.product_name_edt);
        mProductPriceEdt = (EditText) findViewById(R.id.product_price_edt);
        mProductPriceEdt.setInputType(InputType.TYPE_CLASS_NUMBER| InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mToolbar = (Toolbar) findViewById(R.id.add_product_toolbar);

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

        mSaveButton = (Button) findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isFieldsCorrect())
                    return;

                Product newProduct = new Product(mProductNameEdt.getText().toString().toLowerCase(),mProductCategory,Float.valueOf(mProductPriceEdt.getText().toString().trim()));
                for (Product product : mProducts.values()){
                    if (product.isim.equals(newProduct.isim)){
                        Toast.makeText(AddProductActivity.this, "Bu isimde başka bir ürün var", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                addProduct(newProduct);
                finish();
            }
        });
    }

    public boolean isFieldsCorrect(){
        if (mProductCategory == null){
            Toast.makeText(AddProductActivity.this, "Kategori seçiniz.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mProductNameEdt.getText().toString().trim().length() == 0 || mProductNameEdt.getText().toString().trim().length() == 0){
            Toast.makeText(AddProductActivity.this, "İlgili alanları doldurunuz.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void addProduct(Product newProduct){
        mProductDbRef.child(newProduct.kategori).child(newProduct.isim).setValue(newProduct).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddProductActivity.this, "Ürün başarıyla eklendi.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProductActivity.this, "Hata: Ürün eklenemedi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
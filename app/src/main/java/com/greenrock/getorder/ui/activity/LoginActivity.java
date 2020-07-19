package com.greenrock.getorder.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.greenrock.getorder.R;

public class LoginActivity extends AppCompatActivity {

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabase;

    FirebaseAuth.AuthStateListener authStateListener;
    ValueEventListener valueEventListener;

    EditText mUsernameEditText;
    EditText mPasswordEditText;
    Button mLoginButton;

    Intent loginIntent;

    private String TAG = "FirebaseTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFirebase();
        initComponents();
    }

    public void initComponents(){
        mUsernameEditText = (EditText) findViewById(R.id.username_edittext);
        mPasswordEditText = (EditText) findViewById(R.id.password_edittext);
        mLoginButton = (Button) findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseLogin(mUsernameEditText.getText().toString(),mPasswordEditText.getText().toString());
            }
        });
    }

    public void initFirebase(){
        mApp = FirebaseApp.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("calisanlar");
        mAuth = FirebaseAuth.getInstance(mApp);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Selamlar");
                    if (child.child("email").getValue(String.class).equals(mUser.getEmail())){
                        passLoginScreen(child.child("pozisyon").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Error");
            }
        };

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = mAuth.getCurrentUser();

                if (mUser != null){
                    // If user logged in
                    Log.d(TAG, "onAuthStateChanged: hiii");
                    mDatabase.addValueEventListener(valueEventListener);
                }
            }
        };

        mAuth.addAuthStateListener(authStateListener);
    }

    
    public void firebaseLogin(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //User logged in
                    Log.d(TAG, "onComplete: User successfully logged in. Uid : " + mUser.getUid() + " Email : " + mUser.getEmail());
                }else{
                    Log.d(TAG, "onComplete: Username or password is wrong");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Check network or something else");
            }
        });
    }

    public void passLoginScreen(String pozisyon){
        switch (pozisyon){
            case "garson":
                loginIntent = new Intent(LoginActivity.this,WaiterActivity.class);
                break;
            case "admin":
                loginIntent = new Intent(LoginActivity.this,AdminActivity.class);
                break;
            case "kasa":
                loginIntent = new Intent(LoginActivity.this,CashierActivity.class);
                break;
        }

        mDatabase.removeEventListener(valueEventListener);
        mAuth.removeAuthStateListener(authStateListener);
        startActivity(loginIntent);
        finish();
    }

}

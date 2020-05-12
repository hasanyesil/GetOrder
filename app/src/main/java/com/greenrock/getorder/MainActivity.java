package com.greenrock.getorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    FirebaseApp mApp;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mDatabase;

    EditText mUsernameEditText;
    EditText mPasswordEditText;
    Button mLoginButton;

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

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = mAuth.getCurrentUser();
                final String[] email = new String[1];
                if (mUser != null){
                    // If user logged in
                    Log.d(TAG, "onAuthStateChanged: User logged in");
                    Log.d(TAG, "onAuthStateChange: " + mUser.getEmail());
                    // Todo: Pass login screen
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                if (child.child("email").getValue(String.class).equals(mUser.getEmail()))
                                    Log.d(TAG, "onDataChange: Hell Yeah");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else{
                    Log.d(TAG, "onAuthStateChanged: No current user ");
                }
            }
        });
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
}

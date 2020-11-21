package com.example.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    EditText editTextEmail = null;
    EditText editTextPassword = null;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        // Initialize Firebase Auth


        if (mAuth.getCurrentUser() != null) {
            login();

        }
    }

    public void goMethod(View v) {
        //if we can login
        //else sign in
        mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            login();
                        } else {
                            // If sign in fails, sign up.
                            mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                //add to db
                                                login();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Login failed. Try Again", LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    public void login() {
        //move to next activity
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }
}



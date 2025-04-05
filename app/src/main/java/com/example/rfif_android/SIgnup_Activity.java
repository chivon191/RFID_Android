package com.example.rfif_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SIgnup_Activity extends AppCompatActivity {
    ImageButton imgbtnback_sigup;
    Button btnSignin,btntaoacc;
    EditText edtname, edtPass, edtcomfirm;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        imgbtnback_sigup=findViewById(R.id.btnback_signup);
        btnSignin=findViewById(R.id.btnSignin);
        btntaoacc=findViewById(R.id.btntaoacc);
        edtname=findViewById(R.id.edtname);
        edtPass=findViewById(R.id.edtPass);
        edtcomfirm=findViewById(R.id.edtcomfirm);

        mAuth = FirebaseAuth.getInstance();

        imgbtnback_sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(SIgnup_Activity.this,MainActivity.class);
                startActivity(myintent);
            }
        });

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(SIgnup_Activity.this,logginActive.class);
                startActivity(myintent);
            }
        });

        btntaoacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtname.getText().toString();
                String pass = edtPass.getText().toString();
                String repass = edtcomfirm.getText().toString();

                if (user.isEmpty() || pass.isEmpty() || repass.isEmpty()) {
                    Toast.makeText(SIgnup_Activity.this, "Please fill in all fields.", Toast.LENGTH_LONG).show();
                } else if (!repass.equals(pass)) {
                    Toast.makeText(SIgnup_Activity.this, "Password do not match!", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("Main", "createUserWithEmail:success");
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                Intent it = new Intent(SIgnup_Activity.this, logginActive.class);
                                it.putExtra("user", user);
                                it.putExtra("pass", pass);
                                startActivity(it);
                            } else {
                                Log.w("Main", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SIgnup_Activity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
package com.example.rfif_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SIgnup_Activity extends AppCompatActivity {
    ImageButton imgbtnback_sigup;
    Button btnSignin,btntaoacc;
    EditText edtname,edtPass,edtcomfirm;
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

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Account");
        DatabaseReference newAccount = databaseReference.push();


       // nhan nut back
       imgbtnback_sigup.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent myintent = new Intent(SIgnup_Activity.this,MainActivity.class);
               startActivity(myintent);
           }
       });

       // nhan nut chuyen sang dang nhap
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(SIgnup_Activity.this,logginActive.class);
                startActivity(myintent);
            }
        });

        // neu comfirm pass = pass thi chuyen du lieu len fb
        if(edtPass.getText().toString().equals(edtcomfirm.getText().toString())){
            btntaoacc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newAccount.child("Taikhoan").setValue(edtname.getText().toString());
                    newAccount.child("Matkhau").setValue(edtPass.getText().toString());
                    AlertDialog.Builder builder = new AlertDialog.Builder(SIgnup_Activity.this);
                    builder.setMessage("Đăng ký thành công").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    builder.create().show();
                }
            });
        }


    }
}
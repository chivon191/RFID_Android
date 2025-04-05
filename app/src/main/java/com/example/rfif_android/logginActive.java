package com.example.rfif_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class logginActive extends AppCompatActivity {
    EditText edttaikhoan,edtpass;
    TextView tv8,tv9;
    ImageButton imgbtnback;
    Button btnlogin,btndagnhap;
    String taikhoan,matkhau;

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loggin_active);

       imgbtnback=findViewById(R.id.imgback);
       btnlogin=findViewById(R.id.btnlogin);
       btndagnhap=findViewById(R.id.btndagnhap);
       edttaikhoan=findViewById(R.id.edtaccount);
       edtpass=findViewById(R.id.edtpass);
        // tro ve trang dau
       imgbtnback.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent myintent = new Intent(logginActive.this,MainActivity.class);
               startActivity(myintent);
           }
       });
       // nhan vao nut tao tai khoan
       btnlogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent myintent = new Intent(logginActive.this,SIgnup_Activity.class);
               startActivity(myintent);
           }
       });


        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Account");


        // nhan vao nut dang nhap khi nhap tai khoan mk
        btndagnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taikhoan=edttaikhoan.getText().toString();
                matkhau=edtpass.getText().toString();
                // kiem tra neu tai khoan trong
                if(taikhoan.isEmpty()){
                    edttaikhoan.setError("Tai khoan khong duoc de trong");
                    return;
                }
                if(matkhau.isEmpty()){
                    edtpass.setError("mat khau khong duoc de trong");
                    return;
                }
                //kiem tra tai khoan va mat khau trong firebase
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean adviseconnect=false;
                        // duyet qua tung con trong moi
                        for(DataSnapshot nut:snapshot.getChildren()){
                            String storetaikhoan=nut.child("Taikhoan").getValue(String.class);
                            String storematkhau=nut.child("Matkhau").getValue(String.class);
                            if(storetaikhoan!=null&&storematkhau!=null&&taikhoan.equals(storetaikhoan)&&matkhau.equals(storematkhau)){
                                adviseconnect=true;
                                break;
                            }
                        }
                        if(adviseconnect==true){
                            Intent myintent = new Intent(logginActive.this, giaodien1.class);
                            startActivity(myintent);
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(logginActive.this);
                            builder.setMessage("tai khoan khong hop le").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}
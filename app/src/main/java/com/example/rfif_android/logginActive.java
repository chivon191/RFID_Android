package com.example.rfif_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggin_active);

        imgbtnback=findViewById(R.id.imgback);
        btnlogin=findViewById(R.id.btnlogin);
        btndagnhap=findViewById(R.id.btndagnhap);
        edttaikhoan=findViewById(R.id.edtaccount);
        edtpass=findViewById(R.id.edtpass);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent!=null) {
            Bundle ex = intent.getExtras();
            if (ex!=null){
                edttaikhoan.setText(ex.getString("user"));
                edtpass.setText(ex.getString("pass"));
            }
        }

        imgbtnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(logginActive.this, MainActivity.class);
                startActivity(myintent);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(logginActive.this, SIgnup_Activity.class);
                startActivity(myintent);
            }
        });

        btndagnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edttaikhoan.getText().toString();
                String pass = edtpass.getText().toString();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(logginActive.this, "Please fill in all fields", Toast.LENGTH_LONG).show();
                } else {
                    mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(logginActive.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(logginActive.this, "Login succesfull", Toast.LENGTH_SHORT).show();
                                Intent it = new Intent(logginActive.this, giaodien1.class);
                                startActivity(it);
                            } else {
                                Log.w("Main", "signInWithEmail:failure", task.getException());
                                Toast.makeText(logginActive.this, "Fail username or password!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
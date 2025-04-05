package com.example.rfif_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button btnsignin,btnsignup;
    ImageButton imgfb,imgig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        btnsignin=findViewById(R.id.signin);
        btnsignup=findViewById(R.id.btnsignup);
        imgfb=findViewById(R.id.imgfb);
        imgig=findViewById(R.id.imgig);

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myntenr = new Intent(MainActivity.this, logginActive.class);
                startActivity(myntenr);
            }
        });
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myntenr = new Intent(MainActivity.this, SIgnup_Activity.class);
                startActivity(myntenr);
            }
        });
    }
}
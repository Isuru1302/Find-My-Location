package com.isk.indoornavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    LinearLayout scanBtn,Instructions,ContactUs,exitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        scanBtn = findViewById(R.id.scanBtn);
        Instructions = findViewById(R.id.instructions);
        ContactUs = findViewById(R.id.contact);
        exitBtn = findViewById(R.id.exitBtn);

        scanBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,barcode_scan.class);
            startActivity(intent);
        });

        Instructions.setOnClickListener(view -> {
            Intent intent2 = new Intent(MainActivity.this,instructions.class);
            startActivity(intent2);
        });

        ContactUs.setOnClickListener(view -> {
            Intent intent3 = new Intent(MainActivity.this,contactus.class);
            startActivity(intent3);
        });

        exitBtn.setOnClickListener(view -> System.exit(0));
    }
}

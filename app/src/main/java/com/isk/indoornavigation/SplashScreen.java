package com.isk.indoornavigation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIMEOUT = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getSupportActionBar().hide();

        new Handler().postDelayed(() -> {
            Intent newIntent = new Intent(SplashScreen.this,MainActivity.class);
            startActivity(newIntent);
            finish();
        },SPLASH_TIMEOUT);
    }
}

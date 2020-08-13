package com.isk.indoornavigation;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class barcode_scan extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanner = new ZXingScannerView(this);
        setContentView(scanner);
        getSupportActionBar().hide();
    }


    @Override
    public void onResume() {
        super.onResume();
        scanner.setResultHandler(this);
        scanner.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scanner.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        scanner.stopCamera();
    }
    @Override
    public void handleResult(Result result) {


        String scannedText=result.getText();
        Toast.makeText(barcode_scan.this, scannedText, Toast.LENGTH_LONG).show();

        if(scannedText.equals("northshore") || scannedText.equals("Northshore")){
            Intent newIntent = new Intent(barcode_scan.this,map_layout.class);
            startActivity(newIntent);
        }

        else{
            scanner.resumeCameraPreview(this);
        }

    }
}

package com.example.ratproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ratproject.services.RatService;

public class MainActivity extends AppCompatActivity {
    private TextView statusText;
    private Button connectButton;
    private Button startServiceButton;

    private static final int PERMISSION_REQUEST = 100;
    private static final String TAG = "MainActivity";

    private static final String SERVER_URL = "ws://192.168.1.5:8080";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        connectButton = findViewById(R.id.connectButton);
        startServiceButton = findViewById(R.id.startServiceButton);

        connectButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RatService.class);
            intent.putExtra("server_url", SERVER_URL);
            startService(intent);
            statusText.setText("Service Started");
        });

        startServiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RatService.class);
            stopService(intent);
            statusText.setText("Service Stopped");
        });

        requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
        };

        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Some permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
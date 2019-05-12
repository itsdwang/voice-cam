package com.example.voicecam;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.camerakit.CameraKitView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class CameraTestActivity extends AppCompatActivity {
    private CameraKitView cameraKitView;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int WRITE_REQUEST_CODE = 201;
    private static int imgNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);
        cameraKitView = findViewById(R.id.camera);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        } else {
            Log.d(this.getClass().getSimpleName(), "Permission granted for external storage");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(this.getClass().getSimpleName(), "Inside onRequestPermissionsResult");

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void takePhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(this.getClass().getSimpleName(), "Camera permission not granted");
        } else {
            Log.d(this.getClass().getSimpleName(), "Camera permission granted");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(this.getClass().getSimpleName(), "Audio permission not granted");
        } else {
            Log.d(this.getClass().getSimpleName(), "Audio permission granted");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(this.getClass().getSimpleName(), "Permission not granted for external storage");

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
        } else {
            Log.d(this.getClass().getSimpleName(), "Permission granted for external storage");
        }

        cameraKitView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                Log.d(this.getClass().getSimpleName(), "Inside takePhoto method, captureImage");
                Toast.makeText(getApplicationContext(), "Photo taken", Toast.LENGTH_SHORT).show();

                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File myPath = new File(directory,currentDateTimeString + ".jpg");
                imgNum++;

                FileOutputStream fos = null;
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(capturedImage, 0, capturedImage.length);

                try {
                    fos = new FileOutputStream(myPath);
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

package com.akash.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.card.MaterialCardView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    Button btnstopRing, btnSecurity, btnAccessibilityRight;
    MaterialCardView cardSettings, cardManual;
    String number = "";
    SharedPreferences sp;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            number = intent.getStringExtra("number");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("MyPref", MODE_PRIVATE);

        btnstopRing = findViewById(R.id.btnstopRing);
        btnSecurity = findViewById(R.id.btnSecurity);
        btnAccessibilityRight = findViewById(R.id.btnAccessibilityRight);
        cardSettings = findViewById(R.id.cardSettings);
        cardManual = findViewById(R.id.cardManual);

        cardSettings.setOnClickListener(v -> openSettings());

        cardManual.setOnClickListener(v -> {
            startActivity(new Intent(this, ManualActivity.class));
        });

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS,
                Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, 1);

        Intent intent = getIntent();
        if (intent != null) {
            number = intent.getStringExtra("number");
        }

        clearAppStorage();

        btnstopRing.setOnClickListener(v -> {
            if (SmsReceive.ring != null && SmsReceive.ring.isPlaying()) {
                SmsReceive.ring.stop();
                Toast.makeText(MainActivity.this, "Ring stopped", Toast.LENGTH_SHORT).show();
                if (number != null && !number.isEmpty()) {
                    try {
                        SmsManager smsManager = getSystemService(SmsManager.class);
                        smsManager.sendTextMessage(number, null, "Ringing Stopped", null, null);
                    } catch (Exception e) {}
                }
            }
        });

        btnSecurity.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });
        
        btnAccessibilityRight.setOnClickListener(v -> {
            startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));
            Toast.makeText(this, "Enable 'Security Guard' in Accessibility Settings", Toast.LENGTH_LONG).show();
        });
    }

    private void openSettings() {
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void clearAppStorage() {
        try {
            File filesDir = getExternalFilesDir(null);
            if (filesDir != null) {
                File[] files = filesDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (System.currentTimeMillis() - f.lastModified() > 1800000) f.delete();
                    }
                }
            }
            File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                File[] cacheFiles = cacheDir.listFiles();
                if (cacheFiles != null) {
                    for (File f : cacheFiles) f.delete();
                }
            }
        } catch (Exception e) {}
    }
}

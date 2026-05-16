package com.akash.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone, etOtp;
    private TextInputLayout tilOtp;
    private MaterialButton btnSendOtp, btnVerify;
    private View dividerLogin;
    private String generatedOtp;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        sp = getSharedPreferences("MyPref", MODE_PRIVATE);

        etPhone = findViewById(R.id.etPhone);
        etOtp = findViewById(R.id.etOtp);
        tilOtp = findViewById(R.id.tilOtp);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerify = findViewById(R.id.btnVerify);
        dividerLogin = findViewById(R.id.dividerLogin);

        btnSendOtp.setOnClickListener(v -> sendOtp());
        btnVerify.setOnClickListener(v -> verifyOtp());
    }

    private void sendOtp() {
        if (etPhone.getText() == null) return;
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty() || phone.length() < 10) {
            etPhone.setError("Enter a valid phone number");
            return;
        }

        // Generate a 6-digit OTP
        generatedOtp = String.format(java.util.Locale.US, "%06d", new Random().nextInt(1000000));

        try {
            SmsManager smsManager = getSystemService(SmsManager.class);
            smsManager.sendTextMessage(phone, null, "Your OTP for Login is: " + generatedOtp, null, null);
            
            Toast.makeText(this, R.string.otp_sent, Toast.LENGTH_SHORT).show();

            // Show OTP fields
            tilOtp.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.VISIBLE);
            dividerLogin.setVisibility(View.VISIBLE);
            
            // Optionally disable phone input and send button
            etPhone.setEnabled(false);
            btnSendOtp.setEnabled(false);
            
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void verifyOtp() {
        if (etOtp.getText() == null) return;
        String enteredOtp = etOtp.getText().toString().trim();
        if (enteredOtp.equals(generatedOtp)) {
            Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();
            
            // Mark as logged in
            sp.edit().putBoolean("isLoggedIn", true).apply();
            sp.edit().putString("userPhone", etPhone.getText().toString()).apply();

            // Navigate to Settings or Main
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        } else {
            etOtp.setError(getString(R.string.invalid_otp));
        }
    }
}

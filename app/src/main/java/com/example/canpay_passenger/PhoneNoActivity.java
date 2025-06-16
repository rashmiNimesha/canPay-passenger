package com.example.canpay_passenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PhoneNoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneno);

        EditText etPhone = findViewById(R.id.et_phone);
        Button btnLogin = findViewById(R.id.btn_login);
        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvTermsLink = findViewById(R.id.tv_terms_link);

        // Back button: navigate to LoginActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(PhoneNoActivity.this, LoginActivity.class);
            // Optional: clear the current activity from the stack
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Terms & Conditions link: open in browser
        tvTermsLink.setOnClickListener(v -> {
            String url = "https://www.termsfeed.com/terms-conditions-generator/";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        // Login button: validate and go to OTP activity
        btnLogin.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.isEmpty() || phone.length() < 10) {
                etPhone.setError("Enter a valid phone number");
            } else {
                // Simulate sending 6-digit code
                Toast.makeText(this, "6-digit code sent to " + phone, Toast.LENGTH_SHORT).show();
                // Navigate to OTP Activity
                Intent intent = new Intent(PhoneNoActivity.this, OtpActivity.class);
                intent.putExtra("phone_number", phone); // Pass phone number if needed
                startActivity(intent);
            }
        });
    }
}

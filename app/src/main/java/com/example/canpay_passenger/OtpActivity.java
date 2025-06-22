package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class OtpActivity extends AppCompatActivity {

    private EditText[] otpBoxes = new EditText[6];
    private TextView tvResend;
    private Button btnNext;
    private CountDownTimer timer;
    private int resendSeconds = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otpBoxes[0] = findViewById(R.id.otp1);
        otpBoxes[1] = findViewById(R.id.otp2);
        otpBoxes[2] = findViewById(R.id.otp3);
        otpBoxes[3] = findViewById(R.id.otp4);
        otpBoxes[4] = findViewById(R.id.otp5);
        otpBoxes[5] = findViewById(R.id.otp6);
        tvResend = findViewById(R.id.tv_resend);
        btnNext = findViewById(R.id.btn_next);
        ImageButton btnBack = findViewById(R.id.btn_back);

        // Auto move focus
        for (int i = 0; i < otpBoxes.length; i++) {
            final int index = i;
            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < otpBoxes.length - 1) {
                        otpBoxes[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpBoxes[index - 1].requestFocus();
                    }
                }
            });
        }

        // NEXT Button: Validate OTP and navigate to NameActivity
        btnNext.setOnClickListener(v -> {
            StringBuilder otp = new StringBuilder();
            for (EditText box : otpBoxes) {
                String digit = box.getText().toString().trim();
                if (digit.isEmpty()) {
                    Toast.makeText(this, "Enter all 6 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                otp.append(digit);
            }

            String email = getIntent().getStringExtra("email");
            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "Missing email", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2:8081/api/auth/verify-otp";

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("otp", otp.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    response -> {
                        boolean isNewUser = response.optBoolean("newUser", true);
                        if (isNewUser) {
                            Toast.makeText(this, "OTP verified. Please complete your profile.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(OtpActivity.this, NameActivity.class);
                            intent.putExtra("email", email); // pass along if needed
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            // Handle existing user login
                            // e.g., save token and navigate
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                        Log.e("OTP_VERIFY", "Error: " + error.toString());
                    }
            );

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(jsonObjectRequest);
        });


        // Back button: Navigate to PhoneNoActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(OtpActivity.this, PhoneNoActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Resend code timer
        startResendTimer();

        // Resend click

        String email = getIntent().getStringExtra("email");

        tvResend.setOnClickListener(v -> {
            if (tvResend.isEnabled()) {
                resendOtp(email); // 🔄 call resend logic
                startResendTimer();
            }
        });
    }

    private void startResendTimer() {
        tvResend.setEnabled(false);
        timer = new CountDownTimer(resendSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvResend.setText("RESEND CODE IN " + (millisUntilFinished / 1000) + " SECONDS");
            }
            public void onFinish() {
                tvResend.setText("RESEND CODE");
                tvResend.setEnabled(true);
            }
        }.start();
    }

    private void resendOtp(String email) {
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Missing email address", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8081/api/auth/send-otp"; // or your real IP

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(this, "OTP resent to " + email, Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("OTP_RESEND", "Error: " + error.toString());
                    Toast.makeText(this, "Failed to resend OTP: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
    }
}

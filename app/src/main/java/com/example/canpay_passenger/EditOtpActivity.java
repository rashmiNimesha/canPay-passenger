package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EditOtpActivity extends AppCompatActivity {

    private EditText[] otpFields = new EditText[6];
    private Button btnNext;
    private TextView tvResend, tvOtpSubtitle;
    private ImageButton btnBack;
    private String phoneNumber;
    private CountDownTimer resendTimer;
    private final int resendSeconds = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_otp);

        // Initialize views
        btnBack = findViewById(R.id.btn_back);
        tvOtpSubtitle = findViewById(R.id.tv_otp_subtitle);
        btnNext = findViewById(R.id.btn_next);
        tvResend = findViewById(R.id.tv_resend);

        // Initialize OTP fields
        otpFields[0] = findViewById(R.id.otp1);
        otpFields[1] = findViewById(R.id.otp2);
        otpFields[2] = findViewById(R.id.otp3);
        otpFields[3] = findViewById(R.id.otp4);
        otpFields[4] = findViewById(R.id.otp5);
        otpFields[5] = findViewById(R.id.otp6);

        // Get phone number from intent
        phoneNumber = getIntent().getStringExtra("phone_number");
        tvOtpSubtitle.setText("A code was sent to " + maskPhone(phoneNumber));

        // Setup OTP field listeners
        setupOtpListeners();

        // Setup back button
        btnBack.setOnClickListener(v -> finish());

        // Setup resend timer
        startResendTimer();

        // Setup resend click listener
        tvResend.setOnClickListener(v -> {
            if (tvResend.isEnabled()) {
                // Resend OTP logic here
                startResendTimer();
            }
        });

        // Setup next button
        btnNext.setOnClickListener(v -> validateOtp());
    }

    private void setupOtpListeners() {
        for (int i = 0; i < otpFields.length; i++) {
            final int currentIndex = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < 5) {
                        otpFields[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Handle backspace to move to previous field
            otpFields[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (otpFields[currentIndex].getText().length() == 0 && currentIndex > 0) {
                        otpFields[currentIndex - 1].requestFocus();
                        otpFields[currentIndex - 1].setText("");
                    }
                }
                return false;
            });
        }
    }

    private void validateOtp() {
        StringBuilder otpBuilder = new StringBuilder();
        for (EditText field : otpFields) {
            otpBuilder.append(field.getText().toString());
        }
        String otp = otpBuilder.toString();

        if (otp.length() != 6) {
            showOtpError("Please enter complete OTP code");
            return;
        }

        // Simulate OTP verification - replace with your actual API call
        if (otp.equals("123456")) {
            startActivity(new Intent(this, PhoneUpdateSuccessActivity.class));
        } else {
            startActivity(new Intent(this, PhoneUpdateFailedActivity.class));
        }
        finish();
    }

    private void showOtpError(String message) {
        // Implement your error display logic here
        // For example, show a Toast or TextView
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "******";
        return "07******" + phone.substring(phone.length() - 3);
    }

    private void startResendTimer() {
        tvResend.setEnabled(false);
        resendTimer = new CountDownTimer(resendSeconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvResend.setText("RESEND CODE IN " + (millisUntilFinished / 1000) + " SECONDS");
            }
            public void onFinish() {
                tvResend.setText("RESEND CODE");
                tvResend.setEnabled(true);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if (resendTimer != null) resendTimer.cancel();
        super.onDestroy();
    }
}

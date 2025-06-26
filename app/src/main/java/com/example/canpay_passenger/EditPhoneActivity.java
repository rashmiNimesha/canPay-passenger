package com.example.canpay_passenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditPhoneActivity extends AppCompatActivity {

    private TextInputEditText etPhone;
    private TextInputLayout tilPhone;
    private TextView tvError;
    private Button btnUpdatePhone;
    private ImageView ivBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);

        EditText etPhone = findViewById(R.id.et_phone);
        Button btnUpdatePhone = findViewById(R.id.btn_login);
        ImageButton ivBack = findViewById(R.id.btn_back);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdatePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndProceed();
            }
        });
    }

    private void validateAndProceed() {
        String phone = etPhone.getText() != null ? etPhone.getText().toString().replaceAll("\\s+", "") : "";

        if (TextUtils.isEmpty(phone)) {
            showError("Phone number cannot be empty");
            return;
        }
        if (!isValidPhone(phone)) {
            showError("Please enter a valid phone number");
            return;
        }
        showError(null);

        // Proceed to OTP activity, pass phone number
        Intent intent = new Intent(EditPhoneActivity.this, EditOtpActivity.class);
        intent.putExtra("phone_number", phone);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        if (message == null) {
            tvError.setVisibility(View.GONE);
            tilPhone.setError(null);
        } else {
            tvError.setText(message);
            tvError.setVisibility(View.VISIBLE);
            tilPhone.setError(" ");
        }
    }

    private boolean isValidPhone(String phone) {
        // Accepts 10-13 digits, no letters
        return phone.matches("^[0-9]{10,13}$") && Patterns.PHONE.matcher(phone).matches();
    }
}

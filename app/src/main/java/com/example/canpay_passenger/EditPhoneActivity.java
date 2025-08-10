package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EditPhoneActivity extends AppCompatActivity {

    private EditText etPhone;
    private TextView tvError;
    private Button btnUpdatePhone;
    private ImageButton ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phone);

        etPhone = findViewById(R.id.et_phone);
        btnUpdatePhone = findViewById(R.id.btn_login);
        ivBack = findViewById(R.id.btn_back);

        // Dynamically add the error TextView if not in XML
        tvError = findViewById(R.id.tvError);
        if (tvError == null) {
            tvError = new TextView(this);
            tvError.setId(View.generateViewId());
            tvError.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvError.setTextSize(14);
            tvError.setVisibility(View.GONE);
            LinearLayout rootLayout = (LinearLayout) findViewById(android.R.id.content).getRootView().findViewById(android.R.id.content);
            rootLayout.addView(tvError, 7); // Add error TextView after phone EditText
        }

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
        String email = "";
        if (etPhone.getText() != null) {
            email = etPhone.getText().toString().trim();
        }

        if (TextUtils.isEmpty(email)) {
            showError("Email address cannot be empty");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email address");
            return;
        }

        showError(null);

        // Proceed to OTP activity, pass email with original key "phone_number" (do not change)
        Intent intent = new Intent(EditPhoneActivity.this, EditOtpActivity.class);
        intent.putExtra("phone_number", email);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        if (message == null) {
            tvError.setVisibility(View.GONE);
        } else {
            tvError.setText(message);
            tvError.setVisibility(View.VISIBLE);
        }
    }
}

package com.example.canpay_passenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmPincodeActivity extends AppCompatActivity {

    private EditText[] pinBoxes = new EditText[4];
    private Button btnEnter;
    private String originalPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pincode);

        pinBoxes[0] = findViewById(R.id.pin1);
        pinBoxes[1] = findViewById(R.id.pin2);
        pinBoxes[2] = findViewById(R.id.pin3);
        pinBoxes[3] = findViewById(R.id.pin4);
        btnEnter = findViewById(R.id.btn_enter);
        ImageButton btnBack = findViewById(R.id.btn_back);

        originalPin = getIntent().getStringExtra("pin_code");

        // Auto focus movement
        for (int i = 0; i < pinBoxes.length; i++) {
            final int index = i;
            pinBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && index < pinBoxes.length - 1) {
                        pinBoxes[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        pinBoxes[index - 1].requestFocus();
                    }
                }
            });
        }

        btnEnter.setOnClickListener(v -> {
            StringBuilder pin = new StringBuilder();
            for (EditText box : pinBoxes) {
                String digit = box.getText().toString().trim();
                if (digit.isEmpty()) {
                    Toast.makeText(this, "Enter all 4 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                pin.append(digit);
            }

            String confirmPin = pin.toString();
            if (!confirmPin.equals(originalPin)) {
                Toast.makeText(this, "PINs do not match. Please try again.", Toast.LENGTH_SHORT).show();
                for (EditText box : pinBoxes) box.setText("");
                pinBoxes[0].requestFocus();
                return;
            }

            // ✅ Save the matched PIN locally
            SharedPreferences preferences = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
            preferences.edit().putString("user_pin", confirmPin).apply();

            Toast.makeText(this, "PIN saved locally", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });

        // Back button: Navigate to PincodeActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmPincodeActivity.this, PincodeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}

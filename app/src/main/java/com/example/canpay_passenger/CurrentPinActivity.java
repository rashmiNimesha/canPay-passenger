package com.example.canpay_passenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CurrentPinActivity extends AppCompatActivity {

    private EditText[] pinFields = new EditText[4];
    private ImageButton btnBack;
    private Button btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_pin);

        initializeViews();
        setupPinListeners();
        setupButtons();
    }

    private void initializeViews() {
        // PIN input fields
        pinFields[0] = findViewById(R.id.pin1);
        pinFields[1] = findViewById(R.id.pin2);
        pinFields[2] = findViewById(R.id.pin3);
        pinFields[3] = findViewById(R.id.pin4);

        // Buttons
        btnBack = findViewById(R.id.btn_back);
        btnEnter = findViewById(R.id.btn_enter);
    }

    private void setupPinListeners() {
        for (int i = 0; i < pinFields.length; i++) {
            final int currentIndex = i;

            pinFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < 3) {
                        // Move to next field
                        pinFields[currentIndex + 1].requestFocus();
                    } else if (s.length() == 0 && currentIndex > 0) {
                        // Move to previous field if current is empty
                        pinFields[currentIndex - 1].requestFocus();
                    }

                    // Auto-validate when all 4 digits are entered
                    if (isAllFieldsFilled()) {
                        validatePin();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Handle backspace to move to previous field
            pinFields[i].setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (pinFields[currentIndex].getText().length() == 0 && currentIndex > 0) {
                            pinFields[currentIndex - 1].requestFocus();
                            pinFields[currentIndex - 1].setText("");
                        }
                    }
                    return false;
                }
            });
        }
    }

    private void setupButtons() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePin();
            }
        });
    }

    private boolean isAllFieldsFilled() {
        for (EditText field : pinFields) {
            if (field.getText().toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void validatePin() {
        if (!isAllFieldsFilled()) {
            showError("Please enter complete 4-digit PIN");
            return;
        }

        String enteredPin = getCurrentPin();
        String savedPin = getSavedPin();

        if (savedPin == null || savedPin.isEmpty()) {
            // No PIN set yet, redirect to create PIN
            Intent intent = new Intent(this, CreateNewPinActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (enteredPin.equals(savedPin)) {
            // PIN is correct, navigate to CreateNewPinActivity
            Intent intent = new Intent(this, CreateNewPinActivity.class);
            intent.putExtra("is_change_pin", true);
            startActivity(intent);
            finish();
        } else {
            // Incorrect PIN
            showError("Incorrect PIN. Please try again.");
            clearAllFields();
        }
    }

    private String getCurrentPin() {
        StringBuilder pin = new StringBuilder();
        for (EditText field : pinFields) {
            pin.append(field.getText().toString().trim());
        }
        return pin.toString();
    }

    private String getSavedPin() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return prefs.getString("user_pin", null);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Add visual feedback - make fields shake or change color
        for (EditText field : pinFields) {
            field.setError("");
        }
    }

    private void clearAllFields() {
        for (EditText field : pinFields) {
            field.setText("");
            field.setError(null);
        }
        pinFields[0].requestFocus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Focus on first field when activity resumes
        pinFields[0].requestFocus();
    }
}

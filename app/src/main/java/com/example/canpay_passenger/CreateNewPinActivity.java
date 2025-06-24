package com.example.canpay_passenger;

import android.content.Intent;
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

public class CreateNewPinActivity extends AppCompatActivity {

    private EditText[] pinFields = new EditText[4];
    private ImageButton btnBack;
    private Button btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_pin);

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
                        validateAndSubmitPin();
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
                validateAndSubmitPin();
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

    private void validateAndSubmitPin() {
        if (!isAllFieldsFilled()) {
            showError("Please enter complete 4-digit PIN");
            return;
        }

        String pin = getCurrentPin();

        // Frontend validations
        if (!isValidPin(pin)) {
            return;
        }

        // Proceed to confirm PIN activity
        Intent intent = new Intent(this, ConfirmNewPinActivity.class);
        intent.putExtra("new_pin", pin);
        startActivity(intent);
        finish();
    }

    private String getCurrentPin() {
        StringBuilder pin = new StringBuilder();
        for (EditText field : pinFields) {
            pin.append(field.getText().toString().trim());
        }
        return pin.toString();
    }

    private boolean isValidPin(String pin) {
        // Check if PIN is exactly 4 digits
        if (pin.length() != 4) {
            showError("PIN must be exactly 4 digits");
            clearAllFields();
            return false;
        }

        // Check if PIN contains only numbers
        if (!pin.matches("\\d{4}")) {
            showError("PIN must contain only numbers");
            clearAllFields();
            return false;
        }

        // Check if PIN is not all same digits (e.g., 1111, 2222)
        if (pin.matches("(\\d)\\1{3}")) {
            showError("PIN cannot be all same digits");
            clearAllFields();
            return false;
        }

        // Check if PIN is not sequential (e.g., 1234, 4321)
        if (isSequentialPin(pin)) {
            showError("PIN cannot be sequential numbers");
            clearAllFields();
            return false;
        }

        // Check if PIN is not common weak PINs
        if (isWeakPin(pin)) {
            showError("Please choose a stronger PIN");
            clearAllFields();
            return false;
        }

        return true;
    }

    private boolean isSequentialPin(String pin) {
        // Check ascending sequence (1234, 2345, etc.)
        boolean ascending = true;
        for (int i = 1; i < pin.length(); i++) {
            if (Character.getNumericValue(pin.charAt(i)) !=
                    Character.getNumericValue(pin.charAt(i-1)) + 1) {
                ascending = false;
                break;
            }
        }

        // Check descending sequence (4321, 5432, etc.)
        boolean descending = true;
        for (int i = 1; i < pin.length(); i++) {
            if (Character.getNumericValue(pin.charAt(i)) !=
                    Character.getNumericValue(pin.charAt(i-1)) - 1) {
                descending = false;
                break;
            }
        }

        return ascending || descending;
    }

    private boolean isWeakPin(String pin) {
        // List of common weak PINs
        String[] weakPins = {
                "0000", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999",
                "1234", "4321", "1122", "2211", "1212", "2121", "0123", "3210",
                "1010", "2020", "3030", "4040", "5050", "6060", "7070", "8080", "9090"
        };

        for (String weakPin : weakPins) {
            if (pin.equals(weakPin)) {
                return true;
            }
        }
        return false;
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Add visual feedback - highlight error fields
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

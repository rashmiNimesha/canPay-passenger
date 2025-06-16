package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class PincodeActivity extends AppCompatActivity {

    private EditText[] pinBoxes = new EditText[4];
    private Button btnEnter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode);

        pinBoxes[0] = findViewById(R.id.pin1);
        pinBoxes[1] = findViewById(R.id.pin2);
        pinBoxes[2] = findViewById(R.id.pin3);
        pinBoxes[3] = findViewById(R.id.pin4);
        btnEnter = findViewById(R.id.btn_enter);
        ImageButton btnBack = findViewById(R.id.btn_back);

        // Auto move focus
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

        // Enter Button: Validate PIN and navigate to ConfirmPincodeActivity
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
            String pinStr = pin.toString();
            if (pinStr.matches("(\\d)\\1{3}")) {
                Toast.makeText(this, "PIN cannot have all digits the same", Toast.LENGTH_SHORT).show();
                return;
            }
            if ("1234".equals(pinStr) || "0000".equals(pinStr) || "4321".equals(pinStr)) {
                Toast.makeText(this, "Choose a less obvious PIN", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(PincodeActivity.this, ConfirmPincodeActivity.class);
            intent.putExtra("pin_code", pinStr);
            startActivity(intent);
            finish();
        });

        // Back button: Navigate to NameActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(PincodeActivity.this, NameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}

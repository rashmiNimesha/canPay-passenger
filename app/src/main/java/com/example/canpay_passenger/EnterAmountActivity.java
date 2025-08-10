package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EnterAmountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_amount);

        // Retrieve busId and operatorId from QrScanActivity
        String busId = getIntent().getStringExtra("busId");
        String operatorId = getIntent().getStringExtra("operatorId");

        // Display payee info
        TextView payeeInfo = findViewById(R.id.tv_payee_info);
        payeeInfo.setText("Scan successful. Enter amount to pay.");

        EditText amountEdit = findViewById(R.id.et_amount);
        Button nextButton = findViewById(R.id.btn_next);

        nextButton.setOnClickListener(v -> {
            String amount = amountEdit.getText() != null ? amountEdit.getText().toString().trim() : "";

            // Validate amount: not empty
            if (TextUtils.isEmpty(amount)) {
                amountEdit.setError("Please enter an amount");
                amountEdit.requestFocus();
                return;
            }

            // Validate amount: must be a valid positive number
            double value;
            try {
                value = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                amountEdit.setError("Enter a valid numeric amount");
                amountEdit.requestFocus();
                return;
            }

            if (value <= 0) {
                amountEdit.setError("Enter an amount greater than zero");
                amountEdit.requestFocus();
                return;
            }

            // Optional: Limit decimal places to 2 (if desired)
            if (amount.contains(".")) {
                int decimalPlaces = amount.length() - amount.indexOf('.') - 1;
                if (decimalPlaces > 2) {
                    amountEdit.setError("Amount can have up to 2 decimal places");
                    amountEdit.requestFocus();
                    return;
                }
            }

            // All validations passed, proceed to next activity
            Intent intent = new Intent(EnterAmountActivity.this, ConfirmPaymentActivity.class);
            intent.putExtra("busId", busId);
            intent.putExtra("operatorId", operatorId);
            intent.putExtra("amount", amount);
            startActivity(intent);
            finish();
        });
    }
}

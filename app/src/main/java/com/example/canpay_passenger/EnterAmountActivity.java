package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EnterAmountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_amount);

        // Get QR result from intent
        String qrResult = getIntent().getStringExtra("QR_RESULT");

        // Display payee info
        TextView payeeInfo = findViewById(R.id.tv_payee_info);
        if (qrResult != null) {
            payeeInfo.setText("You're paying to: " + qrResult);
        }

        EditText amountEdit = findViewById(R.id.et_amount);
        Button nextButton = findViewById(R.id.btn_next);

        nextButton.setOnClickListener(v -> {
            String amount = amountEdit.getText().toString().trim();

            // Validate amount
            if (TextUtils.isEmpty(amount)) {
                amountEdit.setError("Please enter an amount");
                amountEdit.requestFocus();
                return;
            }
            try {
                double value = Double.parseDouble(amount);
                if (value <= 0) {
                    amountEdit.setError("Enter a valid amount");
                    amountEdit.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                amountEdit.setError("Enter a valid amount");
                amountEdit.requestFocus();
                return;
            }

            // Start ConfirmPaymentActivity and pass data
            Intent intent = new Intent(EnterAmountActivity.this, ConfirmPaymentActivity.class);
            intent.putExtra("QR_RESULT", qrResult);
            intent.putExtra("AMOUNT", amount);
            startActivity(intent);
            finish();
        });
    }
}

package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

public class QrScanActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_amount);

        // Default UUIDs for testing
        String busId = "66666666-6666-6666-6666-666666666666";
        String operatorId = "971bbec0-5a9c-4948-8132-5a2db4666f6b";

        // Changed: Enable QR parsing for dynamic UUIDs
        String qrResult = getIntent().getStringExtra("QR_RESULT");
        if (qrResult != null) {
            try {
                // Expect QR code JSON: {"busId":"66666666-6666-6666-6666-666666666666","operatorId":"971bbec0-5a9c-4948-8132-5a2db4666f6b"}
                JSONObject json = new JSONObject(qrResult);
                busId = json.getString("busId");
                operatorId = json.getString("operatorId");
                Log.d("QrScanActivity", "Parsed QR: busId=" + busId + ", operatorId=" + operatorId);
            } catch (JSONException e) {
                Log.e("QrScanActivity", "Invalid QR code format: " + e.getMessage());
                Toast.makeText(this, "Invalid QR code format", Toast.LENGTH_SHORT).show();
            }
        }

        // Display payee info
        TextView payeeInfo = findViewById(R.id.tv_payee_info);
        payeeInfo.setText("Scan successful. Enter amount to pay.");

        EditText amountEdit = findViewById(R.id.et_amount);
        Button nextButton = findViewById(R.id.btn_next);

        String finalBusId = busId;
        String finalOperatorId = operatorId;
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

            // Changed: Log intent data
            Log.d("QrScanActivity", "Navigating to ConfirmPaymentActivity with busId=" + finalBusId + ", operatorId=" + finalOperatorId + ", amount=" + amount);

            Intent intent = new Intent(QrScanActivity.this, ConfirmPaymentActivity.class);
            intent.putExtra("busId", finalBusId);
            intent.putExtra("operatorId", finalOperatorId);
            intent.putExtra("amount", amount);
            startActivity(intent);
            finish();
        });
    }
}
package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RechargeConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_confirmation);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Get intent extras (replace with real data as needed)
        String amount = getIntent().getStringExtra("AMOUNT");
        String bankAccount = getIntent().getStringExtra("BANK_ACCOUNT");
        String dateTime = getIntent().getStringExtra("DATE_TIME");
        if (amount == null) amount = "970.00";
        if (bankAccount == null) bankAccount = "123456890 - Bank of Ceylon";
        if (dateTime == null) dateTime = "02 June 2025 - 10.30 AM";

        // Set dynamic UI values
        TextView tvTitle = findViewById(R.id.tv_recharge_title);
        tvTitle.setText("Add " + amount + " LKR to your wallet?");
        ((TextView)findViewById(R.id.tv_from)).setText(bankAccount);
        ((TextView)findViewById(R.id.tv_to)).setText("My CanPay Wallet");
        ((TextView)findViewById(R.id.tv_date_time)).setText(dateTime);

        // Make final copies for use inside lambda
        final String amountFinal = amount;
        final String bankAccountFinal = bankAccount;
        final String dateTimeFinal = dateTime;

        Button btnRecharge = findViewById(R.id.btn_recharge);
        btnRecharge.setOnClickListener(v -> {
            // Simulate recharge logic
            boolean isSuccess = simulateRecharge(amountFinal, bankAccountFinal);

            Intent intent;
            if (isSuccess) {
                intent = new Intent(RechargeConfirmationActivity.this, RechargeSuccessActivity.class);
                intent.putExtra("AMOUNT", amountFinal);
                intent.putExtra("DATE_TIME", dateTimeFinal);
            } else {
                intent = new Intent(RechargeConfirmationActivity.this, RechargeFailedActivity.class);
            }
            startActivity(intent);
            finish();
        });
    }

    // Simulate recharge logic (replace with real API/validation)
    private boolean simulateRecharge(String amount, String bankAccount) {
        try {
            double amt = Double.parseDouble(amount);
            return amt > 0 && amt < 5000;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RechargeSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_success);

        // Get data from intent
        String amount = getIntent().getStringExtra("AMOUNT");
        String bankAccount = getIntent().getStringExtra("BANK_ACCOUNT");
        String dateTime = getIntent().getStringExtra("DATE_TIME");

        if (amount == null) amount = "1000.00";
        if (bankAccount == null) bankAccount = "123456890 - Bank of Ceylon";
        if (dateTime == null) dateTime = "02 June 2025 - 10.30 AM";

        ((TextView)findViewById(R.id.tv_amount)).setText("LKR " + amount);
        ((TextView)findViewById(R.id.tv_from)).setText(bankAccount);
        ((TextView)findViewById(R.id.tv_to)).setText("My CanPay Wallet");
        ((TextView)findViewById(R.id.tv_date_time)).setText(dateTime);

        ImageButton btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            Intent intent = new Intent(RechargeSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Button btnGoHome = findViewById(R.id.btn_go_home);
        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(RechargeSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

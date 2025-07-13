package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class BankAddFailedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_add_failed);

        Button btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(v -> {
            finish(); // Go back to AddBankAccountActivity
        });
    }
}

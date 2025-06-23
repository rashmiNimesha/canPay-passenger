package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddBankAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);

        ImageButton btnBack = findViewById(R.id.btn_back);
        Spinner spinnerBank = findViewById(R.id.spinner_bank);
        EditText etAccountNumber = findViewById(R.id.et_account_number);
        EditText etAccountName = findViewById(R.id.et_account_name);
        Button btnNext = findViewById(R.id.btn_next);

        btnBack.setOnClickListener(v -> finish());

        // Set up bank spinner
        String[] banks = {
                "Select bank",
                "Bank of Ceylon",
                "Commercial Bank PLC",
                "National Savings Bank",
                "People's Bank"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, banks);
        spinnerBank.setAdapter(adapter);

        btnNext.setOnClickListener(v -> {
            String bank = spinnerBank.getSelectedItem().toString();
            String accountNumber = etAccountNumber.getText().toString().trim();
            String accountName = etAccountName.getText().toString().trim();

            // UI Validation
            if (bank.equals("Select bank")) {
                Toast.makeText(this, "Please select a bank", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(accountNumber)) {
                etAccountNumber.setError("Enter account number");
                etAccountNumber.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(accountName)) {
                etAccountName.setError("Enter account name");
                etAccountName.requestFocus();
                return;
            }

            // All good, go to pincode activity
            Intent intent = new Intent(AddBankAccountActivity.this, PincodeActivity.class);
            // Pass data if needed: intent.putExtra("BANK", bank); etc.
            startActivity(intent);
            finish();
        });
    }
}

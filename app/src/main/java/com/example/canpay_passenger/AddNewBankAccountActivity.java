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

public class AddNewBankAccountActivity extends AppCompatActivity {

    private Spinner spinnerBank;
    private EditText etAccountNumber, etAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bank_account);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        spinnerBank = findViewById(R.id.spinner_bank);
        etAccountNumber = findViewById(R.id.et_account_number);
        etAccountName = findViewById(R.id.et_account_name);
        Button btnAdd = findViewById(R.id.btn_add_account);

        // Set up spinner
        String[] banks = {
                "Select bank",
                "Bank of Ceylon",
                "Commercial Bank",
                "National Savings Bank",
                "People's Bank",
                "HSBC"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, banks);
        spinnerBank.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String bank = spinnerBank.getSelectedItem().toString();
            String accountNumber = etAccountNumber.getText().toString().trim();
            String accountName = etAccountName.getText().toString().trim();

            // Validation
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

            // Simulate result (replace with backend call)
            boolean addSuccess = simulateAddBankAccount(bank, accountNumber, accountName);

            if (addSuccess) {
                Intent intent = new Intent(this, BankAddSuccessActivity.class);
                startActivity(intent);
                setResult(RESULT_OK);
                finish();
            } else {
                Intent intent = new Intent(this, BankAddFailedActivity.class);
                startActivity(intent);
            }
        });
    }

    // Simulate backend: fail if account number is "0000", success otherwise
    private boolean simulateAddBankAccount(String bank, String accountNumber, String accountName) {
        return !accountNumber.equals("0000");
    }
}

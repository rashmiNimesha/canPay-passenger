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
import java.util.ArrayList;
import java.util.List;

public class RechargeAmountActivity extends AppCompatActivity {

    private Spinner spinnerBankAccount;
    private EditText etAmount;
    private Button btnNext;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_amount);

        initViews();
        setupBankAccounts();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        spinnerBankAccount = findViewById(R.id.spinner_bank_account);
        etAmount = findViewById(R.id.et_amount);
        btnNext = findViewById(R.id.btn_next);
    }

    private void setupBankAccounts() {
        // For now, using sample data. Replace with backend API call
        List<String> bankAccounts = getSampleBankAccounts();

        // TODO: Replace with actual backend call
        // loadBankAccountsFromBackend();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, bankAccounts);
        spinnerBankAccount.setAdapter(adapter);
    }

    // Sample bank accounts - replace with backend data
    private List<String> getSampleBankAccounts() {
        List<String> accounts = new ArrayList<>();
        accounts.add("Select bank account");
        accounts.add("Bank of Ceylon - ****2174");
        accounts.add("Commercial Bank - ****5689");
        accounts.add("People's Bank - ****1234");
        return accounts;
    }

    // TODO: Implement this method to load from your backend
    /*
    private void loadBankAccountsFromBackend() {
        // Call your partner's backend API here
        // Example:
        // ApiService.getBankAccounts(userId, new Callback<List<BankAccount>>() {
        //     @Override
        //     public void onSuccess(List<BankAccount> accounts) {
        //         updateSpinner(accounts);
        //     }
        //     
        //     @Override
        //     public void onError(String error) {
        //         // Handle error
        //     }
        // });
    }
    */

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            if (validateInputs()) {
                proceedToPayment();
            }
        });
    }

    private boolean validateInputs() {
        String selectedAccount = spinnerBankAccount.getSelectedItem().toString();
        String amount = etAmount.getText().toString().trim();

        if (selectedAccount.equals("Select bank account")) {
            Toast.makeText(this, "Please select a bank account", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(amount)) {
            etAmount.setError("Enter amount");
            etAmount.requestFocus();
            return false;
        }

        try {
            double amt = Double.parseDouble(amount);
            if (amt <= 0) {
                etAmount.setError("Enter a valid amount");
                etAmount.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Enter a valid amount");
            etAmount.requestFocus();
            return false;
        }

        return true;
    }

    private void proceedToPayment() {
        String selectedAccount = spinnerBankAccount.getSelectedItem().toString();
        String amount = etAmount.getText().toString().trim();

        // TODO: Implement payment confirmation screen or direct payment processing
        Intent intent = new Intent(this, RechargeConfirmationActivity.class);
        intent.putExtra("BANK_ACCOUNT", selectedAccount);
        intent.putExtra("AMOUNT", amount);
        startActivity(intent);
        finish();
    }
}

package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class EditBankAccountActivity extends AppCompatActivity {

    private Spinner spinnerBank;
    private EditText etAccountNumber, etAccountName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bank_account);

        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnClose = findViewById(R.id.btn_close);

        btnBack.setOnClickListener(v -> finish());
        btnClose.setOnClickListener(v -> showRemoveAccountDialog());

        spinnerBank = findViewById(R.id.spinner_bank);
        etAccountNumber = findViewById(R.id.et_account_number);
        etAccountName = findViewById(R.id.et_account_name);
        Button btnUpdate = findViewById(R.id.btn_update_account);

        // Set up spinner
        String[] banks = {
                "Bank of Ceylon",
                "Commercial Bank",
                "National Savings Bank",
                "People's Bank",
                "HSBC"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, banks);
        spinnerBank.setAdapter(adapter);

        btnUpdate.setOnClickListener(v -> {
            String bank = spinnerBank.getSelectedItem().toString();
            String accountNumber = etAccountNumber.getText().toString().trim();
            String accountName = etAccountName.getText().toString().trim();

            // Validation
            if (TextUtils.isEmpty(bank)) {
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

            // Simulate update result
            boolean updateSuccess = simulateBankUpdate(bank, accountNumber, accountName);

            if (updateSuccess) {
                Intent intent = new Intent(this, BankUpdateSuccessActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, BankUpdateFailedActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // NEW: Modern Bottom Sheet for Remove Account Confirmation
    private void showRemoveAccountDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_remove_bank_account, null);

        Button btnRemove = view.findViewById(R.id.btn_remove_account);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        btnRemove.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            removeAccount();
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();
    }

    private void removeAccount() {
        // Simulate account removal
        Toast.makeText(this, "Bank account removed successfully", Toast.LENGTH_SHORT).show();

        // Navigate back to bank accounts list
        Intent intent = new Intent(this, BankAccountListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // Simulate backend update
    private boolean simulateBankUpdate(String bank, String accountNumber, String accountName) {
        return !accountNumber.equals("0000");
    }
}

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
import com.android.volley.VolleyError;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.Endpoints;
import com.example.canpay_passenger.utils.PreferenceManager;
import org.json.JSONException;
import org.json.JSONObject;

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
                "HSBC",
                "Seylan Bank"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, banks);
        spinnerBank.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String bank = spinnerBank.getSelectedItem().toString();
            String accountNumberStr = etAccountNumber.getText().toString().trim();
            String accountName = etAccountName.getText().toString().trim();

            // Bank validation
            if (bank.equals("Select bank")) {
                Toast.makeText(this, "Please select a bank", Toast.LENGTH_SHORT).show();
                return;
            }

            // Account number validations
            if (TextUtils.isEmpty(accountNumberStr)) {
                etAccountNumber.setError("Enter account number");
                etAccountNumber.requestFocus();
                return;
            }
            if (!accountNumberStr.matches("\\d+")) {
                etAccountNumber.setError("Account number must contain digits only");
                etAccountNumber.requestFocus();
                return;
            }
            if (accountNumberStr.length() > 15) {
                etAccountNumber.setError("Account number must be at most 15 digits");
                etAccountNumber.requestFocus();
                return;
            }
            if (accountNumberStr.startsWith("0")) {
                etAccountNumber.setError("Account number cannot start with zero");
                etAccountNumber.requestFocus();
                return;
            }
            long accountNumber;
            try {
                accountNumber = Long.parseLong(accountNumberStr);
                if (accountNumber <= 0) {
                    etAccountNumber.setError("Invalid account number");
                    etAccountNumber.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etAccountNumber.setError("Invalid account number format");
                etAccountNumber.requestFocus();
                return;
            }

            // Account name validations
            if (TextUtils.isEmpty(accountName)) {
                etAccountName.setError("Enter account name");
                etAccountName.requestFocus();
                return;
            }
            if (!accountName.matches("[a-zA-Z ]+")) {
                etAccountName.setError("Account name must contain letters and spaces only");
                etAccountName.requestFocus();
                return;
            }
            if (accountName.length() > 50) {
                etAccountName.setError("Account name must be at most 50 characters");
                etAccountName.requestFocus();
                return;
            }

            sendBankDetails(bank, accountNumber, accountName);
        });
    }

    private void sendBankDetails(String bank, long accountNumber, String accountName) {
        String token = PreferenceManager.getToken(this);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PhoneNoActivity.class));
            finish();
            return;
        }

        // Create JSON payload
        JSONObject body = new JSONObject();
        try {
            body.put("bankName", bank);
            body.put("accountNumber", accountNumber);
            body.put("accountName", accountName);
            body.put("isDefault", false); // Hardcode to false as per backend change
        } catch (JSONException e) {
            Toast.makeText(this, "Error building request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Make API call
        ApiHelper.postJson(this, Endpoints.ADD_BANK_ACCOUNT, body, token, new ApiHelper.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        Toast.makeText(AddNewBankAccountActivity.this, "Bank account added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNewBankAccountActivity.this, BankAddSuccessActivity.class);
                        startActivity(intent);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String message = response.optString("message", "Failed to add bank account");
                        Toast.makeText(AddNewBankAccountActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddNewBankAccountActivity.this, BankAddFailedActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Toast.makeText(AddNewBankAccountActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddNewBankAccountActivity.this, BankAddFailedActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(VolleyError error) {
                ApiHelper.handleVolleyError(AddNewBankAccountActivity.this, error, "AddNewBankAccountActivity");
                Intent intent = new Intent(AddNewBankAccountActivity.this, BankAddFailedActivity.class);
                startActivity(intent);
            }
        });
    }
}

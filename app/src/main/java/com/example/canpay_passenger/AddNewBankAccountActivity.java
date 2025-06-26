package com.example.canpay_passenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
            sendBankDetails(bank, accountNumber, accountName);

        });


    }
    private void sendBankDetails(String bank, String accountNumber, String accountName) {
        SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String token = prefs.getString("token", null);

        if (email == null || token == null) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8081/api/v1/user-service/passenger-account";

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("accName", accountName);
            body.put("accNo", accountNumber);
            body.put("bank", bank);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error building request", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH,
                url,
                body,
                response -> {
                    Intent intent = new Intent(this, BankAddSuccessActivity.class);
                    startActivity(intent);
                    setResult(RESULT_OK);
                    finish();
                },
                error -> {
                    error.printStackTrace();
                    Intent intent = new Intent(this, BankAddFailedActivity.class);
                    startActivity(intent);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}

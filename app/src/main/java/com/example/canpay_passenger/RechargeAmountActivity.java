package com.example.canpay_passenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if (email != null) {
            loadBankAccountsFromBackend(email);
        } else {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
        }
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        spinnerBankAccount = findViewById(R.id.spinner_bank_account);
        etAmount = findViewById(R.id.et_amount);
        btnNext = findViewById(R.id.btn_next);
    }

    private void loadBankAccountsFromBackend(String email) {
        String url = "http://10.0.2.2:8081/api/v1/bank-account/by-email?email=" + email;
        Log.d("RECHARGE", "Requesting bank accounts from: " + url);

        SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Missing token", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    List<String> accounts = new ArrayList<>();
                    accounts.add("Select bank account");

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            String bank = obj.getString("bank");
                            String acc = obj.getString("accountNumber");
                            String masked = "****" + acc.substring(Math.max(0, acc.length() - 4));
                            accounts.add(bank + " - " + masked);
                        } catch (JSONException e) {
                            Log.e("RECHARGE", "JSON parse error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                RechargeAmountActivity.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                accounts
                        );
                        spinnerBankAccount.setAdapter(adapter);
                    });
                },
                error -> {
                    Log.e("RECHARGE", "Failed to fetch bank accounts: " + error.toString());
                    Toast.makeText(this, "Failed to load bank accounts", Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

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

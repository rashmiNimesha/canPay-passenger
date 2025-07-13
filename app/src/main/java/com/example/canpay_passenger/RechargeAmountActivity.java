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
import com.example.canpay_passenger.config.ApiConfig;
import com.example.canpay_passenger.request.VolleySingleton;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.Endpoints;
import com.example.canpay_passenger.utils.JwtUtils;
import com.example.canpay_passenger.utils.PreferenceManager;

import org.json.JSONArray;
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
    private  String userEmail, token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_amount);

        initViews();
        setupClickListeners();

        token = PreferenceManager.getToken(this);

        if (token != null) {
            userEmail = JwtUtils.getEmailFromToken(token); // extract from JWT
            Log.d("RECHARGE", "Token User: " + userEmail);
            loadBankAccountsFromBackend(token); // send only token, not email or role
        } else {
            Toast.makeText(this, "Missing token", Toast.LENGTH_SHORT).show();
        }

    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        spinnerBankAccount = findViewById(R.id.spinner_bank_account);
        etAmount = findViewById(R.id.et_amount);
        btnNext = findViewById(R.id.btn_next);
    }

    private void loadBankAccountsFromBackend(String token) {
        String url = ApiConfig.getBaseUrl() + "/api/v1/bank-account/by-email";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    List<String> accounts = parseBankAccounts(response);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            accounts
                    );
                    spinnerBankAccount.setAdapter(adapter);
                },
                error -> ApiHelper.handleVolleyError(this, error, "RECHARGE")
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private List<String> parseBankAccounts(JSONArray response) {
        List<String> accounts = new ArrayList<>();
        accounts.add("Select bank account");

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                String bank = obj.getString("bankName");
                String acc = obj.getString("accountNumber");
                String masked = "****" + acc.substring(Math.max(0, acc.length() - 4));
                accounts.add(bank + " - " + masked);
            } catch (JSONException e) {
                Log.e("RECHARGE", "JSON parse error", e);
            }
        }

        return accounts;
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

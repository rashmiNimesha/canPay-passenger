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
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.canpay_passenger.utils.JwtUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
            if (!btnNext.isEnabled()) return;
            btnNext.setEnabled(false);

            String bank = spinnerBank.getSelectedItem().toString();
            String accountNumber = etAccountNumber.getText().toString().trim();
            String accountName = etAccountName.getText().toString().trim();

            if (bank.equals("Select bank")) {
                Toast.makeText(this, "Please select a bank", Toast.LENGTH_SHORT).show();
                btnNext.setEnabled(true); // re-enable on validation fail
                return;
            }
            if (TextUtils.isEmpty(accountNumber)) {
                etAccountNumber.setError("Enter account number");
                etAccountNumber.requestFocus();
                btnNext.setEnabled(true); // re-enable on validation fail
                return;
            }
            if (TextUtils.isEmpty(accountName)) {
                etAccountName.setError("Enter account name");
                etAccountName.requestFocus();
                btnNext.setEnabled(true); // re-enable on validation fail
                return;
            }

            // Get values from previous screens
            String name = getIntent().getStringExtra("name");
            String email = getIntent().getStringExtra("email");
            String nic = getIntent().getStringExtra("nic");

            if (email == null || name == null || nic == null) {
                Toast.makeText(this, "Missing user data", Toast.LENGTH_SHORT).show();
                btnNext.setEnabled(true); // re-enable
                return;
            }

            String url = "http://10.0.2.2:8081/api/v1/auth/create-profile";

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("name", name);
                jsonBody.put("nic", nic);
                jsonBody.put("accNo", accountNumber);
                jsonBody.put("bank", bank);
                jsonBody.put("accName", accountName);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
                btnNext.setEnabled(true);
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject profile = data.getJSONObject("profile");
                            String emailFromResponse = profile.getString("email");
                            String token = data.getString("token");

                            // Decode using JwtUtils
                            String role = JwtUtils.getRoleFromToken(token);
                            String userEmail = JwtUtils.getEmailFromToken(token);
                            String userName = JwtUtils.getNameFromToken(token);
                            int userId = JwtUtils.getUserIdFromToken(token);
                            String nicToken = JwtUtils.getNicFromToken(token);

                            SharedPreferences preferences = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("token", token);
                            editor.putString("role", role);
                            editor.putString("email", userEmail);
                            editor.putString("user_name", userName);
                            editor.putInt("user_id", userId);
                            editor.putString("nic", nicToken);
                            editor.apply();

                            Log.d("TOKEN_DECODE", "Saved role: " + role + ", id: " + userId);
                            Toast.makeText(this, "Profile created successfully", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AddBankAccountActivity.this, PincodeActivity.class);
                            intent.putExtra("email", emailFromResponse);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            Log.e("PARSE_ERROR", "Failed to parse response: " + e.getMessage());
                            Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                            btnNext.setEnabled(true);
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Failed to create profile", Toast.LENGTH_SHORT).show();
                        Log.e("CREATE_PROFILE", "Error: " + error.toString());
                        btnNext.setEnabled(true);
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        });


    }
}

package com.example.canpay_passenger;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
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
import com.android.volley.VolleyError;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.Endpoints;
import com.example.canpay_passenger.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;


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
            // Retrieve token from SharedPreferences
            String token = PreferenceManager.getToken(this);
            if (token == null) {
                Log.e(TAG, "No token found in SharedPreferences");
                Toast.makeText(this, "Authentication token missing", Toast.LENGTH_LONG).show();
                btnNext.setEnabled(true);
                return;
            }


            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("name", name);
                jsonBody.put("nic", nic);
                jsonBody.put("accNo", accountNumber);
                jsonBody.put("bank", bank);
                jsonBody.put("accName", accountName);
            } catch (JSONException e) {
                Log.e(TAG, "JSON creation error", e);
                Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
                btnNext.setEnabled(true);
                return;
            }


            ApiHelper.postJson(this, Endpoints.CREATE_PROFILE, jsonBody, token, new ApiHelper.Callback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject profile = data.getJSONObject("profile");
                        String emailFromResponse = profile.getString("email");
                        String newToken = data.getString("token");
                        String userRole = profile.getString("role");
                        String userName = profile.optString("name", null);
                        String nic = profile.optString("nic", null);
                        int userId = profile.optInt("id", 0);


                        // Save updated session data
                        PreferenceManager.saveUserSession(
                                AddBankAccountActivity.this,
                                emailFromResponse,
                                newToken,
                                userRole,
                                userName,
                                userId,
                              nic
                        );

                        Log.d(TAG, "Profile created for email: " + emailFromResponse);
                        Toast.makeText(AddBankAccountActivity.this, "Profile created successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AddBankAccountActivity.this, PincodeActivity.class);
                        intent.putExtra("email", emailFromResponse);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(AddBankAccountActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show();
                        btnNext.setEnabled(true);
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    ApiHelper.handleVolleyError(AddBankAccountActivity.this, error, TAG);
                    btnNext.setEnabled(true);
                }
            });


        });


    }
}

 package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.VolleyError;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.Endpoints;
import com.example.canpay_passenger.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class RechargeConfirmationActivity extends AppCompatActivity {
    private static final String TAG = "RechargeConfirmationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_confirmation);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        String amount = getIntent().getStringExtra("AMOUNT");
        String bankAccount = getIntent().getStringExtra("BANK_ACCOUNT");
        String dateTime = getIntent().getStringExtra("DATE_TIME");

        TextView tvTitle = findViewById(R.id.tv_recharge_title);
        tvTitle.setText("Add " + amount + " LKR to your wallet?");
        ((TextView) findViewById(R.id.tv_from)).setText(bankAccount);
        ((TextView) findViewById(R.id.tv_to)).setText("My CanPay Wallet");
        ((TextView) findViewById(R.id.tv_date_time)).setText(dateTime);

        final String amountFinal = amount;
        final String bankAccountFinal = bankAccount;
        final String dateTimeFinal = dateTime;

        Button btnRecharge = findViewById(R.id.btn_recharge);

        btnRecharge.setOnClickListener(v -> {
            String email = PreferenceManager.getEmail(this);
            String token = PreferenceManager.getToken(this);

            if (email == null || amountFinal == null || token == null) {
                Toast.makeText(this, "Missing required info", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject body = new JSONObject();
            try {
                body.put("email", email);
                body.put("amount", amountFinal);
            } catch (JSONException e) {
                Log.e(TAG, "Error building request", e);
                Toast.makeText(this, "Error building request", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Posting to URL: " + Endpoints.PASSENGER_WALLET_RECHARGE + ", with body: " + body.toString());

            ApiHelper.postJson(this, Endpoints.PASSENGER_WALLET_RECHARGE, body, token, new ApiHelper.Callback() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject dataWallet = data.getJSONObject("dataWallet");
                            double balance = dataWallet.getDouble("balance");
                            String walletNumber = dataWallet.optString("walletNumber", "N/A");
                            String accountName = dataWallet.optString("accountName", "N/A");

                            Log.d(TAG, "Recharge successful: email=" + email + ", amount=" + amountFinal +
                                    ", new balance=" + balance + ", walletNumber=" + walletNumber +
                                    ", accountName=" + accountName);

                            // Navigate to success activity
                            Intent intent = new Intent(RechargeConfirmationActivity.this, RechargeSuccessActivity.class);
                            intent.putExtra("AMOUNT", amountFinal);
                            intent.putExtra("BANK_ACCOUNT", bankAccountFinal);
                            intent.putExtra("DATE_TIME", dateTimeFinal);
                            intent.putExtra("BALANCE", String.valueOf(balance));
                            intent.putExtra("WALLET_NUMBER", walletNumber);
                            intent.putExtra("ACCOUNT_NAME", accountName);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = response.optString("message", "Failed to recharge wallet");
                            Log.e(TAG, "Recharge failed: " + message);
                            Toast.makeText(RechargeConfirmationActivity.this, message, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RechargeConfirmationActivity.this, RechargeFailedActivity.class));
                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing response", e);
                        Toast.makeText(RechargeConfirmationActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RechargeConfirmationActivity.this, RechargeFailedActivity.class));
                        finish();
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e(TAG, "Volley error", error);
                    ApiHelper.handleVolleyError(RechargeConfirmationActivity.this, error, TAG);
                    startActivity(new Intent(RechargeConfirmationActivity.this, RechargeFailedActivity.class));
                    finish();
                }
            });
        });
    }
}

package com.example.canpay_passenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class RechargeConfirmationActivity extends AppCompatActivity {

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
        ((TextView)findViewById(R.id.tv_from)).setText(bankAccount);
        ((TextView)findViewById(R.id.tv_to)).setText("My CanPay Wallet");
        ((TextView)findViewById(R.id.tv_date_time)).setText(dateTime);

        final String amountFinal = amount;
        final String bankAccountFinal = bankAccount;
        final String dateTimeFinal = dateTime;

        Button btnRecharge = findViewById(R.id.btn_recharge);

        btnRecharge.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
            String email = prefs.getString("email", null);
            String token = prefs.getString("token", null); // 🔐 get token

            if (email == null || amountFinal == null || token == null) {
                Toast.makeText(this, "Missing required info", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2:8081/api/v1/wallet/recharge";

            JSONObject body = new JSONObject();
            try {
                body.put("email", email);
                body.put("amount", amountFinal);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, body,
                    response -> {
                        Intent intent = new Intent(RechargeConfirmationActivity.this, RechargeSuccessActivity.class);
                        intent.putExtra("AMOUNT", amountFinal);
                        intent.putExtra("BANK_ACCOUNT", bankAccountFinal);
                        intent.putExtra("DATE_TIME", dateTimeFinal);
                        startActivity(intent);
                        finish();
                    },
                    error -> {
                        Intent intent = new Intent(RechargeConfirmationActivity.this, RechargeFailedActivity.class);
                        startActivity(intent);
                        finish();
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
        });
    }
}

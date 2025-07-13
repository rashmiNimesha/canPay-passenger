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
import com.example.canpay_passenger.utils.PreferenceManager;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfirmPaymentActivity extends AppCompatActivity {
    String amount, conductor, busNumber, busRoute, busId, operatorId;
    private Button swipeToPay;
    private TextView tvTitle, tvBusNumber, tvBusRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);

        // Retrieve intent data
        busId = getIntent().getStringExtra("busId");
        operatorId = getIntent().getStringExtra("operatorId");
        amount = getIntent().getStringExtra("amount");

        // Initialize UI with placeholders to avoid null references
        tvTitle = findViewById(R.id.tv_confirm_title);
        tvBusNumber = findViewById(R.id.tv_bus_number);
        tvBusRoute = findViewById(R.id.tv_bus_route);
        try {
            tvTitle.setText("Pay " + (amount != null ? amount : "0") + " LKR");
            tvBusNumber.setText("Loading...");
            tvBusRoute.setText("Loading...");
        } catch (Exception e) {
            Log.e("ConfirmPaymentActivity", "Error initializing UI: " + e.getMessage());
            Toast.makeText(this, "Error initializing UI", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        swipeToPay = findViewById(R.id.btn_swipe_to_pay);
        swipeToPay.setOnClickListener(v -> processPayment());

        Log.d("ConfirmPaymentActivity", "Received: busId=" + busId + ", operatorId=" + operatorId + ", amount=" + amount);

        fetchBusAndOperatorDetails();
    }

    private void fetchBusAndOperatorDetails() {
        String token = PreferenceManager.getToken(this);
        if (token == null) {
            Log.e("ConfirmPaymentActivity", "No JWT token found");
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PhoneNoActivity.class));
            finish();
            return;
        }

        String endpoint = "/api/v1/bus/" + busId + "/operator/" + operatorId;
        Log.d("ConfirmPaymentActivity", "Fetching details from: " + endpoint);

        ApiHelper.getJson(this, endpoint, token, new ApiHelper.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");
                        busNumber = data.getString("busNumber");
                        busRoute = data.getString("busRoute");
                        conductor = data.getString("operatorName");

                        tvTitle.setText("Pay " + amount + " LKR to " + conductor + "?");
                        tvBusNumber.setText(busNumber);
                        tvBusRoute.setText(busRoute);
                    } else {
                        Log.e("ConfirmPaymentActivity", "API error: " + response.getString("message"));
                        Toast.makeText(ConfirmPaymentActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (Exception e) {
                    Log.e("ConfirmPaymentActivity", "Error parsing response: " + e.getMessage());
                    Toast.makeText(ConfirmPaymentActivity.this, "Error parsing response", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onError(VolleyError error) {
                String errorMessage = "Error fetching details: " + (error.networkResponse != null ? new String(error.networkResponse.data) : error.toString());
                Log.e("ConfirmPaymentActivity", errorMessage);
                ApiHelper.handleVolleyError(ConfirmPaymentActivity.this, error, "ConfirmPaymentActivity");
                finish();
            }
        });
    }

    private void processPayment() {
        String token = PreferenceManager.getToken(this);
        if (token == null) {
            Log.e("ConfirmPaymentActivity", "No JWT token found for payment");
            Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PhoneNoActivity.class));
            finish();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("busId", busId);
            requestBody.put("operatorId", operatorId);
            requestBody.put("amount", amount);
        } catch (Exception e) {
            Log.e("ConfirmPaymentActivity", "Error preparing payment: " + e.getMessage());
            Toast.makeText(this, "Error preparing payment", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("ConfirmPaymentActivity", "Payment request: " + requestBody.toString());

        ApiHelper.postJson(this, "/api/v1/payment/process", requestBody, token, new ApiHelper.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");
                        String busNumber = data.getString("busNumber");
                        String operatorName = data.getString("operatorName");
                        String dateTime = new SimpleDateFormat("dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(new Date());

                        Intent intent = new Intent(ConfirmPaymentActivity.this, PaymentSuccessActivity.class);
                        intent.putExtra("AMOUNT", amount);
                        intent.putExtra("CONDUCTOR", operatorName);
                        intent.putExtra("BUS_NUMBER", busNumber);
                        intent.putExtra("BUS_ROUTE", busRoute);
                        intent.putExtra("DATE_TIME", dateTime);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e("ConfirmPaymentActivity", "Payment failed: " + response.getString("message"));
                        Intent intent = new Intent(ConfirmPaymentActivity.this, PaymentFailedActivity.class);
                        intent.putExtra("ERROR_MESSAGE", response.getString("message"));
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    Log.e("ConfirmPaymentActivity", "Error processing response: " + e.getMessage());
                    Toast.makeText(ConfirmPaymentActivity.this, "Error processing response", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                String errorMessage = "Payment failed: " + (error.networkResponse != null ? new String(error.networkResponse.data) : error.toString());
                Log.e("ConfirmPaymentActivity", errorMessage);
                Intent intent = new Intent(ConfirmPaymentActivity.this, PaymentFailedActivity.class);
                intent.putExtra("ERROR_MESSAGE", errorMessage);
                startActivity(intent);
                finish();
            }
        });
    }
}
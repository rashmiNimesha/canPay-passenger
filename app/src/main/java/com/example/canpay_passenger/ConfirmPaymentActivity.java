package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmPaymentActivity extends AppCompatActivity {

    String qrResult, amount, conductor, busNumber, busRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);

        // Get data from intent
        qrResult = getIntent().getStringExtra("QR_RESULT");
        amount = getIntent().getStringExtra("AMOUNT");
        // For demo, set static values. In real app, parse qrResult for these.
        conductor = "Gamage";
        busNumber = "ND - 1234";
        busRoute = "Matara - Makumbura";

        // Set UI
        TextView tvTitle = findViewById(R.id.tv_confirm_title);
        tvTitle.setText("Pay " + amount + " LKR to " + conductor + "?");
        ((TextView)findViewById(R.id.tv_bus_number)).setText(busNumber);
        ((TextView)findViewById(R.id.tv_bus_route)).setText(busRoute);

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        Button swipeToPay = findViewById(R.id.btn_swipe_to_pay);
        swipeToPay.setOnClickListener(v -> {
            // Simulate payment logic
            boolean paymentSuccess = simulatePayment(amount);

            Intent intent;
            if (paymentSuccess) {
                intent = new Intent(ConfirmPaymentActivity.this, PaymentSuccessActivity.class);
                intent.putExtra("AMOUNT", amount);
                intent.putExtra("CONDUCTOR", conductor);
                intent.putExtra("BUS_NUMBER", busNumber);
                intent.putExtra("BUS_ROUTE", busRoute);
                intent.putExtra("DATE_TIME", "02 June 2025 - 10.30 AM"); // For demo
            } else {
                intent = new Intent(ConfirmPaymentActivity.this, PaymentFailedActivity.class);
            }
            startActivity(intent);
            finish();
        });
    }

    // Simulate payment (replace with real logic)
    private boolean simulatePayment(String amount) {
        try {
            double amt = Double.parseDouble(amount);
            return amt > 0 && amt < 10000; // Success if amount is less than 10000
        } catch (Exception e) {
            return false;
        }
    }
}

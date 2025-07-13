package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // Get data from intent
        String amount = getIntent().getStringExtra("AMOUNT");
        String conductor = getIntent().getStringExtra("CONDUCTOR");
        String busNumber = getIntent().getStringExtra("BUS_NUMBER");
        String busRoute = getIntent().getStringExtra("BUS_ROUTE");
        String dateTime = getIntent().getStringExtra("DATE_TIME");

        ((TextView)findViewById(R.id.tv_paid_amount)).setText("LKR " + amount);
        ((TextView)findViewById(R.id.tv_conductor)).setText(conductor);
        ((TextView)findViewById(R.id.tv_bus_number)).setText(busNumber);
        ((TextView)findViewById(R.id.tv_bus_route)).setText(busRoute);
        ((TextView)findViewById(R.id.tv_date_time)).setText(dateTime);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
        findViewById(R.id.btn_go_home).setOnClickListener(v -> {
            Intent intent = new Intent(PaymentSuccessActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NICEntryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nicentry);

        EditText etNic = findViewById(R.id.et_nic);
        Button btnNext = findViewById(R.id.btn_next);
        ImageButton btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> {
            String nic = etNic.getText().toString().trim();
            if (TextUtils.isEmpty(nic)) {
                etNic.setError("Please enter your NIC number");
                etNic.requestFocus();
                return;
            }
            // No validation, just go to next activity
            Intent intent = new Intent(NICEntryActivity.this, AddBankAccountActivity.class);
            intent.putExtra("NIC", nic); // Optional: pass NIC if needed
            startActivity(intent);
            finish();
        });
    }
}

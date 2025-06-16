package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        EditText etName = findViewById(R.id.et_name);
        Button btnNext = findViewById(R.id.btn_next);
        ImageButton btnBack = findViewById(R.id.btn_back);

        // Back button: navigate to OtpActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(NameActivity.this, OtpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // NEXT button: validate and go to PincodeActivity
        btnNext.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Please enter your name");
            } else {
                Intent intent = new Intent(NameActivity.this, PincodeActivity.class);
                intent.putExtra("user_name", name);
                startActivity(intent);
                finish();
            }
        });
    }
}

package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");

        btnNext.setOnClickListener(v -> {
            String nic = etNic.getText().toString().trim();

            if (TextUtils.isEmpty(nic)) {
                etNic.setError("Please enter your NIC number");
                etNic.requestFocus();
                return;
            }

            if (!isValidNIC(nic)) {
                etNic.setError("Invalid NIC format. Use 12 digits or 9 digits + 'V'/'v'");
                etNic.requestFocus();
                return;
            }

            Intent intent = new Intent(NICEntryActivity.this, AddBankAccountActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("nic", nic);
            startActivity(intent);
            finish();
        });
    }

    private boolean isValidNIC(String nic) {
        // Check for 12 digits
        if (nic.matches("^\\d{12}$")) {
            return true;
        }
        // Check for 9 digits followed by 'V' or 'v'
        if (nic.matches("^\\d{9}[Vv]$")) {
            return true;
        }
        return false;
    }
}

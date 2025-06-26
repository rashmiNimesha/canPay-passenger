package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditNameActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputLayout tilName;
    private Button btnUpdateName;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        EditText etName = findViewById(R.id.etName);
        btnUpdateName = findViewById(R.id.btnUpdateName);
        btnBack = findViewById(R.id.btn_back);

        // Optionally set current name if available
        // etName.setText(getIntent().getStringExtra("current_name"));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to ProfileActivity
                Intent intent = new Intent(EditNameActivity.this, ProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });

        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmit();
            }
        });
    }

    private void validateAndSubmit() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";

        // Validation: not empty, min 2 chars, only letters and spaces
        if (TextUtils.isEmpty(name)) {
            tilName.setError("Name cannot be empty");
            return;
        }
        if (name.length() < 2) {
            tilName.setError("Name must be at least 2 characters");
            return;
        }
        if (!name.matches("^[a-zA-Z .'-]+$")) {
            tilName.setError("Name contains invalid characters");
            return;
        }

        tilName.setError(null);

        // Simulate backend call (replace with your real API logic)
        boolean isSuccess = Math.random() > 0.2; // 80% chance success for demo

        if (isSuccess) {
            Intent intent = new Intent(EditNameActivity.this, NameUpdateSuccessActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(EditNameActivity.this, NameUpdateFailedActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

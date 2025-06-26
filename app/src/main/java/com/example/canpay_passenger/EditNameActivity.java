package com.example.canpay_passenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
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

public class EditNameActivity extends AppCompatActivity {

    private EditText etName;
    private TextView tvError;
    private Button btnUpdateName;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);

        etName = findViewById(R.id.etName);
        tvError = findViewById(R.id.tvError);
        btnUpdateName = findViewById(R.id.btnUpdateName);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        tvError.setVisibility(View.GONE); // Hide error by default

        if (TextUtils.isEmpty(name)) {
            showError("Name cannot be empty");
            return;
        }
        if (name.length() < 2) {
            showError("Name must be at least 2 characters");
            return;
        }
        if (!name.matches("^[a-zA-Z .'-]+$")) {
            showError("Name contains invalid characters");
            return;
        }

        updateNameOnBackend(name);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void updateNameOnBackend(String newName) {
        SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);
        Log.d("EDIT", "email: " + email );

        String token = prefs.getString("token", null);
        Log.d("EDIT", "token: " + token );

        if (email == null || token == null) {
            Toast.makeText(this, "User session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8081/api/v1/user-service/passenger-account";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("name", newName);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PATCH, url, requestBody,
                response -> {
                    try {
                        boolean success = response.optBoolean("success", false);
                        if (success) {
                            JSONObject profile = response.getJSONObject("data").getJSONObject("profile");
                            String updatedName = profile.optString("name", newName);

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("user_name", updatedName);
                            editor.apply();

                            Intent intent = new Intent(EditNameActivity.this, NameUpdateSuccessActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(EditNameActivity.this, NameUpdateFailedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("NAME_UPDATE", "Error: " + error.toString());
                    Toast.makeText(this, "Failed to update name", Toast.LENGTH_SHORT).show();
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
    }
}

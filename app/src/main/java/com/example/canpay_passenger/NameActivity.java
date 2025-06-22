package com.example.canpay_passenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        EditText etName = findViewById(R.id.et_name);
        Button btnNext = findViewById(R.id.btn_next);
        ImageButton btnBack = findViewById(R.id.btn_back);
        String email = getIntent().getStringExtra("email");

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
                return;
            }

            if (email == null || email.isEmpty()) {
                Toast.makeText(this, "Missing email", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "http://10.0.2.2:8081/api/v1/auth/create-profile";

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("name", name);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    response -> {
                        try {
                            JSONObject profile = response.getJSONObject("profile");
                            String nameFromResponse = profile.getString("name");

                            // Save to SharedPreferences
                            SharedPreferences preferences = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("user_name", nameFromResponse);
                            editor.apply();

                            Toast.makeText(this, "Profile created successfully", Toast.LENGTH_SHORT).show();

                            // Navigate to PIN setup screen
                            Intent intent = new Intent(NameActivity.this, PincodeActivity.class);
                            intent.putExtra("email", email); // pass email forward if needed
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Profile parsing failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    ,
                    error -> {
                        Toast.makeText(this, "Failed to create profile", Toast.LENGTH_SHORT).show();
                        Log.e("CREATE_PROFILE", "Error: " + error.toString());
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);


        });

    }
}

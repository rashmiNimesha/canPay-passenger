package com.example.canpay_passenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PhoneNoActivity extends AppCompatActivity {
    private static final String TAG = "PhoneNoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneno);

        EditText etEmail = findViewById(R.id.et_phone);
        Button btnLogin = findViewById(R.id.btn_login);
        ImageButton btnBack = findViewById(R.id.btn_back);
        TextView tvTermsLink = findViewById(R.id.tv_terms_link);

        // Back button: navigate to LoginActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(PhoneNoActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Terms & Conditions link: open in browser
        tvTermsLink.setOnClickListener(v -> {
            String url = "https://www.termsfeed.com/terms-conditions-generator/";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Enter a valid email address");
                return;
            }

            String baseUrl = getBaseUrl();
            String url = baseUrl + "/api/v1/auth/send-otp";

            Log.d(TAG, "Sending OTP request to: " + url);
            Log.d(TAG, "Email: " + email);

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
            } catch (JSONException e) {
                Log.e(TAG, "JSON creation error", e);
                Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
                return;
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, url, jsonBody,
                    response -> {
                        Log.d(TAG, "OTP sent successfully: " + response.toString());
                        Toast.makeText(PhoneNoActivity.this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PhoneNoActivity.this, OtpActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    },
                    error -> {
                        Log.e(TAG, "OTP send failed", error);
                        handleVolleyError(error);
                    }
            ) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    java.util.Map<String, String> headers = new java.util.HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            // Set timeout (optional)
            jsonObjectRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                    10000, // 10 seconds timeout
                    com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(PhoneNoActivity.this);
            queue.add(jsonObjectRequest);
        });
    }

    private String getBaseUrl() {
        // For Android Studio Emulator:
        return "http://10.0.2.2:8081";

        // For physical device (replace with your computer's IP):
        // return "http://192.168.1.XXX:8081";

    }

    private void handleVolleyError(VolleyError error) {
        String errorMessage = "Unknown error occurred";

        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            Log.e(TAG, "HTTP Status Code: " + statusCode);

            try {
                String responseBody = new String(error.networkResponse.data, "UTF-8");
                Log.e(TAG, "Error Response: " + responseBody);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing error response", e);
            }

            switch (statusCode) {
                case 400:
                    errorMessage = "Bad request - please check your email";
                    break;
                case 404:
                    errorMessage = "Server endpoint not found";
                    break;
                case 500:
                    errorMessage = "Server error - please try again later";
                    break;
                default:
                    errorMessage = "Server error (Code: " + statusCode + ")";
            }
        } else {
            // Network error (no response from server)
            if (error.getCause() instanceof java.net.ConnectException) {
                errorMessage = "Cannot connect to server - check your connection";
            } else if (error.getCause() instanceof java.net.UnknownHostException) {
                errorMessage = "Server not found - check server address";
            } else if (error.getCause() instanceof java.net.SocketTimeoutException) {
                errorMessage = "Request timeout - server might be slow";
            } else {
                errorMessage = "Network error: " + error.getMessage();
            }
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Final error message: " + errorMessage);
    }
}
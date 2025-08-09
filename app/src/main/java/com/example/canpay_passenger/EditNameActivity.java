package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.Endpoints;
import com.example.canpay_passenger.utils.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

public class EditNameActivity extends AppCompatActivity {
    private static final String TAG = "EditNameActivity";
    private static final int RESULT_NAME_UPDATED = 1001;

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

        // Pre-fill with current name
        String currentName = getIntent().getStringExtra("user_name");
        if (currentName != null) {
            etName.setText(currentName);
        }

        btnBack.setOnClickListener(v -> finish());

        btnUpdateName.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        tvError.setVisibility(View.GONE);

        if (TextUtils.isEmpty(name)) {
            showError("Name cannot be empty");
            return;
        }
        if (name.length() < 2) {
            showError("Name must be at least 2 characters");
            return;
        }
        if (name.length() > 50) {
            showError("Name cannot exceed 50 characters");
            return;
        }
        // Regex explanation:
        // Allowed: letters (a-zA-Z), spaces, periods, apostrophes, hyphens
        // No consecutive spaces or special characters sequences
        if (!name.matches("^[a-zA-Z]+([ '.-]?[a-zA-Z]+)*$")) {
            showError("Name contains invalid characters or sequences");
            return;
        }

        updateNameOnBackend(name);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    private void updateNameOnBackend(String newName) {
        String token = PreferenceManager.getToken(this);
        String email = PreferenceManager.getEmail(this);

        Log.d(TAG, "Updating name for email: " + email + ", token: " + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null"));

        if (email == null || token == null) {
            Log.e(TAG, "Missing email or token in SharedPreferences");
            Toast.makeText(this, "User session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("email", email);
            requestBody.put("name", newName);
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON request body", e);
            Toast.makeText(this, "Failed to prepare request", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiHelper.patchJson(this, Endpoints.ACCOUNT_EDIT, requestBody, token, new ApiHelper.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean success = response.optBoolean("success", false);
                    if (success) {
                        JSONObject profile = response.getJSONObject("data").getJSONObject("profile");
                        String updatedName = profile.optString("name", newName);

                        PreferenceManager.setUserName(EditNameActivity.this, updatedName);
                        Log.d(TAG, "Name updated successfully: " + updatedName);

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("new_name", updatedName);
                        setResult(RESULT_NAME_UPDATED, resultIntent);
                        Intent intent = new Intent(EditNameActivity.this, NameUpdateSuccessActivity.class);
                        startActivity(intent);
                    } else {
                        String message = response.optString("message", "Failed to update name");
                        Log.e(TAG, "Server returned success=false: " + message);
                        Intent intent = new Intent(EditNameActivity.this, NameUpdateFailedActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing response", e);
                    Toast.makeText(EditNameActivity.this, "Invalid server response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                Log.e(TAG, "Failed to update name", error);
                ApiHelper.handleVolleyError(EditNameActivity.this, error, TAG);
            }
        });
    }
}

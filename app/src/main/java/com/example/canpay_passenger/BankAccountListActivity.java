package com.example.canpay_passenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankAccountListActivity extends AppCompatActivity implements BankAccountAdapter.OnBankAccountClickListener {

    private static final int ADD_BANK_REQUEST = 1001;
    private RecyclerView recyclerView;
    private BankAccountAdapter adapter;
    private List<BankAccount> bankAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_list);

        // Set up header
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        ImageView btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddNewBankAccountActivity.class);
            startActivityForResult(intent, ADD_BANK_REQUEST);
        });

        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText("Bank Accounts");

        // Set up RecyclerView
        recyclerView = findViewById(R.id.rv_bank_accounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchBankAccounts();

    }

    private void fetchBankAccounts() {
        SharedPreferences prefs = getSharedPreferences("CanPayPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String token = prefs.getString("token", null);

        if (email == null || token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8081/api/v1/user-service/passengers/bank-account?email=" + email;

        com.android.volley.toolbox.JsonObjectRequest request = new com.android.volley.toolbox.JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        handleBankAccountResponse(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Failed to fetch bank accounts", Toast.LENGTH_SHORT).show();
                    Log.e("BANK_FETCH", "Error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void handleBankAccountResponse(JSONArray response) {
        if (bankAccounts == null) {
            bankAccounts = new ArrayList<>();
        } else {
            bankAccounts.clear();
        }

        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
                String bank = obj.getString("bankName");
                String accName = obj.getString("accountHolderName");

                bankAccounts.add(new BankAccount(bank, accName));
            }

            if (adapter == null) {
                adapter = new BankAccountAdapter(bankAccounts, this);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing bank account data", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBankAccountClick(BankAccount account) {
        Intent intent = new Intent(this, EditBankAccountActivity.class);
        intent.putExtra("account_name", account.getAccountName());
        intent.putExtra("bank", account.getBank());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_BANK_REQUEST && resultCode == RESULT_OK) {
            fetchBankAccounts();
        }
    }
}

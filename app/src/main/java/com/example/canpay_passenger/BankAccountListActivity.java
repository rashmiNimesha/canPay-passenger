package com.example.canpay_passenger;

import android.content.Intent;
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
import com.android.volley.VolleyError;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.Endpoints;
import com.example.canpay_passenger.utils.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

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

        bankAccounts = new ArrayList<>();
        adapter = new BankAccountAdapter(bankAccounts, this);
        recyclerView.setAdapter(adapter);

        fetchBankAccounts();
    }

    private void fetchBankAccounts() {
        String token = PreferenceManager.getToken(this);
        if (token == null) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, PhoneNoActivity.class));
            finish();
            return;
        }

        ApiHelper.getJson(this, Endpoints.LOAD_BANK_LIST, token, new ApiHelper.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        handleBankAccountResponse(data);
                    } else {
                        String message = response.optString("message", "Failed to fetch bank accounts");
                        Toast.makeText(BankAccountListActivity.this, message, Toast.LENGTH_SHORT).show();
                        Log.e("BankAccountListActivity", "API error: " + message);
                    }
                } catch (JSONException e) {
                    Log.e("BankAccountListActivity", "Error parsing response: " + e.getMessage());
                    Toast.makeText(BankAccountListActivity.this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                ApiHelper.handleVolleyError(BankAccountListActivity.this, error, "BankAccountListActivity");
            }
        });
    }

    private void handleBankAccountResponse(JSONArray response) {
        bankAccounts.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject obj = response.getJSONObject(i);
//                String bankName = obj.getString("bankName");
//                long accountNumber = obj.getLong("accountNumber");
                String accountName = obj.getString("accountName");
             //   boolean isDefault = obj.getBoolean("default");

                bankAccounts.add(new BankAccount( accountName));
            }
            adapter.notifyDataSetChanged();
            if (bankAccounts.isEmpty()) {
                Toast.makeText(this, "No bank accounts found", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Log.e("BankAccountListActivity", "Error parsing bank account data: " + e.getMessage());
            Toast.makeText(this, "Error parsing bank account data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBankAccountClick(BankAccount account) {
        Intent intent = new Intent(this, EditBankAccountActivity.class);
        intent.putExtra("bank", account.getBank());
        intent.putExtra("accountNumber", account.getAccountNumber());
        intent.putExtra("accountName", account.getAccountName());
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
package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.PreferenceManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private View emptyStateContainer;
    private List<String> transactionsList; // Replace with your actual transaction data type
    private TextView tvCardName, tvBalance, tvCardNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_recent_transactions);
        emptyStateContainer = view.findViewById(R.id.empty_state_container);
        tvCardName = view.findViewById(R.id.tv_card_name);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvCardNumber = view.findViewById(R.id.tv_card_number);

        // Recharge Wallet Button Listener
        Button btnRecharge = view.findViewById(R.id.btn_recharge);
        btnRecharge.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RechargeAmountActivity.class);
            startActivity(intent);
        });

        // Initialize transaction data
        transactionsList = new ArrayList<>();
        loadTransactions();

        // Fetch wallet details from API
        fetchWalletDetails();

        return view;
    }

    private void fetchWalletDetails() {
        String token = PreferenceManager.getToken(getContext());
        if (token == null) {
            Toast.makeText(getContext(), "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), PhoneNoActivity.class));
            if (getActivity() != null) {
                getActivity().finish();
            }
            return;
        }

        ApiHelper.getJson(getContext(), "/api/v1/wallet/balance", token, new ApiHelper.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject data = response.getJSONObject("data");
                        String walletNumber = data.isNull("walletNumber") ? null : data.getString("walletNumber");
                        double balance = data.isNull("balance") ? 0.0 : data.getDouble("balance");
                        String accountName = data.isNull("accountName") ? "N/A" : data.getString("accountName");

                        // Update UI
                        updateWalletUI(walletNumber, balance, accountName);
                    } else {
                        String message = response.optString("message", "Failed to fetch wallet balance");
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        updateWalletUI(null, 0.0, "N/A"); // Fallback UI
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    updateWalletUI(null, 0.0, "N/A"); // Fallback UI
                }
            }

            @Override
            public void onError(VolleyError error) {
                ApiHelper.handleVolleyError(getContext(), error, "HomeFragment");
                Toast.makeText(getContext(), "Failed to fetch wallet details", Toast.LENGTH_SHORT).show();
                updateWalletUI(null, 0.0, "N/A"); // Fallback UI
            }
        });
    }

    private void updateWalletUI(String walletNumber, double balance, String accountName) {
        // Update cardholder name
        tvCardName.setText(accountName != null ? accountName : "N/A");

        // Update balance
        tvBalance.setText(String.format("LKR %.2f", balance));

        // Update card number (show last 4 digits of walletNumber or "N/A")
        if (walletNumber != null && walletNumber.length() >= 4) {
            tvCardNumber.setText("**** " + walletNumber.substring(walletNumber.length() - 4));
        } else {
            tvCardNumber.setText("N/A");
        }
    }

    private void loadTransactions() {
        // Simulate loading data (replace with your actual data loading logic)
        // transactionsList = getTransactionsFromDatabase(); // Your actual method

        // Check if transactions list is empty and show appropriate view
        if (transactionsList.isEmpty()) {
            showEmptyState();
        } else {
            showTransactionsList();
            // Set up your RecyclerView adapter here
            // TransactionAdapter adapter = new TransactionAdapter(transactionsList);
            // recyclerView.setAdapter(adapter);
        }
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyStateContainer.setVisibility(View.VISIBLE);
    }

    private void showTransactionsList() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyStateContainer.setVisibility(View.GONE);
    }
}
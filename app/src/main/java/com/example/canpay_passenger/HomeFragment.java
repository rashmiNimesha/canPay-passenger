package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.VolleyError;
import com.example.canpay_passenger.entity.Transaction;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.Endpoints;
import com.example.canpay_passenger.utils.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private View emptyStateContainer;
    private List<Transaction> transactionsList;
    private TransactionAdapter adapter;
    private TextView tvCardName, tvBalance, tvCardNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_recent_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyStateContainer = view.findViewById(R.id.empty_state_container);
        tvCardName = view.findViewById(R.id.tv_card_name);
        tvBalance = view.findViewById(R.id.tv_balance);
        tvCardNumber = view.findViewById(R.id.tv_card_number);

        // Initialize transaction data
        transactionsList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionsList);
        recyclerView.setAdapter(adapter);

        // Recharge Wallet Button Listener
        Button btnRecharge = view.findViewById(R.id.btn_recharge);
        btnRecharge.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RechargeAmountActivity.class);
            startActivity(intent);
        });

        // Fetch wallet details and transactions
        fetchWalletDetails();
        loadTransactions();

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
                        updateWalletUI(null, 0.0, "N/A");
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    updateWalletUI(null, 0.0, "N/A");
                }
            }

            @Override
            public void onError(VolleyError error) {
                ApiHelper.handleVolleyError(getContext(), error, "HomeFragment");
                Toast.makeText(getContext(), "Failed to fetch wallet details", Toast.LENGTH_SHORT).show();
                updateWalletUI(null, 0.0, "N/A");
            }
        });
    }

    private void updateWalletUI(String walletNumber, double balance, String accountName) {
        tvCardName.setText(accountName != null ? accountName : "N/A");
        tvBalance.setText(String.format("LKR %.2f", balance));
        if (walletNumber != null && walletNumber.length() >= 4) {
            tvCardNumber.setText("**** " + walletNumber.substring(walletNumber.length() - 4));
        } else {
            tvCardNumber.setText("N/A");
        }
    }

    private void loadTransactions() {
        String token = PreferenceManager.getToken(getContext());
        String passengerId = String.valueOf(PreferenceManager.getUserId(getContext()));

        if (token == null || passengerId == null) {
            Toast.makeText(getContext(), "Session expired. Please log in again.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), PhoneNoActivity.class));
            if (getActivity() != null) getActivity().finish();
            return;
        }

        String endpoint = Endpoints.PASSENGER_TRANSACTION_HISTORY + passengerId;
        ApiHelper.getJson(getContext(), endpoint, token, new ApiHelper.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        transactionsList.clear();
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject t = data.getJSONObject(i);
                            // Parse fields from JSON
                            String transactionId = t.optString("transactionId", null);
                            double amount = t.optDouble("amount", 0.0);
                            String happenedAt = t.optString("happenedAt", "");
                            String status = t.optString("status", "");
                            String note = t.optString("note", "");
                            String operatorName = t.optString("operatorName", null);
                            String busNumber = t.optString("busNumber", null);
                            String ownerEmail = t.optString("ownerEmail", null);
                            String type = t.optString("type", "PAYMENT"); // "RECHARGE" or "PAYMENT"

                            // Use the main constructor and set all fields needed by the adapter
                            Transaction transaction = new Transaction(
                                    transactionId,
                                    amount,
                                    happenedAt,
                                    status,
                                    note,
                                    null, // passengerId
                                    null, // passengerName
                                    null, // passengerEmail
                                    type
                            );
                            transaction.setOperatorName(operatorName);
                            transaction.setBusNumber(busNumber);
                            transaction.setOwnerEmail(ownerEmail);

                            transactionsList.add(transaction);
                        }

                        if (transactionsList.isEmpty()) {
                            showEmptyState();
                        } else {
                            showTransactionsList();
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), response.optString("message", "Failed to load transactions"), Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error parsing transaction data", Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            }

            @Override
            public void onError(VolleyError error) {
                ApiHelper.handleVolleyError(getContext(), error, "TransactionLoad");
                showEmptyState();
            }
        });
    }

    public void addTransaction(Transaction transaction) {
        if (transactionsList != null && adapter != null) {
            transactionsList.add(0, transaction);
            adapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);
            showTransactionsList();
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

//package com.example.canpay_passenger;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.canpay_passenger.entity.Transaction;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class HistoryFragment extends Fragment {
//
//    private RecyclerView recyclerView;
//    private LinearLayout emptyStateView;
//    private List<Transaction> transactionList; // Use your actual model
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_history, container, false);
//
//        recyclerView = view.findViewById(R.id.rv_history);
//        emptyStateView = view.findViewById(R.id.history_empty_state);
//
//        // Example: Load transactions (replace with backend/API call)
//        transactionList = loadTransactions();
//
//        if (transactionList.isEmpty()) {
//            recyclerView.setVisibility(View.GONE);
//            emptyStateView.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            emptyStateView.setVisibility(View.GONE);
//
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            recyclerView.setAdapter(new TransactionAdapter(transactionList));
//        }
//
//        return view;
//    }
//
//    // TODO: Replace this with your actual transaction loading logic
//    private List<Transaction> loadTransactions() {
//        List<Transaction> list = new ArrayList<>();
//        // For demo, return empty for empty state, or add sample data for history
//        // Uncomment to test with data:
//        /*
//        list.add(new Transaction("Merchant name", "-2,050.00", "Nov 18, 2024", R.drawable.ic_arrow_red));
//        list.add(new Transaction("Wallet recharge", "+10,000.00", "Nov 18, 2024", R.drawable.ic_arrow_blue));
//        // ... add more as needed
//        */
//        return list;
//    }
//}

package com.example.canpay_passenger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.example.canpay_passenger.entity.Transaction;
import com.example.canpay_passenger.utils.ApiHelper;
import com.example.canpay_passenger.utils.Endpoints;
import com.example.canpay_passenger.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyStateView;
    private List<Transaction> transactionList;
    private TransactionAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.rv_history);
        emptyStateView = view.findViewById(R.id.history_empty_state);
        transactionList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        fetchTransactions();

        return view;
    }

    private void fetchTransactions() {
        String passengerId = PreferenceManager.getUserId(getContext());
        String authToken = PreferenceManager.getToken(getContext());

        if (passengerId == null || authToken == null) {
            ApiHelper.handleVolleyError(getContext(), new VolleyError("User not logged in"), "HistoryFragment");
            showEmptyState();
            return;
        }

        String endpoint = String.format(Endpoints.PAYMENT_HISTORY, passengerId);

        ApiHelper.getJson(getContext(), endpoint, authToken, new ApiHelper.Callback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        transactionList.clear();
                        JSONObject data = response.getJSONObject("data");

                        // Parse recharges
                        JSONArray recharges = data.getJSONArray("recharges");
                        for (int i = 0; i < recharges.length(); i++) {
                            JSONObject recharge = recharges.getJSONObject(i);
                            Transaction transaction = new Transaction(
                                    recharge.getString("transactionId"),
                                    recharge.getDouble("amount"),
                                    recharge.getString("happenedAt"),
                                    recharge.getString("status"),
                                    recharge.getString("note"),
                                    recharge.getString("passengerId"),
                                    recharge.getString("passengerName"),
                                    recharge.getString("passengerEmail"),
                                    "RECHARGE"
                            );

                            transactionList.add(transaction);
                        }

                        // Parse payments
                        JSONArray payments = data.getJSONArray("payments");
                        for (int i = 0; i < payments.length(); i++) {
                            JSONObject payment = payments.getJSONObject(i);
                            Transaction transaction = new Transaction(
                                    payment.getString("transactionId"),
                                    payment.getDouble("amount"),
                                    payment.getString("happenedAt"),
                                    payment.getString("status"),
                                    payment.getString("note"),
                                    payment.getString("passengerId"),
                                    payment.getString("passengerName"),
                                    payment.getString("passengerEmail"),
                                    "PAYMENT"
                            );

                            transactionList.add(transaction);
                        }

                        adapter.notifyDataSetChanged();

                        if (transactionList.isEmpty()) {
                            showEmptyState();
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyStateView.setVisibility(View.GONE);
                        }
                    } else {
                        String message = response.optString("message", "Error fetching transactions");
                        ApiHelper.handleVolleyError(getContext(), new VolleyError(message), "HistoryFragment");
                        showEmptyState();
                    }
                } catch (Exception e) {
                    ApiHelper.handleVolleyError(getContext(), new VolleyError("Error parsing response: " + e.getMessage()), "HistoryFragment");
                    showEmptyState();
                }
            }

            @Override
            public void onError(VolleyError error) {
                ApiHelper.handleVolleyError(getContext(), error, "HistoryFragment");
                showEmptyState();
            }
        });
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyStateView.setVisibility(View.VISIBLE);
    }
}
package com.example.canpay_passenger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyStateContainer;
    private List<String> transactionsList; // Replace with your actual transaction data type

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_recent_transactions);
        emptyStateContainer = view.findViewById(R.id.empty_state_container);

        // Initialize your transaction data (this is where you'd load real data)
        transactionsList = new ArrayList<>();

        // Load transactions and update UI
        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        // This is where you would load your actual transaction data
        // For now, we'll simulate an empty list to show the empty state

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

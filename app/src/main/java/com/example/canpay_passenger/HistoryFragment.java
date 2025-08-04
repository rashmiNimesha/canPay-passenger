package com.example.canpay_passenger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canpay_passenger.entity.Transaction;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayout emptyStateView;
    private List<Transaction> transactionList; // Use your actual model

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.rv_history);
        emptyStateView = view.findViewById(R.id.history_empty_state);

        // Example: Load transactions (replace with backend/API call)
        transactionList = loadTransactions();

        if (transactionList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(new TransactionAdapter(transactionList));
        }

        return view;
    }

    // TODO: Replace this with your actual transaction loading logic
    private List<Transaction> loadTransactions() {
        List<Transaction> list = new ArrayList<>();
        // For demo, return empty for empty state, or add sample data for history
        // Uncomment to test with data:
        /*
        list.add(new Transaction("Merchant name", "-2,050.00", "Nov 18, 2024", R.drawable.ic_arrow_red));
        list.add(new Transaction("Wallet recharge", "+10,000.00", "Nov 18, 2024", R.drawable.ic_arrow_blue));
        // ... add more as needed
        */
        return list;
    }
}

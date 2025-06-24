package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

        // Load accounts (replace with backend call)
        bankAccounts = getBankAccounts();
        adapter = new BankAccountAdapter(bankAccounts, this);
        recyclerView.setAdapter(adapter);
    }

    // Example data
    private List<BankAccount> getBankAccounts() {
        List<BankAccount> list = new ArrayList<>();
        list.add(new BankAccount("Bank of Ceylon - Sehan Weerasekara"));
        list.add(new BankAccount("Commercial Bank - Weerasekara"));
        list.add(new BankAccount("People's Bank - WMSR Weerasekara"));
        list.add(new BankAccount("HSBC - Sehan W"));
        return list;
    }

    @Override
    public void onBankAccountClick(BankAccount account) {
        Intent intent = new Intent(this, EditBankAccountActivity.class);
        intent.putExtra("account_name", account.getName());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Optionally, refresh the list after adding a new bank account
        if (requestCode == ADD_BANK_REQUEST && resultCode == RESULT_OK) {
            // TODO: Reload bank accounts from backend or database
            // For demo, you can add a new dummy account or refresh the adapter
        }
    }
}

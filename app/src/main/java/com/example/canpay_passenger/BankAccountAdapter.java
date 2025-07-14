package com.example.canpay_passenger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.ViewHolder> {
    private List<BankAccount> bankAccounts;
    private OnBankAccountClickListener listener;

    public interface OnBankAccountClickListener {
        void onBankAccountClick(BankAccount account);
    }

    public BankAccountAdapter(List<BankAccount> bankAccounts, OnBankAccountClickListener listener) {
        this.bankAccounts = bankAccounts;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bank_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BankAccount account = bankAccounts.get(position);
//        holder.tvBankName.setText(account.getBank());
        holder.tvAccountName.setText(account.getAccountName());
//        holder.tvAccountNumber.setText(String.valueOf(account.getAccountNumber()));
//        holder.tvDefaultStatus.setText(account.isDefault() ? "Default" : "");
        holder.itemView.setOnClickListener(v -> listener.onBankAccountClick(account));
    }

    @Override
    public int getItemCount() {
        return bankAccounts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBankName, tvAccountName, tvAccountNumber, tvDefaultStatus;

        public ViewHolder(View itemView) {
            super(itemView);
//            tvBankName = itemView.findViewById(R.id.tv_bank_name);
            tvAccountName = itemView.findViewById(R.id.tv_account_holder);
//            tvAccountNumber = itemView.findViewById(R.id.tv_account_number);
//            tvDefaultStatus = itemView.findViewById(R.id.tv_default_status);
        }
    }
}
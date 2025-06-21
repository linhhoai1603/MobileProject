package com.mobile.fe_bankproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.fe_bankproject.R;
import com.mobile.fe_bankproject.dto.TransactionResponse;

public class TransactionAdapter
    extends ListAdapter<TransactionResponse, TransactionAdapter.ViewHolder> {

    public TransactionAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<TransactionResponse> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<TransactionResponse>() {
            @Override
            public boolean areItemsTheSame(
                @NonNull TransactionResponse oldItem,
                @NonNull TransactionResponse newItem) {
                return oldItem.getId().equals(newItem.getId());
            }
            @Override
            public boolean areContentsTheSame(
                @NonNull TransactionResponse oldItem,
                @NonNull TransactionResponse newItem) {
                return oldItem.equals(newItem);
            }
        };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
        @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
        @NonNull ViewHolder holder, int position) {

        TransactionResponse tx = getItem(position);
        holder.tvDate.setText(tx.getTransactionDate());
        holder.tvAmount.setText(
            String.format("%,.0f VND", tx.getAmount()));
        holder.tvDesc.setText(tx.getDescription());
        holder.tvStatus.setText(tx.getStatus());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvAmount, tvDesc, tvStatus;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate   = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDesc   = itemView.findViewById(R.id.tvDesc);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
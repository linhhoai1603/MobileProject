package com.mobile.fe_bankproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.fe_bankproject.R;
import com.mobile.fe_bankproject.dto.BillResponse;
import com.mobile.fe_bankproject.dto.BillStatus;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private List<BillResponse> bills;
    private OnBillClickListener listener;

    public interface OnBillClickListener {
        void onPayClick(BillResponse bill);
    }

    public BillAdapter(List<BillResponse> bills, OnBillClickListener listener) {
        this.bills = bills;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        BillResponse bill = bills.get(position);
        holder.bind(bill);
    }

    @Override
    public int getItemCount() {
        return bills != null ? bills.size() : 0;
    }

    public void updateBills(List<BillResponse> newBills) {
        this.bills = newBills;
        notifyDataSetChanged();
    }

    class BillViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBillCode, tvBillType, tvBillingPeriod, tvUsageAmount, 
                        tvUnitPrice, tvAmount, tvStatus, tvCreatedDate, tvDueDate;
        private Button btnPay;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBillCode = itemView.findViewById(R.id.tvBillCode);
            tvBillType = itemView.findViewById(R.id.tvBillType);
            tvBillingPeriod = itemView.findViewById(R.id.tvBillingPeriod);
            tvUsageAmount = itemView.findViewById(R.id.tvUsageAmount);
            tvUnitPrice = itemView.findViewById(R.id.tvUnitPrice);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCreatedDate = itemView.findViewById(R.id.tvCreatedDate);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            btnPay = itemView.findViewById(R.id.btnPay);
        }

        public void bind(BillResponse bill) {
            // Format currency
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            
            // Set text for each field
            tvBillCode.setText(bill.getBillCode());
            tvBillType.setText(bill.getBillType().toString());
            tvBillingPeriod.setText(bill.getBillingPeriod());
            tvUsageAmount.setText(String.format("%.2f", bill.getUsageAmount()));
            tvUnitPrice.setText(formatter.format(bill.getUnitPrice()));
            tvAmount.setText(formatter.format(bill.getTotalAmount()));
            tvStatus.setText(bill.getBillStatus().toString());
            tvCreatedDate.setText(bill.getCreatedDate());
            tvDueDate.setText(bill.getDueDate());

            // Show pay button only for unpaid bills
            if (bill.getBillStatus() == BillStatus.UNPAID) {
                btnPay.setVisibility(View.VISIBLE);
                btnPay.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onPayClick(bill);
                    }
                });
            } else {
                btnPay.setVisibility(View.GONE);
            }
        }
    }
} 
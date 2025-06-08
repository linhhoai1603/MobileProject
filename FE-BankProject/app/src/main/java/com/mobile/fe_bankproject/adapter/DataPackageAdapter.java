package com.mobile.fe_bankproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.fe_bankproject.R;
import com.mobile.fe_bankproject.dto.DataMobile;

import java.util.List;

public class DataPackageAdapter extends RecyclerView.Adapter<DataPackageAdapter.ViewHolder> {
    private List<DataMobile> dataPackages;
    private OnPackageClickListener listener;

    public void setSelectedPackage(DataMobile dataPackage) {
    }

    public interface OnPackageClickListener {
        void onPackageClick(DataMobile dataPackage);
    }

    public DataPackageAdapter(List<DataMobile> dataPackages, OnPackageClickListener listener) {
        this.dataPackages = dataPackages;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_data_package, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataMobile dataPackage = dataPackages.get(position);
        holder.bind(dataPackage);
    }

    @Override
    public int getItemCount() {
        return dataPackages.size();
    }

    public void updateData(List<DataMobile> newData) {
        this.dataPackages = newData;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPackageName;
        private TextView tvProvider;
        private TextView tvQuantity;
        private TextView tvValidDate;
        private TextView tvPrice;

        ViewHolder(View itemView) {
            super(itemView);
            tvPackageName = itemView.findViewById(R.id.tvPackageName);
            tvProvider = itemView.findViewById(R.id.tvProvider);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvValidDate = itemView.findViewById(R.id.tvValidDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPackageClick(dataPackages.get(position));
                }
            });
        }

        void bind(DataMobile dataPackage) {
            tvPackageName.setText(dataPackage.getPackageName());
            tvProvider.setText(dataPackage.getTelcoProvider().toString());
            tvQuantity.setText(String.format("%dMB", dataPackage.getQuantity()));
            tvValidDate.setText(String.format("%d ngày", dataPackage.getValidDate()));
            tvPrice.setText(String.format("%,dđ", (int)dataPackage.getPrice()).replace(",", "."));
        }
    }
} 
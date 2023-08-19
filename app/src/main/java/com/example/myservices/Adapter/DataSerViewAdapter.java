package com.example.myservices.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myservices.databinding.CardDataItemBinding;
import com.example.myservices.interfaces.ClickAction;
import com.example.myservices.model.Services;

import java.util.List;

public class DataSerViewAdapter extends RecyclerView.Adapter<DataSerViewHolder> {
    List<Services> services;
    private ClickAction reservationClickListener;

    public DataSerViewAdapter(List<Services> services, ClickAction reservationClickListener) {
        this.services = services;
        this.reservationClickListener = reservationClickListener;
    }

    @NonNull
    @Override
    public DataSerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardDataItemBinding binding = CardDataItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DataSerViewHolder(binding,reservationClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DataSerViewHolder holder, int position) {
        holder.setData(services.get(position));

    }

    @Override
    public int getItemCount() {
        return services.size();
    }
}

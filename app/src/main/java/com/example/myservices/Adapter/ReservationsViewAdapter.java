package com.example.myservices.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myservices.databinding.CardReservationItemBinding;
import com.example.myservices.interfaces.OnDeleteClickListener;
import com.example.myservices.model.Reservation;

import java.util.List;

public class ReservationsViewAdapter extends RecyclerView.Adapter<ReservationsViewHolder> {
    List<Reservation> reservations;
    private OnDeleteClickListener deleteClickListener;

    public ReservationsViewAdapter(List<Reservation> reservations, OnDeleteClickListener deleteClickListener) {
        this.reservations = reservations;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ReservationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardReservationItemBinding binding = CardReservationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReservationsViewHolder(binding,deleteClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.setData(reservation);
        holder.binding.deleteIcon.setOnClickListener(delete -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(reservation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }
}

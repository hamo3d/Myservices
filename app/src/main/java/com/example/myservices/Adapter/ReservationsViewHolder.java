package com.example.myservices.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myservices.databinding.CardReservationItemBinding;
import com.example.myservices.interfaces.OnDeleteClickListener;
import com.example.myservices.model.Reservation;

public class ReservationsViewHolder extends RecyclerView.ViewHolder {
    CardReservationItemBinding binding;
    private OnDeleteClickListener deleteClickListener;
    public ReservationsViewHolder(CardReservationItemBinding binding,OnDeleteClickListener deleteClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.deleteClickListener = deleteClickListener;
    }

    public void setData(Reservation reservation) {

        Bitmap decodedBitmap = decodeImage(reservation.getImage());
        binding.imageServ.setImageBitmap(decodedBitmap);
        binding.nameUserTextView.setText(reservation.getNameUser());
        binding.typeServTextView.setText(reservation.getTypeService());
        binding.priceServTextView.setText(reservation.getServicePrice()+"$");
        binding.dateServTextView.setText(reservation.getBookingDate());
        binding.deleteIcon.setOnClickListener(delete -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(reservation);
            }
        });
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}

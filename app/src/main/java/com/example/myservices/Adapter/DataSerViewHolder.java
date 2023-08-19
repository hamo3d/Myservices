package com.example.myservices.Adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myservices.AppController;
import com.example.myservices.databinding.CardDataItemBinding;
import com.example.myservices.interfaces.ClickAction;
import com.example.myservices.model.Services;
import com.example.myservices.prefs.AppShedPreferencesController;

public class DataSerViewHolder extends RecyclerView.ViewHolder {
    CardDataItemBinding binding;
    private ClickAction reservationClickListener;

    public DataSerViewHolder(CardDataItemBinding binding, ClickAction reservationClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        this.reservationClickListener = reservationClickListener;
    }

    public void setData(Services services) {
        binding.nameUserTextView.setText(services.getNameUser());
        binding.nameServicesTextView.setText(services.getServiceName());
        binding.priceServicesTextView.setText(String.valueOf(services.getServicePrice()+"$"));
        Bitmap decodedBitmap = decodeImage(services.getImage());
        binding.image.setImageBitmap(decodedBitmap);
        binding.reservationButton.setOnClickListener(v -> {
            if (reservationClickListener != null) {
                reservationClickListener.onReservationClick(services);
            }

        });
        isCheck();
    }


    private void isCheck () {
        String user = AppShedPreferencesController.getInstance(AppController.getInstance()).getString("type_user");
        if (user != null) {
            if (user.equals("user")){
                binding.reservationButton.setVisibility(View.VISIBLE);
            }else {
                binding.reservationButton.setVisibility(View.INVISIBLE);

            }
        }
    }
    private Bitmap decodeImage(String encodedImage) {
        byte[] imageBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}

package com.example.myservices;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.myservices.databinding.ActivityHomeBinding;
import com.example.myservices.prefs.AppShedPreferencesController;
import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;

    private AppShedPreferencesController preferencesController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setData();
        AndroidThreeTen.init(getApplicationContext());
        preferencesController = AppShedPreferencesController.getInstance(this);


    }


    @Override
    protected void onStart() {
        super.onStart();
        setOnCLickListener();
        String email = preferencesController.getString("user_name");
        binding.tvName.setText(email);
        isCheck();
        String imageUrl = preferencesController.getString("user_image_url");

        Glide.with(this)
                .load(imageUrl)
                .into(binding.IVProfile);


        Log.e("TAGs", "onStart: "+imageUrl );

    }


    private void isCheck () {
        String user = preferencesController.getString("type_user");
        if (user != null) {
            if (user.equals("user")){
               binding.floatingActionButton.setVisibility(View.INVISIBLE);
               binding.button2.setVisibility(View.VISIBLE);
            }else {
                binding.floatingActionButton.setVisibility(View.VISIBLE);
                binding.button2.setVisibility(View.INVISIBLE);


            }
        }
    }


    private void logout(){
        FirebaseAuth.getInstance().signOut();
        preferencesController.clear();
        startActivity(new Intent(this, LoginAccount.class));
        finish();

    }


    private void setData() {
        ArrayList<SlideModel> imageSliders = new ArrayList<>();
        imageSliders.add(new SlideModel(R.drawable.test2, ScaleTypes.FIT));
        imageSliders.add(new SlideModel(R.drawable.test2, ScaleTypes.FIT));
        imageSliders.add(new SlideModel(R.drawable.test3, ScaleTypes.FIT));
        binding.imageSlider.setImageList(imageSliders);
    }


    private void setOnCLickListener() {
        binding.floatingActionButton.setOnClickListener(goToAddSe -> startActivity(new Intent(getApplicationContext(), AddServicesActivity.class)));
        binding.Cleaning.setOnClickListener(click -> startActivity(new Intent(getApplicationContext(), CleaningActivity.class)));
        binding.Repairing.setOnClickListener(click->startActivity(new Intent(getApplicationContext(),RepairingActivity.class)));
        binding.Painting.setOnClickListener(click->startActivity(new Intent(getApplicationContext(),PaintinActivity.class)));
        binding.Laundry.setOnClickListener(click->startActivity(new Intent(getApplicationContext(),LaundryActivity.class)));
        binding.button2.setOnClickListener(click->startActivity(new Intent(getApplicationContext(),ReservationActivity.class)));
        binding.logOutIcon.setOnClickListener(logout->logout());
    }


}
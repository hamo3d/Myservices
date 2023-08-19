package com.example.myservices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myservices.Adapter.MostSarvices;
import com.example.myservices.databinding.ActivityMostPopularServicesBinding;

import java.util.ArrayList;


public class MostPopularServices extends AppCompatActivity {
    ActivityMostPopularServicesBinding binding;
//    AdapterMostServices myadapter;
    ArrayList<MostSarvices>mostSarvices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMostPopularServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MostPopularServices.this,Home.class);
                startActivity(intent);
            }
        });

        mostSarvices=new ArrayList<>();

    mostSarvices.add(new MostSarvices("Ayman","Celining","$"+"20",R.drawable.test3));
    mostSarvices.add(new MostSarvices("samy","Celining","$"+"30",R.drawable.test4));
    mostSarvices.add(new MostSarvices("dhier","Celining","$"+"81",R.drawable.test2));
    mostSarvices.add(new MostSarvices("farag","Celining","$"+"70",R.drawable.test4));
    mostSarvices.add(new MostSarvices("farag","Celining","$"+"70",R.drawable.test4));
    mostSarvices.add(new MostSarvices("farag","Celining","$"+"70",R.drawable.test4));
    mostSarvices.add(new MostSarvices("farag","Celining","$"+"70",R.drawable.test4));



//        myadapter=new AdapterMostServices(mostSarvices);
//        binding.RVMostService.setAdapter(myadapter);
//        binding.RVMostService.setLayoutManager(new LinearLayoutManager(this));
//        binding.RVMostService.setHasFixedSize(true);


    }
}
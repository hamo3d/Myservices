package com.example.myservices;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myservices.databinding.ActivityTestBinding;

import java.util.Objects;

public class TestActivity extends AppCompatActivity {

    private ActivityTestBinding binding;
//    private DataSerViewAdapter adapter;
//    private final List<Services> services = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loading(true);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

//        setupRecyclerView();
//        loadData();
    }

//    private void setupRecyclerView() {
//        adapter = new DataSerViewAdapter(services);
//        binding.rc.setAdapter(adapter);
//        binding.rc.setLayoutManager(new LinearLayoutManager(this));
//    }

//    private void loadData() {
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        database.collection("services")
//                .whereEqualTo("typeService", "cleaning")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                        Services service = documentSnapshot.toObject(Services.class);
//                        services.add(service);
//                        loading(false);
//
//                    }
//                    adapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    loading(false);
//                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }


    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.rc.setVisibility(View.INVISIBLE);
            binding.progressParIcon.setVisibility(View.VISIBLE);
        } else {
            binding.rc.setVisibility(View.VISIBLE);
            binding.progressParIcon.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

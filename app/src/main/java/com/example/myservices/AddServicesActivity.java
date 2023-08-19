package com.example.myservices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myservices.databinding.ActivityAddServicesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class AddServicesActivity extends AppCompatActivity {

    private ActivityAddServicesBinding binding;
    private String encodedImage;
    private final ActivityResultLauncher<Intent> pickedImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.hintImage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private String selectItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSpinner();
        setListeners();
    }

    private void setListeners() {
        binding.createServ.setOnClickListener(s -> {
            if (isValidSignDetails()) {
                isCreating();
            }
        });
        binding.layoutImage.setOnClickListener(s -> {
            checkPermissionsAndPickImage();
        });
    }

    private void isCreating() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, Object> services = new HashMap<>();
        services.put("nameUser", binding.nameInputEditText.getText().toString());
        services.put("serviceName", binding.serviceNameEditText.getText().toString());
        services.put("servicePrice", binding.servicePricEditText.getText().toString());
        services.put("typeService", selectItem);
        services.put("image", encodedImage);

        database.collection("services").add(services).addOnSuccessListener(documentReference -> {
            loading(false);
            onBackPressed();
            showToast("Created Successfully");
        }).addOnFailureListener(error -> {
            loading(false);
            Log.d("TAG", "isCreating: " + error.getMessage());
            showToast(error.getMessage());
        });
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.service_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinner.setAdapter(adapter);

        View spinnerSelectedView = binding.spinner.getSelectedView();
        if (spinnerSelectedView instanceof TextView) {
            ((TextView) spinnerSelectedView).setTextColor(Color.LTGRAY);
        }

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.secondary_text));
                } else {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.primary_text));
                    selectItem = parent.getSelectedItem().toString().toLowerCase();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignDetails() {
        if (encodedImage == null) {
            showToast("Select profile image");
            return false;
        }
        if (selectItem == null) {
            showToast("Select service");
            return false;
        } else if (binding.nameInputEditText.getText().toString().trim().isEmpty()) {
            showToast("Enter your Name");
            return false;
        } else if (binding.serviceNameEditText.getText().toString().trim().isEmpty()) {
            showToast("Enter service");
            return false;
        } else if (binding.servicePricEditText.getText().toString().isEmpty()) {
            showToast("Enter price");
            return false;
        } else {
            return true;
        }
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.createServ.setVisibility(View.INVISIBLE);
            binding.progressParIcon.setVisibility(View.VISIBLE);
        } else {
            binding.createServ.setVisibility(View.VISIBLE);
            binding.progressParIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void checkPermissionsAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickedImage.launch(intent);
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}

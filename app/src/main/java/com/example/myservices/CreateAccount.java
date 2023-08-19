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
import android.util.Patterns;
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

import com.example.myservices.databinding.ActivityCreateAccountBinding;
import com.example.myservices.prefs.AppShedPreferencesController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccount extends AppCompatActivity {
    ActivityCreateAccountBinding binding;
    private FirebaseAuth firebaseAuth;
    private String encodedImage;
    private final ActivityResultLauncher<Intent> pickedImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        // استرداد URI للصورة المختارة من معرض الصور
                        Uri imageUri = result.getData().getData();
                        try {
                            // تحميل الصورة المختارة من معرض الصور وعرضها في ImageView
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.hintImage.setVisibility(View.GONE);
                            // تشفير الصورة إلى Base64
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private AppShedPreferencesController preferencesController;
    private String selectItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ربط المتغيرات بعناصر واجهة المستخدم
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // استرداد تحكم التفضيلات وإعداد Firebase
        preferencesController = AppShedPreferencesController.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        // إعداد قائمة منبثقة للاختيار منها
        setupSpinner();
        // تعيين المستمعين لعناصر واجهة المستخدم
        setListeners();
    }

    private void setListeners() {
        // تعيين المستمعين لعناصر واجهة المستخدم مثل زر العودة وزر الانضمام وصورة الملف الشخصي
        binding.signInTextView.setOnClickListener(v -> onBackPressed());
        binding.SignUpButton.setOnClickListener(s -> {
            // التحقق من صحة تفاصيل التسجيل والبدء في عملية التسجيل
            if (isValidSignDetails()) {
                isSignUp();
            }
        });
        binding.layoutImage.setOnClickListener(s -> {
            // التحقق من الإذن واختيار صورة من معرض الصور
            checkPermissionsAndPickImage();
        });
    }

    private void isSignUp() {
        // بدء عملية التسجيل باستخدام Firebase Authentication
        loading(true);
        String email = binding.emailInputEditText.getText().toString().trim().toLowerCase();
        String password = binding.passwordInputEditText.getText().toString().trim().toLowerCase();
        String name = binding.nameInputEditText.getText().toString().trim().toLowerCase();
        String selectedService = selectItem;
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // نجاح عملية التسجيل، تحميل بيانات المستخدم إلى Firebase
                        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                        uploadUserDataToFirebase(userId, name, email, password, selectedService, encodedImage);
                    } else {
                        // خطأ في عملية التسجيل، عرض رسالة خطأ
                        loading(false);
                        Toast.makeText(this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "uploadUserDataToFirebase: " + task.getException().getLocalizedMessage());
                    }
                });
    }

    private void uploadUserDataToFirebase(String userId, String name, String email, String password, String selectedService, String encodedImage) {
        // تحميل بيانات المستخدم والصورة المشفرة إلى Firebase
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("user_images");
        StorageReference imageRef = storageRef.child(userId + ".jpg");

        byte[] imageData = Base64.decode(encodedImage, Base64.DEFAULT);
        UploadTask uploadTask = imageRef.putBytes(imageData);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // الحصول على رابط الصورة المحملة
                String imageUrl = uri.toString();
                saveUserDataToPreferences(userId, name, email, password, selectedService, imageUrl);
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("email", email);
                userMap.put("name", name);
                userMap.put("service", selectedService);
                userMap.put("profileImage", imageUrl);
                userMap.put("password", password);

                usersRef.child(userId).setValue(userMap)
                        .addOnSuccessListener(aVoid -> {
                            // نجاح تحميل بيانات المستخدم، عرض رسالة نجاح وتوجيه المستخدم
                            loading(false);
                            AppShedPreferencesController.getInstance(getApplicationContext()).putBoolean("isLoggedIn", true);
                            preferencesController.putBoolean("", true);
                            Toast.makeText(this, "تم إنشاء الحساب بنجاح", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, Home.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // فشل تحميل بيانات المستخدم، عرض رسالة خطأ
                            loading(false);
                            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG", "uploadUserDataToFirebase: " + e.toString());
                        });
            });
        }).addOnFailureListener(exception -> {
            // فشل تحميل الصورة إلى Firebase، عرض رسالة خطأ
            loading(false);
            Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", "uploadUserDataToFirebase: " + exception.toString());
        });
    }

    private void saveUserDataToPreferences(String userId, String name, String email, String password, String selectedService, String imageUrl) {
        preferencesController.putString("user_id", userId);
        preferencesController.putString("user_name", name);
        preferencesController.putString("user_email", email);
        preferencesController.putString("user_password", password);
        preferencesController.putString("type_user", selectedService);
        preferencesController.putString("user_image_url", imageUrl); // Store the imageUrl
    }

    private Boolean isValidSignDetails() {
        if (encodedImage == null) {
            showToast("Select profile image");
            return false;
        } else if (binding.nameInputEditText.getText().toString().trim().isEmpty()) {
            showToast("Enter your Name");
            return false;
        } else if (binding.emailInputEditText.getText().toString().trim().isEmpty()) {
            showToast("Enter your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailInputEditText.getText().toString()).matches()) {
            showToast("Must email valid");
            return false;
        } else if (binding.passwordInputEditText.getText().toString().isEmpty()) {
            showToast("Enter password ");
            return false;

        } else if (binding.confirmPasswordInputEditText.getText().toString().trim().isEmpty()) {
            showToast("Enter confirmPassword");
            return false;
        } else if (!binding.confirmPasswordInputEditText.getText().toString().equals(binding.passwordInputEditText.getText().toString())) {
            showToast("Must confirmPassword soma password");
            return false;
        }
        if (selectItem == null) {
            showToast("Select service");
            return false;
        } else
            return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.SignUpButton.setVisibility(View.INVISIBLE);
            binding.progressParIcon.setVisibility(View.VISIBLE);
        } else {
            binding.SignUpButton.setVisibility(View.VISIBLE);
            binding.progressParIcon.setVisibility(View.INVISIBLE);
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

    private void checkPermissionsAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
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

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.account_type,
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
}
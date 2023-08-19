package com.example.myservices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myservices.databinding.ActivityLoginAccountBinding;
import com.example.myservices.prefs.AppShedPreferencesController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginAccount extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 1;
    private ActivityLoginAccountBinding binding;
    private FirebaseAuth firebaseAuth;
    private AppShedPreferencesController preferencesController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // تهيئة واجهة المستخدم
        preferencesController = AppShedPreferencesController.getInstance(this);
        preferencesController.putBoolean("isFirstTime", true);

        firebaseAuth = FirebaseAuth.getInstance();

        // تعيين الإستماع لأزرار الواجهة
        binding.BtnSignin.setOnClickListener(this);
        binding.tvSginin.setOnClickListener(this);
        binding.tvGoogle.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // تم تسجيل الدخول بالفعل، يمكنك تنفيذ الإجراءات المناسبة هنا
        }
    }

    // دالة لتسجيل الدخول للمستخدم
    private void userLogin() {
        String email = binding.edEmail.getText().toString().trim();
        String password = binding.edPassword.getText().toString().trim();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                onLoginSuccess();
                preferencesController.putBoolean("isLoggedIn", true);

            } else {
                loading(false);

                Toast.makeText(LoginAccount.this, Objects.requireNonNull(task.getException())
                        .getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // دالة للتعامل مع نجاح عملية تسجيل الدخول
    private void onLoginSuccess() {
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // استخراج معلومات المستخدم من قاعدة البيانات
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);
                    String selectedService = snapshot.child("service").getValue(String.class);
                    String imageUrl = snapshot.child("profileImage").getValue(String.class);

                    // حفظ معلومات المستخدم في الذاكرة المؤقتة
                    saveUserDataToPreferences(userId, name, email, password, selectedService, imageUrl);

                    // تحميل الصفحة الرئيسية
                    loading(false);
                    finish();
                    startActivity(new Intent(LoginAccount.this, Home.class));
                    Toast.makeText(LoginAccount.this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading(false);
                Toast.makeText(LoginAccount.this, error.toString(), Toast.LENGTH_SHORT).show();
                // معالجة خطأ في قراءة بيانات المستخدم من Firebase Database
            }
        });
    }

    // دالة لحفظ معلومات المستخدم في الذاكرة المؤقتة
    private void saveUserDataToPreferences(String userId, String name, String email, String password, String selectedService, String imageUrl) {
        preferencesController.putString("user_id", userId);
        preferencesController.putString("user_name", name);
        preferencesController.putString("user_email", email);
        preferencesController.putString("user_password", password);
        preferencesController.putString("type_user", selectedService);
        preferencesController.putString("user_image_url", imageUrl);
    }

    // دالة لإظهار أو إخفاء شاشة التحميل
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.BtnSignin.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.BtnSignin.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }

    }

    private boolean check () {
        if (binding.edPassword.getText().toString().isEmpty() && binding.edEmail.getText().toString().isEmpty()) {
            Toast.makeText(this, "enter data", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            loading(true);
            userLogin();
        }
        return  true;
    }





    @Override
    public void onClick(View view) {
        if (view == binding.BtnSignin) {

      check();
        }
        if (view == binding.tvSginin) {
            startActivity(new Intent(this, CreateAccount.class));
        }
        if (view == binding.tvGoogle) {
            // قم بتنفيذ الإجراءات الخاصة بتسجيل الدخول باستخدام حساب Google
        }
    }

    // التعامل مع الضغط على زر العودة
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
}

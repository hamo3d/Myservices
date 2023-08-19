package com.example.myservices;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import com.example.myservices.databinding.ActivityCreateAccountBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Collections;
import java.util.Objects;

public class CreateAccount extends AppCompatActivity implements View.OnClickListener {
    ActivityCreateAccountBinding binding;
   private FirebaseAuth firebaseAuth;
   private CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 1;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        binding.BtnSignup.setOnClickListener(this);
        binding.tvSginup.setOnClickListener(this);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){
            startActivity(new Intent(this,Login.class));
        }

        mCallbackManager = CallbackManager.Factory.create();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser =firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, Login.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                progressDialog.setMessage("جاري تسجيل الدخول...");
                progressDialog.show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException ignored) {
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                startActivity(new Intent(this, Login.class));
                Toast.makeText(this, "تم تسجيل الدخول بنجاح", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(){
        String email = binding.edEmail.getText().toString().trim();
        String password = binding.edPassword.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "من فضلك أدخل البريد الإلكتروني",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.matches(emailPattern)){
            Toast.makeText(this, "من فضلك أدخل بريد إلكتروني صحيح",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "من فضلك أدخل كلمة السر",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("جاري إنشاء الحساب...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                Toast.makeText(CreateAccount.this, "تم إنشاء الحساب بنجاح",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, Login.class));
            } else if (password.length() < 6) {
                Toast.makeText(this, "يجب أن تكون كلمة السر أكبر من 6",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
            else {
//                Toast.makeText(SignUpActivity.this, "هذا الحساب مسجل بالفعل أو أن هناك خطأ في الإتصال بالإنترنت",Toast.LENGTH_LONG).show();
                Toast.makeText(CreateAccount.this, Objects.requireNonNull(task.getException())
                        .getLocalizedMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if (v == binding.BtnSignup) {
            registerUser();
        }
        if(v == binding.tvSginup) {
            startActivity(new Intent(this, LoginAccount.class));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    moveTaskToBack(true);
    startActivity(new Intent(this,Login.class));
    }
}
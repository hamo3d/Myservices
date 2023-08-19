package com.example.myservices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myservices.prefs.AppShedPreferencesController;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        boolean isFirstTime = AppShedPreferencesController.getInstance(getApplicationContext()).getBoolean("isFirstTime");
        if (!isFirstTime) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent onboardingIntent = new Intent(WelcomeScreen.this, Onbording.class);
                    startActivity(onboardingIntent);
                    AppShedPreferencesController.getInstance(getApplicationContext()).putBoolean("isFirstTime", false);
                    finish();
                }
            }, 3000); // تأخير لمدة 3 ثوانٍ (3000 مللي ثانية)
        } else {
            handleSplashActivity();
        }
    }

    public void handleSplashActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLoggedIn = AppShedPreferencesController.getInstance(getApplicationContext()).getBoolean("isLoggedIn");
                Intent intent = new Intent(WelcomeScreen.this, isLoggedIn ? Home.class : LoginAccount.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }
}




package com.example.myservices.prefs;

import android.content.Context;
import android.content.SharedPreferences;



public class AppShedPreferencesController {

    public static AppShedPreferencesController instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    private AppShedPreferencesController(Context context) {
        sharedPreferences = context.getSharedPreferences("myapp", Context.MODE_PRIVATE);
    }

    public static synchronized AppShedPreferencesController getInstance(Context context) {
        if (instance == null) {
            instance = new AppShedPreferencesController(context);
        }
        return instance;
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void clear() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}

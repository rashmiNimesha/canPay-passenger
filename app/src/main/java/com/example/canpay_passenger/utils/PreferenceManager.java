package com.example.canpay_passenger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PreferenceManager {
    private static final String TAG = "PreferenceManager";
    private static final String PREF_NAME = "CanPayPrefs";

    private static SharedPreferences getEncryptedPrefs(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Failed to initialize EncryptedSharedPreferences", e);
            throw new RuntimeException("Failed to initialize encrypted preferences", e);
        }
    }

    public static void saveUserSession(Context context, String email, String token, String role, String userName, int userId, String nic) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            SharedPreferences.Editor editor = prefs.edit();
            if (email != null) editor.putString("email", email);
            if (token != null) editor.putString("token", token);
            if (role != null) editor.putString("role", role);
            if (userName != null) editor.putString("user_name", userName);
            if (userId != 0) editor.putInt("user_id", userId);
            if (nic != null) editor.putString("nic", nic);
            editor.apply();
            Log.d(TAG, "Saved user session: email=" + email + ", token=" + token + ", role=" + role);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user session", e);
            throw new RuntimeException("Failed to save user session", e);
        }
    }

    public static String getEmail(Context context) {
        String email = getEncryptedPrefs(context).getString("email", null);
        Log.d(TAG, "Retrieved email: " + email);
        return email;
    }

    public static String getToken(Context context) {
        String token = getEncryptedPrefs(context).getString("token", null);
        Log.d(TAG, "Retrieved token: " + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "null"));
        return token;
    }

    public static String getRole(Context context) {
        String role = getEncryptedPrefs(context).getString("role", null);
        Log.d(TAG, "Retrieved role: " + role);
        return role;
    }

    public static String getUserName(Context context) {
        String userName = getEncryptedPrefs(context).getString("user_name", "User");
        Log.d(TAG, "Retrieved user_name: " + userName);
        return userName;
    }

    public static int getUserId(Context context) {
        int userId = getEncryptedPrefs(context).getInt("user_id", 0);
        Log.d(TAG, "Retrieved user_id: " + userId);
        return userId;
    }

    public static String getNic(Context context) {
        String nic = getEncryptedPrefs(context).getString("nic", null);
        Log.d(TAG, "Retrieved nic: " + nic);
        return nic;
    }

    public static void clearSession(Context context) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            prefs.edit().clear().apply();
            Log.d(TAG, "Cleared user session");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing user session", e);
        }
    }

}
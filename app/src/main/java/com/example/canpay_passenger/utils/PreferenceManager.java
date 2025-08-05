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
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NIC = "nic";
    private static final String KEY_HMAC_SECRET = "hmac_secret";

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

    public static void saveUserSession(Context context, String email, String token, String role, String userName, String nic, String userId) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            SharedPreferences.Editor editor = prefs.edit();
            if (email != null) editor.putString(KEY_EMAIL, email);
            if (token != null) editor.putString("token", token);
            if (role != null) editor.putString("role", role);
            if (userName != null) editor.putString(KEY_USER_NAME, userName);
            if (userId != null) editor.putString("id", userId);
            if (nic != null) editor.putString(KEY_NIC, nic);
            editor.apply();
            Log.d(TAG, "Saved user session: email=" + email + ", token=" + token + ", role=" + role);
        } catch (Exception e) {
            Log.e(TAG, "Error saving user session", e);
            throw new RuntimeException("Failed to save user session", e);
        }
    }

    public static void saveHmacSecret(Context context, String hmacSecret) {
        SharedPreferences prefs = getEncryptedPrefs(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_HMAC_SECRET, hmacSecret);
        editor.apply();
        Log.d(TAG, "Saved HMAC secret");
    }

    public static String getEmail(Context context) {
        String email = getEncryptedPrefs(context).getString(KEY_EMAIL, null);
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
        String userName = getEncryptedPrefs(context).getString(KEY_USER_NAME, "User");
        Log.d(TAG, "Retrieved user_name: " + userName);
        return userName;
    }

    public static String getUserId(Context context) {
        return getEncryptedPrefs(context).getString("id", null);
    }

    public static String getNic(Context context) {
        String nic = getEncryptedPrefs(context).getString(KEY_NIC, null);
        Log.d(TAG, "Retrieved nic: " + nic);
        return nic;
    }

    public static String getHmacSecret(Context context) {
        String secret = getEncryptedPrefs(context).getString(KEY_HMAC_SECRET, null);
        Log.d(TAG, "Retrieved HMAC secret: " + (secret != null ? "***" : "null"));
        return secret;
    }

    public static void setUserName(Context context, String userName) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_USER_NAME, userName);
            editor.apply();
            Log.d(TAG, "Set user_name: " + userName);
        } catch (Exception e) {
            Log.e(TAG, "Error setting user_name", e);
            throw new RuntimeException("Failed to set user name", e);
        }
    }

    public static void setEmail(Context context, String email) {
        try {
            SharedPreferences prefs = getEncryptedPrefs(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_EMAIL, email);
            editor.apply();
            Log.d(TAG, "Set email: " + email);
        } catch (Exception e) {
            Log.e(TAG, "Error setting email", e);
            throw new RuntimeException("Failed to set email", e);
        }
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
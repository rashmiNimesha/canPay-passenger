package com.example.canpay_passenger.utils;

import android.util.Base64;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtils {
    public static String hmacSha256(String secret, String message) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
            // Convert to Base64 and apply URL-safe transformations
            String base64 = Base64.encodeToString(hash, Base64.NO_WRAP);
            // Mimic Postman's URL-safe Base64: replace + with -, / with _, remove = padding
            base64 = base64.replace('+', '-').replace('/', '_').replaceAll("=+$", "");
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
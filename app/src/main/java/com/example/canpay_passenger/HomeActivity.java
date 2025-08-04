package com.example.canpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.canpay_passenger.utils.PreferenceManager;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import android.Manifest;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int QR_SCAN_REQUEST_CODE = 102;

    private TextView tvGreeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        FloatingActionButton fabQrScan = findViewById(R.id.fab_qr_scan);
        tvGreeting = findViewById(R.id.tv_greeting);

        // Notifications icon click listener
        ImageView ivNotifications = findViewById(R.id.iv_notifications);
        ivNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Profile icon click listener
        ImageView ivProfile = findViewById(R.id.iv_profile);
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Bottom navigation switching
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            if (item.getItemId() == R.id.nav_home) {
                selected = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_history) {
                selected = new HistoryFragment();
            }
            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
                return true;
            }
            return false;
        });

        // QR Scan FAB
        fabQrScan.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                startQrScanner();
            }
        });

        updateGreeting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGreeting();
    }

    private void updateGreeting() {
        String userName = PreferenceManager.getUserName(this);
        tvGreeting.setText("Hi, " + userName);
        Log.d(TAG, "Updated greeting with username: " + userName);
    }

    public void startRechargeActivity() {
        String email = PreferenceManager.getEmail(this);
        if (email != null) {
            Intent intent = new Intent(this, RechargeAmountActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void startQrScanner() {
        Intent intent = new Intent(this, QrScanActivity.class);
        startActivityForResult(intent, QR_SCAN_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQrScanner();
            } else {
                Toast.makeText(this, "Camera permission is required for QR scanning", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            String qrResult = data.getStringExtra("QR_RESULT");
            Intent intent = new Intent(this, EnterAmountActivity.class);
            intent.putExtra("qr_code", qrResult);
            startActivity(intent);
        }
    }
}
